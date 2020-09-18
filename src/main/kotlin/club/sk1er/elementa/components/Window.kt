package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintResolutionGui
import club.sk1er.elementa.constraints.resolution.ConstraintResolver
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.mods.core.universal.*
import club.sk1er.mods.core.universal.wrappers.UniversalPlayer
import club.sk1er.mods.core.universal.wrappers.message.UniversalTextComponent
import org.lwjgl.opengl.GL11
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * "Root" component. All components MUST have a Window in their hierarchy in order to do any rendering
 * or animating.
 */
class Window(val animationFPS: Int = 244) : UIComponent() {
    private var systemTime = -1L
    private var currentMouseButton = -1

    private var floatingComponents = mutableListOf<UIComponent>()

    private var focusedComponent: UIComponent? = null
    private var componentRequestingFocus: UIComponent? = null

    var scaledResolution: UniversalResolutionUtil = UniversalResolutionUtil.getInstance()
    private var cancelDrawing = false
    private var recheckTree = true

    init {
        super.parent = this

        children.addObserver { _, _ ->
            recheckTree = true
        }

        constraints.addObserver { _, _ ->
            recheckTree = true
        }
    }

    override fun draw() {
        if (cancelDrawing)
            return

        if (IS_DEV && recheckTree) {
            recheckTree = false
            val cyclicNodes = ConstraintResolver(this).getCyclicNodes()

            if (cyclicNodes != null) {
                UniversalMinecraft.getMinecraft().displayGuiScreen(
                    ConstraintResolutionGui(cyclicNodes)
                )
                cancelDrawing = true
                return
            }
        }

        val startTime = System.nanoTime()

        val it = renderOperations.iterator()
        while (it.hasNext() && System.nanoTime() - startTime < 5000) {
            it.next()()
            it.remove()
        }

        //#if MC>=11502
        //$$ UniversalGraphicsHandler.setStack(MatrixStack());
        //#endif
        UniversalGraphicsHandler.glClear(GL11.GL_STENCIL_BUFFER_BIT)
        UniversalGraphicsHandler.glClearStencil(0)

        scaledResolution = UniversalResolutionUtil.getInstance()

        if (systemTime == -1L)
            systemTime = System.currentTimeMillis()

        while (this.systemTime < System.currentTimeMillis() + 1000 / animationFPS) {
            animationFrame()

            this.systemTime += 1000 / animationFPS;
        }

        mouseMove()

        super.draw()
    }

    override fun mouseClick(mouseX: Double, mouseY: Double, button: Int) {
        currentMouseButton = button

        for (floatingComponent in floatingComponents) {
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
                UniversalMouse.getScaledX(),
                scaledResolution.scaledHeight - UniversalMouse.getScaledY(),
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
        return scaledResolution.scaledWidth.toFloat()
    }

    override fun getHeight(): Float {
        return scaledResolution.scaledHeight.toFloat()
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
        val sf = scaledResolution.scaleFactor

        val realX = currentScissor.x / sf
        val realWidth = currentScissor.width / sf

        val bottomY = ((scaledResolution.scaledHeight * sf) - currentScissor.y) / sf
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
