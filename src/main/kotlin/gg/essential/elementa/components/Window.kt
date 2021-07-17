package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintResolutionGui
import gg.essential.elementa.constraints.resolution.ConstraintResolver
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.font.FontRenderer
import gg.essential.elementa.utils.elementaDev
import gg.essential.elementa.utils.requireMainThread
import gg.essential.universal.*
import org.lwjgl.opengl.GL11
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

/**
 * "Root" component. All components MUST have a Window in their hierarchy in order to do any rendering
 * or animating.
 */
class Window(val animationFPS: Int = 244) : UIComponent() {
    private var systemTime = -1L
    private var currentMouseButton = -1

    private var floatingComponents = mutableListOf<UIComponent>()

    var hoveredFloatingComponent: UIComponent? = null
    var focusedComponent: UIComponent? = null
        private set
    private var componentRequestingFocus: UIComponent? = null

    private var cancelDrawing = false

    init {
        super.parent = this
    }

    override fun afterInitialization() {
        enqueueRenderOperation {
            FontRenderer.initShaders()
            UICircle.initShaders()
            UIRoundedRectangle.initShaders()
        }
    }

    override fun draw() {
        if (cancelDrawing)
            return

        requireMainThread()

        val startTime = System.nanoTime()

        val it = renderOperations.iterator()
        while (it.hasNext() && System.nanoTime() - startTime < TimeUnit.MILLISECONDS.toNanos(5)) {
            it.next()()
            it.remove()
        }




        if (systemTime == -1L)
            systemTime = System.currentTimeMillis()

        try {

            //If this Window is more than 5 seconds behind, reset it be only 5 seconds.
            //This will drop missed frames but avoid the game freezing as the Window tries
            //to catch after a period of inactivity
            if (System.currentTimeMillis() - this.systemTime > TimeUnit.SECONDS.toMillis(5))
                this.systemTime = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(5)

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
        } catch (e: Throwable) {
            cancelDrawing = true

            if (e is StackOverflowError) {
                val guiName = UMinecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "<unknown>"

                if (elementaDev) {
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
        requireMainThread()

        val it = floatingComponents.iterator()
        while (it.hasNext()) {
            val component = it.next()
            if (ofOrNull(component) == null) {
                it.remove()
                continue
            }
            component.draw()
        }
    }

    override fun mouseScroll(delta: Double) {
        requireMainThread()

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
        requireMainThread()

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
        requireMainThread()

        super.mouseRelease()

        currentMouseButton = -1
    }

    override fun keyType(typedChar: Char, keyCode: Int) {
        requireMainThread()

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
        if (isInitialized) {
            requireMainThread()
        }

        if (floatingComponents.contains(component)) return

        floatingComponents.add(component)
    }

    fun removeFloatingComponent(component: UIComponent) {
        if (isInitialized) {
            requireMainThread()
        }

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
        if (isInitialized) {
            requireMainThread()
        }

        componentRequestingFocus = component
    }

    /**
     * Remove the currently focused component. This means only the window will receive
     * keyboard events until another component is focused.
     */
    fun unfocus() {
        if (isInitialized) {
            requireMainThread()
        }

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
            return ofOrNull(component) ?: throw IllegalStateException(
                "No window parent? It's possible you haven't called Window.addChild() at this point in time."
            )
        }

        fun ofOrNull(component: UIComponent): Window? {
            var current = component

            while (current !is Window && current.hasParent && current.parent != current)
                current = current.parent

            return current as? Window
        }
    }
}
