package gg.essential.elementa.components.inspector

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.components.inspector.display.awt.AwtInspectorDisplay
import gg.essential.elementa.components.inspector.display.glfw.GLFWDisplay
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.font.DefaultFonts
import gg.essential.elementa.impl.ExternalInspectorDisplay
import gg.essential.elementa.impl.Platform
import gg.essential.elementa.manager.ResolutionManager
import gg.essential.elementa.utils.ObservableAddEvent
import gg.essential.elementa.utils.ObservableClearEvent
import gg.essential.elementa.utils.ObservableRemoveEvent
import gg.essential.elementa.utils.elementaDebug
import gg.essential.universal.UGraphics
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMatrixStack
import org.jetbrains.annotations.ApiStatus
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.NumberFormat

class Inspector @JvmOverloads constructor(
    private val rootComponent: UIComponent,
    backgroundColor: Color = Color(40, 40, 40),
    outlineColor: Color = Color(20, 20, 20),
    outlineWidth: Float = 2f,
    maxSectionHeight: HeightConstraint? = null,
) : UIContainer() {


    private val inspectorContent by InspectorContent()
    private val displayManager = DisplayManager()
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
    private val drawObserver by Overlay()

    private var measuringDistance = false

    private var acceptingKeyboardInput = true

    private val keyTypeListener: UIComponent.(typedChar: Char, keyCode: Int) -> Unit = { _, keyCode ->
        if (keyCode == UKeyboard.KEY_F12) {
            acceptingKeyboardInput = !acceptingKeyboardInput
        }
        if (acceptingKeyboardInput) {
            when (keyCode) {
                UKeyboard.KEY_M -> {
                    measuringDistance = !measuringDistance
                }
                UKeyboard.KEY_S -> {
                    openComponentSelector()
                }
                UKeyboard.KEY_C -> {
                    infoBlock.openConstraintsTab()
                }
                UKeyboard.KEY_V -> {
                    infoBlock.openValuesTab()
                }
                UKeyboard.KEY_B -> {
                    infoBlock.openStatesTab()
                }
                UKeyboard.KEY_D -> {
                    elementaDebug = !elementaDebug
                }
            }
        }
    }

    private val infoBlock by InfoBlock(this).constrain {
        y = SiblingConstraint()
        width = ChildBasedMaxSizeConstraint() + 10.pixels()
        height = ChildBasedSizeConstraint() + 10.pixels()
    }

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        container = UIBlock(backgroundColor).constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        } effect outlineEffect childOf inspectorContent

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
                inspectorContent.constrain {
                    x = (inspectorContent.getLeft() + mouseX - clickPos!!.first).pixels()
                    y = (inspectorContent.getTop() + mouseY - clickPos!!.second).pixels()
                }
            }
        } childOf container

        val title = UIText("Inspector").constrain {
            x = 10.pixels()
            y = CenterConstraint()
            width = TextAspectConstraint()
            height = 14.pixels()
        } childOf titleBlock

        SVGComponent.ofResource("/svg/click.svg").constrain {
            x = SiblingConstraint(10f)
            y = CenterConstraint()
            width = AspectConstraint(1f)
            height = RelativeConstraint(1f).to(title) as HeightConstraint
        }.onMouseClick { event ->
            event.stopPropagation()
            openComponentSelector()
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


        infoBlockScroller = ScrollComponent().constrain {
            y = SiblingConstraint()
            width = RelativeConstraint(1f) boundTo infoBlock
            height = RelativeConstraint(1f) boundTo infoBlock coerceAtMost (maxSectionHeight
                ?: RelativeWindowConstraint(1 / 3f))
        }

        infoBlock childOf infoBlockScroller

        drawObserver childOf rootComponent

        rootComponent.onKeyType(keyTypeListener)
    }

    private fun openComponentSelector() {
        isClickSelecting = true

        val rootWindow = Window.of(rootComponent)
        rootWindow.clickInterceptor = { mouseX, mouseY, _ ->
            rootWindow.clickInterceptor = null
            isClickSelecting = false

            val targetComponent = getClickSelectTarget(mouseX.toFloat(), mouseY.toFloat())
            if (targetComponent != null) {
                findAndSelect(targetComponent)
            }
            true
        }
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

    private fun cleanup() {
        drawObserver.hide(instantly = true)
        rootComponent.keyTypedListeners.remove(keyTypeListener)
        if (this in parent.children) {
            parent.children.remove(this)
        }
        displayManager.cleanup()
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
            return parentDisplay.childNodes
                .filterIsInstance<InspectorNode>().find { it.targetComponent == component }
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
            ensureLastComponent(inspectorContent)
        }

    }

    override fun draw(matrixStack: UMatrixStack) {
        separator1.setWidth(container.getWidth().pixels())
        separator2.setWidth(container.getWidth().pixels())

        val debugState = elementaDebug
        elementaDebug = false
        try {
            super.draw(matrixStack)
        } finally {
            elementaDebug = debugState
        }
    }

    @ApiStatus.Internal
    inner class Overlay : UIComponent() {
        override fun draw(matrixStack: UMatrixStack) {
            // If we got removed from our parent, we need to un-float ourselves
            if (!isMounted()) {
                Window.enqueueRenderOperation { setFloating(false) }
                cleanup()
                return
            }
            beforeDraw(matrixStack)
            val targetComponent = selectedNode?.targetComponent
            if (isClickSelecting) {
                val (mouseX, mouseY) = getMousePosition()
                getClickSelectTarget(mouseX, mouseY)
            } else {
                targetComponent
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
                ElementaVersion.V1.enableFor { // need the custom depth testing
                    UIBlock.drawBlock(matrixStack, Color(255, 255, 255, 100), x1, y1, x2, y2)
                }
                UGraphics.depthFunc(GL11.GL_LEQUAL)
                UGraphics.disableDepth()
            }

            if (targetComponent != null && measuringDistance) {
                val (mouseX, mouseY) = getMousePosition()
                val hoveredComponent = getClickSelectTarget(mouseX, mouseY)


                if (hoveredComponent != null && hoveredComponent != targetComponent) {
                    // Outline the hovered item
                    UIBlock.drawBlock(
                        matrixStack,
                        Color(255, 100, 100, 100),
                        hoveredComponent.getLeft().toDouble(),
                        hoveredComponent.getTop().toDouble(),
                        hoveredComponent.getRight().toDouble(),
                        hoveredComponent.getBottom().toDouble()
                    )

                    val horizontalLineY =
                        if (targetComponent.centerY() in hoveredComponent.getTop()..hoveredComponent.getBottom()) {
                            targetComponent.centerY()
                        } else if (targetComponent.getTop() > hoveredComponent.getBottom()) {
                            targetComponent.getTop()
                        } else {
                            targetComponent.getBottom()
                        }

                    if (hoveredComponent.getRight() < targetComponent.getLeft()) {
                        measureHorizontalDistance(
                            matrixStack,
                            hoveredComponent.getRight(),
                            targetComponent.getLeft(),
                            horizontalLineY
                        )
                    } else if (hoveredComponent.getRight() < targetComponent.getRight()) {
                        measureHorizontalDistance(
                            matrixStack,
                            hoveredComponent.getRight(),
                            targetComponent.getRight(),
                            horizontalLineY
                        )
                    }

                    if (hoveredComponent.getLeft() > targetComponent.getRight()) {
                        measureHorizontalDistance(
                            matrixStack,
                            targetComponent.getRight(),
                            hoveredComponent.getLeft(),
                            horizontalLineY
                        )
                    } else if (hoveredComponent.getLeft() > targetComponent.getLeft()) {
                        measureHorizontalDistance(
                            matrixStack,
                            targetComponent.getLeft(),
                            hoveredComponent.getLeft(),
                            horizontalLineY
                        )
                    }


                    val verticalLineX =
                        if (targetComponent.centerX() in hoveredComponent.getLeft()..hoveredComponent.getRight()) {
                            targetComponent.centerX()
                        } else if (hoveredComponent.getRight() < targetComponent.getLeft()) {
                            hoveredComponent.getRight()
                        } else {
                            hoveredComponent.getLeft()
                        }
                    if (hoveredComponent.getBottom() < targetComponent.getTop()) {
                        measureVerticalDistance(
                            matrixStack,
                            hoveredComponent.getBottom(),
                            targetComponent.getTop(),
                            verticalLineX
                        )
                    } else if (hoveredComponent.getBottom() < targetComponent.getBottom()) {
                        measureVerticalDistance(
                            matrixStack,
                            hoveredComponent.getBottom(),
                            targetComponent.getBottom(),
                            verticalLineX
                        )
                    }

                    if (hoveredComponent.getTop() > targetComponent.getBottom()) {
                        measureVerticalDistance(
                            matrixStack,
                            targetComponent.getBottom(),
                            hoveredComponent.getTop(),
                            verticalLineX
                        )
                    } else if (hoveredComponent.getTop() > targetComponent.getTop()) {
                        measureVerticalDistance(
                            matrixStack,
                            targetComponent.getTop(),
                            hoveredComponent.getTop(),
                            verticalLineX
                        )
                    }


                }
            }
            super.draw(matrixStack)
        }

        override fun animationFrame() {
            super.animationFrame()
            // Make sure we are the top-most component (last to draw and first to receive input)
            Window.enqueueRenderOperation {
                ensureLastComponent(this@Overlay)
            }
        }

        fun UIComponent.centerX(): Float {
            return (getLeft() + getRight()) / 2
        }

        fun UIComponent.centerY(): Float {
            return (getTop() + getBottom()) / 2
        }

        fun drawShadedText(
            matrixStack: UMatrixStack,
            text: String,
            xCenter: Float,
            yCenter: Float,
        ) {
            val stringWidth = text.width()
            val stringHeight = UGraphics.getFontHeight()


            val x1 = xCenter - stringWidth / 2.0
            val y1 = yCenter - stringHeight / 2.0
            val x2 = xCenter + stringWidth / 2.0
            val y2 = yCenter + stringHeight / 2.0

            // Draw outline for increased visiblity
            UIBlock.drawBlock(
                matrixStack,
                Color.MAGENTA,
                x1 - 1,
                y1 - 1,
                x2 + 1,
                y2 + 1
            )


            DefaultFonts.VANILLA_FONT_RENDERER.drawString(
                matrixStack,
                text,
                Color.WHITE,
                xCenter - stringWidth / 2,
                yCenter - stringHeight / 2,
                10f,
                1f,
                false,
            )
        }

        fun measureVerticalDistance(
            matrixStack: UMatrixStack,
            y1: Float,
            y2: Float,
            x: Float,
        ) {
            val distance = y2 - y1
            if (distance > 0) {
                UIBlock.drawBlock(
                    matrixStack,
                    Color.YELLOW,
                    (x - 1).toDouble(),
                    (y1).toDouble(),
                    (x + 1).toDouble(),
                    (y2).toDouble()
                )
                val string = String.format("%.2f", distance).dropLastWhile { it == '0' }.dropLastWhile { it == '.' }
                drawShadedText(
                    matrixStack,
                    "${string}px",
                    x + string.width() + 5,
                    y1 + distance / 2,
                )
            }
        }

        fun measureHorizontalDistance(
            matrixStack: UMatrixStack,
            x1: Float,
            x2: Float,
            y: Float,
        ) {
            val distance = x2 - x1
            if (distance > 0) {
                UIBlock.drawBlock(
                    matrixStack,
                    Color.YELLOW,
                    x1.toDouble(),
                    (y - 1).toDouble(),
                    x2.toDouble(),
                    (y + 1).toDouble()
                )
                drawShadedText(
                    matrixStack,
                    "${String.format("%.2f", distance).dropLastWhile { it == '0' }.dropLastWhile { it == '.' }}px",
                    x1 + distance / 2,
                    y + 10,
                )
            }
        }
    }

    private fun ensureLastComponent(component: UIComponent) {
        val componentOrder = listOf(
            Overlay::class.java,
            InspectorContent::class.java,
            Inspector::class.java,
        )
        component.setFloating(false)
        if (component.isMounted()) { // only if we are still mounted
            component.setFloating(true)
            val siblings = component.parent.children
            siblings.sortBy {
                componentOrder.indexOf(it.javaClass)
            }
        }
    }

    fun setDetached(external: Boolean) {
        displayManager.setDetached(external)
    }

    // Class created so that we can reliability detect if a container is this type
    internal inner class InspectorContent : UIContainer() {

        init {
            constrain {
                width = ChildBasedSizeConstraint()
                height = ChildBasedSizeConstraint()
            }
        }
    }

    internal inner class DisplayManager {

        private var externalInspectorDisplay: ExternalInspectorDisplay? = null

        // Create separate constraints so that the position of the inspector in the overlay
        // can be different from it in the external window
        private val externalContentConstraints = inspectorContent.constraints.copy()
        private val overlayContentConstraints = inspectorContent.constraints.copy()
        private var detached = !startDetached // Default to opposite state of startDetached so setDetached will run
        private val drawCallback: Window.() -> Unit = {
            draw(resolutionManager)
        }

        init {
            setDetached(startDetached)
            Window.of(rootComponent).drawCallbacks.add(drawCallback)
        }

        fun cleanup() {
            externalInspectorDisplay?.cleanup()
            Window.of(rootComponent).drawCallbacks.remove(drawCallback)
        }

        fun draw(resolutionManager: ResolutionManager) {
            externalInspectorDisplay?.updateFrameBuffer(resolutionManager)
        }

        fun setDetached(detached: Boolean) {
            if (detached == this.detached) {
                return
            }
            this.detached = detached
            val window = Window.of(rootComponent)
            if (detached) {
                var externalInspectorDisplay = externalInspectorDisplay
                if (externalInspectorDisplay == null) {
                    externalInspectorDisplay = createExternalDisplay().also {
                        this.externalInspectorDisplay = it
                    }
                }
                inspectorContent.constraints = externalContentConstraints

                externalInspectorDisplay.addComponent(inspectorContent)
                window.removeChild(inspectorContent)
                window.removeFloatingComponent(inspectorContent)
            } else {
                externalInspectorDisplay?.removeComponent(inspectorContent)
                window.addChild(inspectorContent)
                inspectorContent.constraints = overlayContentConstraints
            }
        }

        private fun createExternalDisplay(): ExternalInspectorDisplay {
            return when (Platform.platform.mcVersion) {
                11202, 10809 -> AwtInspectorDisplay()
                else -> GLFWDisplay()
            }
        }
    }

    companion object {
        internal val percentFormat: NumberFormat = NumberFormat.getPercentInstance()

        /**
         * Controls whether new inspectors or debug components open in a detached window or on the window they are debugging.
         */
        internal var startDetached = System.getProperty("elementa.inspector.detached", "true") == "true"
    }
}