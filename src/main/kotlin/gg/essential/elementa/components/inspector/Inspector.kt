package gg.essential.elementa.components.inspector

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.utils.ObservableAddEvent
import gg.essential.elementa.utils.ObservableClearEvent
import gg.essential.elementa.utils.ObservableRemoveEvent
import gg.essential.elementa.utils.elementaDebug
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.NumberFormat

class Inspector @JvmOverloads constructor(
    rootComponent: UIComponent,
    backgroundColor: Color = Color(40, 40, 40),
    outlineColor: Color = Color(20, 20, 20),
    outlineWidth: Float = 2f,
    maxSectionHeight: HeightConstraint? = null
) : UIContainer() {
    private val rootNode = componentToNode(rootComponent)
    private val treeBlock: UIContainer
    private var TreeListComponent: TreeListComponent
    internal val container: UIComponent
    internal var selectedNode: InspectorNode? = null
        private set
    private val infoBlockScroller: ScrollComponent
    private val separator1: UIBlock
    private val separator2: UIBlock

    private var clickPos: Pair<Float, Float>? = null
    private val outlineEffect = OutlineEffect(outlineColor, outlineWidth, drawAfterChildren = true)

    private var isClickSelecting = false

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        container = UIBlock(backgroundColor).constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        } effect outlineEffect childOf this

        val titleBlock = UIContainer().constrain {
            x = CenterConstraint()
            width = ChildBasedSizeConstraint() + 30.pixels()
            height = ChildBasedMaxSizeConstraint() + 20.pixels()
        }.onMouseClick {
            clickPos =
                if (it.relativeX < 0 || it.relativeY < 0 || it.relativeX > getWidth() || it.relativeY > getHeight()) {
                    null
                } else {
                    it.relativeX to it.relativeY
                }
        }.onMouseRelease {
            clickPos = null
        }.onMouseDrag { mouseX, mouseY, button ->
            if (clickPos == null)
                return@onMouseDrag

            if (button == 0) {
                this@Inspector.constrain {
                    x = (this@Inspector.getLeft() + mouseX - clickPos!!.first).pixels()
                    y = (this@Inspector.getTop() + mouseY - clickPos!!.second).pixels()
                }
            }
        } childOf container

        val title = UIText("Inspector").constrain {
            x = 10.pixels()
            y = CenterConstraint()
            width = TextAspectConstraint()
            height = 14.pixels()
        } childOf titleBlock

        UIImage.ofResourceCached("/textures/inspector/click.png").constrain {
            x = SiblingConstraint(10f)
            y = CenterConstraint()
            width = AspectConstraint(1f)
            height = 12.pixels
        }.apply {
            textureMinFilter = UIImage.TextureScalingMode.LINEAR
        }.onMouseClick { event ->
            event.stopPropagation()
            isClickSelecting = true

            val rootWindow = Window.of(this)
            rootWindow.clickInterceptor = { mouseX, mouseY, _ ->
                rootWindow.clickInterceptor = null
                isClickSelecting = false

                val targetComponent = getClickSelectTarget(mouseX.toFloat(), mouseY.toFloat())
                if (targetComponent != null) {
                    findAndSelect(targetComponent)
                }
                true
            }
        } childOf titleBlock

        separator1 = UIBlock(outlineColor).constrain {
            y = SiblingConstraint()
            height = 2.pixels()
        } childOf container

        treeBlock = UIContainer().constrain {
            width = ChildBasedSizeConstraint() + 10.pixels()
            height = ChildBasedSizeConstraint() + 10.pixels()
        }

        val treeBlockScroller = ScrollComponent().constrain {
            y = SiblingConstraint()
            width = RelativeConstraint(1f) boundTo treeBlock
            height = RelativeConstraint(1f).boundTo(treeBlock) coerceAtMost (maxSectionHeight
                ?: RelativeWindowConstraint(1 / 3f))
        } childOf container

        treeBlock childOf treeBlockScroller

        TreeListComponent = TreeListComponent(rootNode).constrain {
            x = 5.pixels()
            y = SiblingConstraint() + 5.pixels()
        } childOf treeBlock

        separator2 = UIBlock(outlineColor).constrain {
            y = SiblingConstraint()
            height = 2.pixels()
        }

        val infoBlock = InfoBlock(this).constrain {
            y = SiblingConstraint()
            width = ChildBasedMaxSizeConstraint() + 10.pixels()
            height = ChildBasedSizeConstraint() + 10.pixels()
        }

        infoBlockScroller = ScrollComponent().constrain {
            y = SiblingConstraint()
            width = RelativeConstraint(1f) boundTo infoBlock
            height = RelativeConstraint(1f) boundTo infoBlock coerceAtMost (maxSectionHeight
                ?: RelativeWindowConstraint(1 / 3f))
        }

        infoBlock childOf infoBlockScroller
    }

    private fun componentToNode(component: UIComponent): InspectorNode {
        val node = InspectorNode(this, component).withChildren {
            component.children.forEach {
                if (it != this@Inspector)
                    add(componentToNode(it))
            }
        } as InspectorNode

        component.children.addObserver { _, event ->
            val (index, childComponent) = when (event) {
                is ObservableAddEvent<*> -> event.element
                is ObservableRemoveEvent<*> -> event.element
                is ObservableClearEvent<*> -> {
                    node.clearChildren()
                    return@addObserver
                }
                else -> return@addObserver
            }

            // We do not want to show the inspector itself
            if (childComponent == this) {
                return@addObserver
            }

            // So we also need to offset any indices after it
            val offset = -(0 until index).count { component.children[it] == this }

            when (event) {
                is ObservableAddEvent<*> -> {
                    val childNode = componentToNode(childComponent as UIComponent)
                    node.addChildAt(index + offset, childNode)
                }
                is ObservableRemoveEvent<*> -> node.removeChildAt(index + offset)
            }
        }

        return node
    }

    internal fun setSelectedNode(node: InspectorNode?) {
        if (node == null) {
            container.removeChild(separator2)
            container.removeChild(infoBlockScroller)
        } else if (selectedNode == null) {
            separator2 childOf container
            infoBlockScroller childOf container
        }
        selectedNode = node
    }

    private fun getClickSelectTarget(mouseX: Float, mouseY: Float): UIComponent? {
        val rootComponent = rootNode.targetComponent
        val hitComponent = (rootComponent as? Window)?.hoveredFloatingComponent?.hitTest(mouseX, mouseY)
            ?: rootComponent.hitTest(mouseX, mouseY)

        return if (hitComponent == this || hitComponent.isChildOf(this)) null
        else hitComponent
    }

    internal fun findAndSelect(component: UIComponent) {
        fun findNodeAndExpandParents(component: UIComponent): InspectorNode? {
            if (component == rootNode.targetComponent) {
                return rootNode
            }
            if (component.parent == component) {
                return null
            }
            val parentNode = findNodeAndExpandParents(component.parent) ?: return null
            val parentDisplay = parentNode.displayComponent
            parentDisplay.opened = true
            return parentDisplay.childNodes.filterIsInstance<InspectorNode>().find { it.targetComponent == component }
        }

        val node = findNodeAndExpandParents(component) ?: return
        if (selectedNode != node) {
            node.toggleSelection()
        }
    }

    private fun UIComponent.isMounted(): Boolean =
        parent == this || (this in parent.children && parent.isMounted())

    override fun animationFrame() {
        super.animationFrame()

        // Make sure we are the top-most component (last to draw and first to receive input)
        Window.enqueueRenderOperation {
            setFloating(false)
            if (isMounted()) { // only if we are still mounted
                setFloating(true)
            }
        }
    }

    override fun draw(matrixStack: UMatrixStack) {
        // If we got removed from our parent, we need to un-float ourselves
        if (!isMounted()) {
            Window.enqueueRenderOperation { setFloating(false) }
            return
        }

        separator1.setWidth(container.getWidth().pixels())
        separator2.setWidth(container.getWidth().pixels())

        if (isClickSelecting) {
            val (mouseX, mouseY) = getMousePosition()
            getClickSelectTarget(mouseX, mouseY)
        } else {
            selectedNode?.targetComponent
        }?.also { component ->
            val scissors = generateSequence(component) { if (it.parent != it) it.parent else null }
                .flatMap { it.effects.filterIsInstance<ScissorEffect>().asReversed() }
                .toList()
                .reversed()

            val x1 = component.getLeft().toDouble()
            val y1 = component.getTop().toDouble()
            val x2 = component.getRight().toDouble()
            val y2 = component.getBottom().toDouble()

            // Clear the depth buffer cause we will be using it to draw our outside-of-scissor-bounds block
            UGraphics.glClear(GL11.GL_DEPTH_BUFFER_BIT)

            // Draw a highlight on the element respecting its scissor effects
            scissors.forEach { it.beforeDraw(matrixStack) }
            UIBlock.drawBlock(matrixStack, Color(129, 212, 250, 100), x1, y1, x2, y2)
            scissors.asReversed().forEach { it.afterDraw(matrixStack) }

            // Then draw another highlight (with depth testing such that we do not overwrite the previous one)
            // which does not respect the scissor effects and thereby indicates where the element is drawn outside of
            // its scissor bounds.
            UGraphics.enableDepth()
            UGraphics.depthFunc(GL11.GL_LESS)
            ElementaVersion.v0.enableFor { // need the custom depth testing
                UIBlock.drawBlock(matrixStack, Color(255, 100, 100, 100), x1, y1, x2, y2)
            }
            UGraphics.depthFunc(GL11.GL_LEQUAL)
            UGraphics.disableDepth()
        }

        val debugState = elementaDebug
        elementaDebug = false
        try {
            super.draw(matrixStack)
        } finally {
            elementaDebug = debugState
        }
    }

    companion object {
        internal val percentFormat: NumberFormat = NumberFormat.getPercentInstance()
    }
}
