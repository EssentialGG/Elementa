package club.sk1er.elementa.components.inspector

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.elementa.utils.ObservableAddEvent
import club.sk1er.elementa.utils.ObservableClearEvent
import club.sk1er.elementa.utils.ObservableRemoveEvent
import club.sk1er.mods.core.universal.UMouse
import club.sk1er.mods.core.universal.UResolution
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
    private val container: UIComponent
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
            clickPos = if (it.relativeX < 0 || it.relativeY < 0 || it.relativeX > getWidth() || it.relativeY > getHeight()) {
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

        // TODO: Fix click selecting
//        SVGComponent.ofResource("/svg/click.svg").constrain {
//            x = SiblingConstraint(10f)
//            y = CenterConstraint()
//            width = AspectConstraint(1f)
//            height = RelativeConstraint(1f).to(title) as HeightConstraint
//        }.onMouseClick { event ->
//            event.stopPropagation()
//            isClickSelecting = true
//        } childOf titleBlock

        separator1 = UIBlock(outlineColor).constrain {
            y = SiblingConstraint()
            height = 2.pixels()
        } childOf container

        treeBlock = UIContainer().constrain {
            width = ChildBasedSizeConstraint() + 10.pixels()
            height = ChildBasedSizeConstraint() + 10.pixels()
        }

        val rootWindow = Window.of(rootComponent)

        val treeBlockScroller = ScrollComponent().constrain {
            y = SiblingConstraint()
            width = RelativeConstraint(1f).to(treeBlock) as WidthConstraint
            height = RelativeConstraint(1f).to(treeBlock) max (maxSectionHeight ?: RelativeConstraint(1 / 3f).to(rootWindow))
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
            width = RelativeConstraint(1f).to(infoBlock) as WidthConstraint
            height = RelativeConstraint(1f).to(infoBlock) max (maxSectionHeight ?: RelativeConstraint(1 / 3f).to(rootWindow))
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
            when (event) {
                is ObservableAddEvent<*> -> {
                    val newComponent = event.element.value as UIComponent
                    if (newComponent != this)
                        node.addChild(InspectorNode(this, newComponent))
                }
                is ObservableRemoveEvent<*> -> node.removeChildAt(event.element.index)
                is ObservableClearEvent<*> -> node.clearChildren()
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

    override fun draw() {
        separator1.setWidth(container.getWidth().pixels())
        separator2.setWidth(container.getWidth().pixels())

        if (isClickSelecting) {
            val scaledHeight = UResolution.scaledHeight
            val mouseX = UMouse.getScaledX().toFloat()
            val mouseY = scaledHeight - UMouse.getTrueY().toFloat() * scaledHeight / UResolution.windowHeight - 1f
            val hitComponent = rootNode.targetComponent.hitTest(mouseX, mouseY)

            // TODO: Implement some kind of way to hook into a UIComponent to intercept events,
            //  allowing for us to stop clicks when in the selecting mode.
            if (hitComponent == this || hitComponent.isChildOf(this)) null
            else hitComponent
        } else {
            selectedNode?.targetComponent
        }?.also {
            UIBlock.drawBlock(
                Color(129, 212, 250, 100),
                it.getLeft().toDouble(),
                it.getTop().toDouble(),
                it.getRight().toDouble(),
                it.getBottom().toDouble()
            )
        }

        super.draw()
    }

    companion object {
        internal val percentFormat: NumberFormat = NumberFormat.getPercentInstance()
    }
}
