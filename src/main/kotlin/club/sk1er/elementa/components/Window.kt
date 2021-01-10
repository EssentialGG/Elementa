package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintResolutionGui
import club.sk1er.elementa.constraints.resolution.ConstraintResolver
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.font.DefaultFonts
import club.sk1er.elementa.font.FontRenderer
import club.sk1er.mods.core.universal.*
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * "Root" component. All components MUST have a Window in their hierarchy in order to do any rendering
 * or animating.
 */
class Window(val animationFPS: Int = 244) : UIComponent() {
    private var systemTime = -1L
    private var currentMouseButton = -1

    private var floatingComponents = mutableListOf<UIComponent>()

    var hoveredFloatingComponent: UIComponent? = null
    private var focusedComponent: UIComponent? = null
    private var componentRequestingFocus: UIComponent? = null

    private var cancelDrawing = false

    init {
        super.parent = this
    }

    override fun afterInitialization() {
        enqueueRenderOperation {
            DefaultFonts.load()
            FontRenderer.initShaders()
            UICircle.initShaders()
            UIRoundedRectangle.initShaders()
        }
    }

    override fun draw() {
        if (cancelDrawing)
            return

        val startTime = System.nanoTime()

        val it = renderOperations.iterator()
        while (it.hasNext() && System.nanoTime() - startTime < 5000) {
            it.next()()
            it.remove()
        }

        UGraphics.glClear(GL11.GL_STENCIL_BUFFER_BIT)
        UGraphics.glClearStencil(0)

        if (systemTime == -1L)
            systemTime = System.currentTimeMillis()

        try {
            while (this.systemTime < System.currentTimeMillis() + 1000 / animationFPS) {
                animationFrame()
                this.systemTime += 1000 / animationFPS
            }

            hoveredFloatingComponent = null
            val (mouseX, mouseY) = getMousePosition()
            for (component in floatingComponents.reversed()) {
                if (component.isPointInside(mouseX, mouseY)) {
                    hoveredFloatingComponent = component
                    break
                }
            }

            mouseMove(this)
            super.draw()
            UIBlock.drawBlock(Color.BLACK, 5.0, 5.0, 500.0, 200.0)
            DefaultFonts.MINECRAFT.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", Color.WHITE, 5f, 10f, 10f)
            UGraphics.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 5f, 20f, Color.WHITE.rgb, false)
            DefaultFonts.MINECRAFT.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", Color.WHITE, 5f, 30f, 9f)
            DefaultFonts.MINECRAFT.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", Color.WHITE, 5f, 40f, 6f)
            DefaultFonts.MINECRAFT.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", Color.WHITE, 5f, 50f, 14f)
        } catch (e: Throwable) {
            cancelDrawing = true

            if (e is StackOverflowError) {
                val guiName = UMinecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "<unknown>"

                if (IS_DEV) {
                    val cyclicNodes = ConstraintResolver(this).getCyclicNodes()

                    UMinecraft.getMinecraft().displayGuiScreen(
                        ConstraintResolutionGui(guiName, this, cyclicNodes)
                    )
                } else {
                    UMinecraft.getMinecraft().displayGuiScreen(null)

                    UChat.chat("Elementa encountered an error while drawing a GUI. Check your logs for more information.")
                    println("Elementa: Cyclic constraint structure detected!")
                    println("If you are a developer, set the environment variable \"elementa.dev=true\" to assist in debugging the issue.")
                    println("Gui name: $guiName")
                    e.printStackTrace()
                }
            } else {
                val guiName = UMinecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "<unknown>"
                UMinecraft.getMinecraft().displayGuiScreen(null)
                UChat.chat("§cElementa encountered an error while drawing a GUI. Check your logs for more information.")
                println("Elementa: encountered an error while drawing a GUI")
                println("Gui name: $guiName")
                e.printStackTrace()
            }

            // We may have thrown in the middle of a ScissorEffect, in which case we
            // need to disable the scissor if we don't want half the user's screen gone
            GL11.glDisable(GL11.GL_SCISSOR_TEST)
        }
    }

    fun drawFloatingComponents() {
        floatingComponents.forEach(UIComponent::draw)
    }

    override fun mouseScroll(delta: Double) {
        val (mouseX, mouseY) = getMousePosition()
        for (floatingComponent in floatingComponents.reversed()) {
            if (floatingComponent.isPointInside(mouseX, mouseY)) {
                floatingComponent.mouseScroll(delta)
                return
            }
        }

        super.mouseScroll(delta)
    }

    override fun mouseClick(mouseX: Double, mouseY: Double, button: Int) {
        currentMouseButton = button

        for (floatingComponent in floatingComponents.reversed()) {
            if (floatingComponent.isPointInside(mouseX.toFloat(), mouseY.toFloat())) {
                floatingComponent.mouseClick(mouseX, mouseY, button)
                dealWithFocusRequests()
                return
            }
        }

        super.mouseClick(mouseX, mouseY, button)
        dealWithFocusRequests()
    }

    private fun dealWithFocusRequests() {
        if (componentRequestingFocus == null) {
            unfocus()
        } else if (componentRequestingFocus != focusedComponent) {
            if (focusedComponent != null)
                focusedComponent?.loseFocus()

            focusedComponent = componentRequestingFocus
            focusedComponent?.focus()
        }

        componentRequestingFocus = null
    }

    override fun mouseRelease() {
        super.mouseRelease()

        currentMouseButton = -1
    }

    override fun keyType(typedChar: Char, keyCode: Int) {
        if (focusedComponent != null) {
            focusedComponent?.keyType(typedChar, keyCode)
        } else {
            super.keyType(typedChar, keyCode)
        }
    }

    override fun animationFrame() {
        if (currentMouseButton != -1) {
            dragMouse(
                UMouse.getScaledX().toInt(),
                UResolution.scaledHeight - UMouse.getScaledY().toInt(),
                currentMouseButton
            )
        }

        if (componentRequestingFocus != null && componentRequestingFocus != focusedComponent) {
            if (focusedComponent != null)
                focusedComponent?.loseFocus()

            focusedComponent = componentRequestingFocus
            focusedComponent?.focus()
        }
        componentRequestingFocus = null

        super.animationFrame()
    }

    override fun getLeft(): Float {
        return 0f
    }

    override fun getTop(): Float {
        return 0f
    }

    override fun getWidth(): Float {
        return UResolution.scaledWidth.toFloat()
    }

    override fun getHeight(): Float {
        return UResolution.scaledHeight.toFloat()
    }

    override fun getRight() = getWidth()
    override fun getBottom() = getHeight()

    fun isAreaVisible(left: Double, top: Double, right: Double, bottom: Double): Boolean {
        if (right < getLeft() ||
            left > getRight() ||
            bottom < getTop() ||
            top > getBottom()
        ) return false

        val currentScissor = ScissorEffect.currentScissorState ?: return true
        val sf = UResolution.scaleFactor

        val realX = currentScissor.x / sf
        val realWidth = currentScissor.width / sf

        val bottomY = ((UResolution.scaledHeight * sf) - currentScissor.y) / sf
        val realHeight = currentScissor.height / sf

        return right > realX &&
                left < realX + realWidth &&
                bottom >= bottomY - realHeight &&
                top <= bottomY
    }

    /*
     * Floating API
     */

    fun addFloatingComponent(component: UIComponent) {
        if (floatingComponents.contains(component)) return

        floatingComponents.add(component)
    }

    fun removeFloatingComponent(component: UIComponent) {
        floatingComponents.remove(component)
    }

    /*
     * Focus API
     */

    /**
     * Focus a component. Focusing means that this component will only propagate keyboard
     * events to the currently focused component. The component to be focused does
     * NOT have to be a direct child of this component.
     */
    fun focus(component: UIComponent) {
        componentRequestingFocus = component
    }

    /**
     * Remove the currently focused component. This means only the window will receive
     * keyboard events until another component is focused.
     */
    fun unfocus() {
        focusedComponent?.loseFocus()
        focusedComponent = null
    }

    companion object {
        private val renderOperations = ConcurrentLinkedQueue<() -> Unit>()

        fun enqueueRenderOperation(operation: Runnable) {
            renderOperations.add {
                operation.run()
            }
        }

        fun enqueueRenderOperation(operation: () -> Unit) {
            renderOperations.add(operation)
        }

        fun of(component: UIComponent): Window {
            var current = component

            while (current !is Window && current.hasParent && current.parent != current)
                current = current.parent

            return current as? Window
                ?: throw IllegalStateException("No window parent? It's possible you haven't called Window.addChild() at this point in time.")
        }
    }
}
