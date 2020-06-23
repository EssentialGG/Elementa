package club.sk1er.elementa

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.dsl.animate
import club.sk1er.elementa.effects.Effect
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.events.UIClickEvent
import club.sk1er.elementa.utils.TriConsumer
import club.sk1er.mods.core.universal.UniversalMouse
import club.sk1er.mods.core.universal.UniversalResolutionUtil
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.math.PI
import kotlin.math.sin

/**
 * UIComponent is the base of all drawing, meaning
 * everything visible on the screen is a UIComponent.
 */
abstract class UIComponent {
    open lateinit var parent: UIComponent
    open var children = CopyOnWriteArrayList<UIComponent>()
        internal set
    val effects = mutableListOf<Effect>()

    private var constraints = UIConstraints(this)

    /* Bubbling Events */
    val mouseClickListeners = mutableListOf<UIComponent.(UIClickEvent) -> Unit>()

    /* Non-Bubbling Events */
    var mouseReleaseAction: UIComponent.() -> Unit = {}
    var mouseEnterAction: UIComponent.() -> Unit = {}
    var mouseLeaveAction: UIComponent.() -> Unit = {}
    var mouseScrollAction: UIComponent.(delta: Int) -> Unit = {}
    var mouseDragAction: UIComponent.(mouseX: Float, mouseY: Float, button: Int) -> Unit = { _, _, _ -> }
    var keyTypeAction: UIComponent.(typedChar: Char, keyCode: Int) -> Unit = { _, _ -> }

    private var currentlyHovered = false
    private var beforeHideAnimation: AnimatingConstraints.() -> Unit = { }
    private var afterUnhideAnimation: AnimatingConstraints.() -> Unit = { }
    protected var focusedComponent: UIComponent? = null
    private var onFocus: (UIComponent.() -> Unit)? = null
    private var onFocusLost: (UIComponent.() -> Unit)? = null

    /**
     * Required for [unhide] so it can insert this component
     * back into the same position
     */
    private var indexInParent = 0

    /**
     * Adds [component] to this component's children tree,
     * as well as sets [component]'s parent to this component.
     */
    open fun addChild(component: UIComponent) = apply {
        component.parent = this
        children.add(component)
    }

    /**
     * Wrapper for [addChild].
     */
    fun addChildren(vararg components: UIComponent) = apply {
        components.forEach { addChild(it) }
    }

    /**
     * Remove's [component] from this component's children, effectively
     * removing it from the hierarchy tree.
     *
     * However, [component]'s parent still references this.
     */
    fun removeChild(component: UIComponent) = apply {
        children.remove(component)
    }

    /**
     * Removes all children, according to the same rules as [removeChild]
     */
    fun clearChildren() = apply {
        children.clear()
    }

    /**
     * Kotlin wrapper for [childrenOfType]
     */
    inline fun <reified T> childrenOfType() = childrenOfType(T::class.java)

    /**
     * Fetches all children of this component that are instances of [clazz]
     */
    open fun <T> childrenOfType(clazz: Class<T>) = children.filterIsInstance(clazz)

    /**
     * Constructs an animation object specific to this component.
     *
     * A convenient Kotlin wrapper can be found at [club.sk1er.elementa.dsl.animate]
     */
    fun makeAnimation() = AnimatingConstraints(this, constraints)

    /**
     * Begin animating to a previously constructed animation.
     *
     * This is handled internally by the [club.sk1er.elementa.dsl.animate] dsl if used.
     */
    fun animateTo(constraints: AnimatingConstraints) {
        this.setConstraints(constraints)
    }

    /**
     * Begin using a new set of constraints.
     */
    fun setConstraints(constraints: UIConstraints) {
        this.constraints = constraints
    }

    /**
     * Enables a set of effects to be applied when this component draws.
     */
    fun enableEffects(vararg effects: Effect) = apply {
        this.effects.addAll(effects)
    }

    /**
     * Enables a single effect to be applied when the component draws.
     */
    fun enableEffect(effect: Effect) = apply {
        this.effects.add(effect)
    }

    inline fun <reified T> removeEffect() {
        this.effects.removeIf { it is T }
    }

    fun <T : Effect> removeEffect(clazz: Class<T>) {
        this.effects.removeIf { clazz.isInstance(it) }
    }

    fun setChildOf(parent: UIComponent) = apply {
        parent.addChild(this)
    }

    fun setX(constraint: XConstraint) = apply {
        this.constraints.withX(constraint)
    }

    fun setY(constraint: YConstraint) = apply {
        this.constraints.withY(constraint)
    }

    fun setWidth(constraint: WidthConstraint) = apply {
        this.constraints.withWidth(constraint)
    }

    fun setHeight(constraint: HeightConstraint) = apply {
        this.constraints.withHeight(constraint)
    }

    fun setRadius(constraint: RadiusConstraint) = apply {
        this.constraints.withRadius(constraint)
    }

    fun setTextScale(constraint: HeightConstraint) = apply {
        this.constraints.withTextScale(constraint)
    }

    fun setColor(constraint: ColorConstraint) = apply {
        this.constraints.withColor(constraint)
    }

    open fun getConstraints() = constraints

    open fun getLeft() = constraints.getX()

    open fun getTop() = constraints.getY()

    open fun getRight() = getLeft() + getWidth()

    open fun getBottom() = getTop() + getHeight()

    open fun getWidth() = constraints.getWidth()

    open fun getHeight() = constraints.getHeight()

    open fun getRadius() = constraints.getRadius()

    open fun getTextScale() = constraints.getTextScale()

    open fun getColor() = constraints.getColor()

    open fun isPositionCenter(): Boolean {
        return false
    }

    /**
     * Checks if the player's mouse is currently on top of this component.
     *
     * It simply checks the bounds of this component's constraints (i.e. x,y and width,height).
     * If this component has children outside of its parent's bounds (which probably is not a good idea anyways...)
     * that are being hovered, it will NOT consider this component as hovered.
     */
    open fun isHovered(): Boolean {
        val res = Window.of(this).scaledResolution

        val mouseX = UniversalMouse.getScaledX().toFloat()
        val mouseY = res.scaledHeight - UniversalMouse.getTrueY() * res.scaledHeight / UniversalResolutionUtil.getInstance().windowHeight - 1f

        return isPointInside(mouseX, mouseY)
    }

    open fun isPointInside(x: Float, y: Float): Boolean {
        return x > getLeft()
                && x < getRight()
                && y > getTop()
                && y < getBottom()
    }

    /**
     * Does the actual drawing for this component, meant to be overridden by specific components.
     * Also does some housekeeping dealing with hovering and effects.
     */
    open fun draw() {
        // Draw colored outline around the components
        if (IS_DEBUG) {
            if (ScissorEffect.currentScissorState != null) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST)
            }

            val left = getLeft().toDouble()
            val right = getRight().toDouble()
            val top = getTop().toDouble()
            val bottom = getBottom().toDouble()

            val color = getDebugColor(depth(), (parent.hashCode() / PI) % PI)

            // Top outline block
            UIBlock.drawBlock(
                color,
                left - DEBUG_OUTLINE_WIDTH,
                top - DEBUG_OUTLINE_WIDTH,
                right + DEBUG_OUTLINE_WIDTH,
                top
            )

            // Right outline block
            UIBlock.drawBlock(color, right, top, right + DEBUG_OUTLINE_WIDTH, bottom)

            // Bottom outline block
            UIBlock.drawBlock(
                color,
                left - DEBUG_OUTLINE_WIDTH,
                bottom,
                right + DEBUG_OUTLINE_WIDTH,
                bottom + DEBUG_OUTLINE_WIDTH
            )

            // Left outline block
            UIBlock.drawBlock(color, left - DEBUG_OUTLINE_WIDTH, top, left, bottom)

            if (ScissorEffect.currentScissorState != null) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST)
            }
        }

        beforeChildrenDraw()

        val parentWindow = Window.of(this)

        this.children.forEach { child ->
            // If the child is outside the current viewport, don't waste time drawing
            if (!this.alwaysDrawChildren() && !parentWindow.isAreaVisible(
                    child.getLeft().toDouble(),
                    child.getTop().toDouble(),
                    child.getRight().toDouble(),
                    child.getBottom().toDouble()
                )
            ) return@forEach

            child.draw()
        }

        afterDraw()
    }

    open fun beforeDraw() {
        effects.forEach { it.beforeDraw(this) }
    }

    open fun afterDraw() {
        effects.forEach { it.afterDraw(this) }
    }

    open fun beforeChildrenDraw() {
        effects.forEach { it.beforeChildrenDraw(this) }
    }

    open fun mouseMove() {
        val hovered = isHovered()

        if (hovered && !currentlyHovered) {
            mouseEnterAction()
            currentlyHovered = true
        } else if (!hovered && currentlyHovered) {
            mouseLeaveAction()
            currentlyHovered = false
        }

        this.children.forEach { it.mouseMove() }
    }

    /**
     * Runs the set [onMouseClick] method for the component and it's children.
     * Use this in the proper mouse click event to cascade all component's mouse click events.
     * Most common use is on the [Window] object.
     */
    open fun mouseClick(mouseX: Int, mouseY: Int, button: Int) {
        for (i in children.lastIndex downTo 0) {
            val child = children[i]

            if (child.isPointInside(mouseX.toFloat(), mouseY.toFloat())) {
                return child.mouseClick(mouseX, mouseY, button)
            }
        }

        fireMouseEvent(UIClickEvent(mouseX.toFloat(), mouseY.toFloat(), button, this, this))
    }

    protected fun fireMouseEvent(event: UIClickEvent) {
        for (listener in mouseClickListeners) {
            this.listener(event)

            if (event.propagationStoppedImmediately) return
        }

        if (!event.propagationStopped && parent != this) {
            parent.fireMouseEvent(event.copy(currentTarget = parent))
        }
    }

    /**
     * Runs the set [onMouseRelease] method for the component and it's children.
     * Use this in the proper mouse release event to cascade all component's mouse release events.
     * Most common use is on the [Window] object.
     */
    open fun mouseRelease() {
        mouseReleaseAction()

        this.children.forEach { it.mouseRelease() }
    }

    /**
     * Runs the set [onMouseScroll] method for the component and it's children.
     * Use this in the proper mouse scroll event to cascade all component's mouse scroll events.
     * Most common use is on the [Window] object.
     */
    open fun mouseScroll(delta: Int) {
        if (delta == 0) return

        if (isHovered()) mouseScrollAction(delta)

        this.children.forEach { it.mouseScroll(delta) }
    }


    @Deprecated(
        "You no longer need to call mouseDrag manually, Elementa handles it internally.",
        level = DeprecationLevel.ERROR
    )
    open fun mouseDrag(mouseX: Int, mouseY: Int, button: Int) {
        // no-op
    }

    /**
     * Runs the set [onMouseDrag] method for the component and it's children.
     * Use this in the proper mouse drag event to cascade all component's mouse scroll events.
     * Most common use is on the [Window] object.
     */
    open fun dragMouse(mouseX: Int, mouseY: Int, button: Int) {
        mouseDragAction(mouseX - getLeft(), mouseY - getTop(), button)

        children.forEach { it.dragMouse(mouseX, mouseY, button) }
    }

    open fun keyType(typedChar: Char, keyCode: Int) {
        if (focusedComponent != null) {
            focusedComponent?.keyType(typedChar, keyCode)
        } else {
            keyTypeAction(typedChar, keyCode)
            children.forEach { it.keyType(typedChar, keyCode) }
        }
    }

    open fun animationFrame() {
        val constraints = getConstraints()

        constraints.animationFrame()

        this.children.forEach(UIComponent::animationFrame)
    }

    open fun alwaysDrawChildren(): Boolean {
        return false
    }

    fun depth(): Int {
        var current = this
        var depth = 0

        try {
            while (current !is Window && current.parent != current) {
                current = current.parent
                depth++
            }
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("No window parent? It's possible you haven't called Window.addChild() at this point in time.")
        }

        return depth
    }

    /**
     * Adds a method to be run when mouse is clicked within the component.
     */
    fun onMouseClick(method: UIComponent.(event: UIClickEvent) -> Unit) = apply {
        mouseClickListeners.add(method)
    }

    /**
     * Adds a method to be run when mouse is clicked within the component.
     */
    fun onMouseClickConsumer(method: Consumer<UIClickEvent>) = apply {
        mouseClickListeners.add { method.accept(it) }
    }

    /**
     * Adds a method to be run when mouse is released within the component.
     */
    fun onMouseRelease(method: UIComponent.() -> Unit) = apply {
        mouseReleaseAction = method
    }

    /**
     * Adds a method to be run when mouse is released within the component.
     */
    fun onMouseReleaseRunnable(method: Runnable) = apply {
        mouseReleaseAction = { method.run() }
    }

    /**
     * Adds a method to be run when mouse is dragged anywhere on screen.
     * This does not check if mouse is in component.
     */
    fun onMouseDrag(method: UIComponent.(mouseX: Float, mouseY: Float, mouseButton: Int) -> Unit) = apply {
        mouseDragAction = method
    }

    /**
     * Adds a method to be run when mouse is dragged anywhere on screen.
     * This does not check if mouse is in component.
     */
    fun onMouseDragConsumer(method: TriConsumer<Float, Float, Int>) = apply {
        mouseDragAction = { t: Float, u: Float, v: Int -> method.accept(t, u, v) }
    }

    /**
     * Adds a method to be run when mouse enters the component.
     */
    fun onMouseEnter(method: UIComponent.() -> Unit) = apply {
        mouseEnterAction = method
    }

    /**
     * Adds a method to be run when mouse enters the component.
     */
    fun onMouseEnterRunnable(method: Runnable) = apply {
        mouseEnterAction = { method.run() }
    }

    /**
     * Adds a method to be run when mouse leaves the component.
     */
    fun onMouseLeave(method: UIComponent.() -> Unit) = apply {
        mouseLeaveAction = method
    }

    /**
     * Adds a method to be run when mouse leaves the component.
     */
    fun onMouseLeaveRunnable(method: Runnable) = apply {
        mouseLeaveAction = { method.run() }
    }

    /**
     * Adds a method to be run when mouse scrolls while in the component.
     */
    fun onMouseScroll(method: UIComponent.(delta: Int) -> Unit) = apply {
        mouseScrollAction = method
    }

    /**
     * Adds a method to be run when mouse scrolls while in the component.
     */
    fun onMouseScrollConsumer(method: Consumer<Int>) = apply {
        mouseScrollAction = { method.accept(it) }
    }

    fun onKeyType(method: UIComponent.(typedChar: Char, keyCode: Int) -> Unit) = apply {
        keyTypeAction = method
    }

    fun onKeyTypeConsumer(method: BiConsumer<Char, Int>) {
        keyTypeAction = { t: Char, u: Int -> method.accept(t, u) }
    }

    /*
     Hide API
     */

    /**
     * Hides this component. Behind the scenes, "hiding" entails removal of this component
     * from the entire hierarchy, leading to changes in sibling/children relationships.
     *
     * This also means hidden components will no longer receive events, or be drawn in any way.
     *
     * NOTE: Make sure to release any focus on this component, because it will likely cause
     * unintended side effects.
     *
     * @param instantly normally, hiding a component will run its before-hide
     * animations, and when they are complete, it will fully remove the component.
     * If [instantly] is true, it will skip the animation cycle and instantly remove the component.
     */
    @JvmOverloads
    fun hide(instantly: Boolean = false) {
        if (instantly) {
            indexInParent = parent.children.indexOf(this@UIComponent)
            parent.removeChild(this@UIComponent)
            return
        }

        animate {
            this.beforeHideAnimation()

            val comp = this.completeAction
            onComplete {
                comp()

                indexInParent = parent.children.indexOf(this@UIComponent)
                parent.removeChild(this@UIComponent)
            }
        }
    }

    /**
     * Re-enables this component. This will do the opposite of [hide] and re-add this component
     * to the hierarchy, underneath the same parent.
     */
    fun unhide(useLastPosition: Boolean = true) {
        if (parent.children.contains(this)) {
            return
        }

        if (useLastPosition && indexInParent >= 0 && indexInParent < parent.children.size) {
            parent.children.add(indexInParent, this@UIComponent)
        } else {
            parent.children.add(this@UIComponent)
        }

        animate {
            this.afterUnhideAnimation()
        }
    }

    fun animateBeforeHide(animation: AnimatingConstraints.() -> Unit) = apply {
        beforeHideAnimation = animation
    }

    fun animateAfterUnhide(animation: AnimatingConstraints.() -> Unit) = apply {
        afterUnhideAnimation = animation
    }

    /**
     * Focus API
     */

    /**
     * Focus a component. Focusing means that this component will only propagate keyboard
     * events to the currently focused component. The component to be focused does
     * NOT have to be a direct child of this component, in fact, it is not even necessary
     * that the component passed is a descendent at all.
     */
    fun focus(component: UIComponent) {
        focusedComponent = component
        component.onFocus?.invoke(component)
    }

    fun grabParentFocus() {
        parent.focus(this)
    }

    fun grabWindowFocus() {
        Window.of(this).focus(this)
    }

    fun onFocus(listener: UIComponent.() -> Unit) = apply {
        onFocus = listener
    }

    /**
     * Remove the currently focused component. This means all of this component's children
     * will receive all events normally again.
     */
    fun unfocus() {
        focusedComponent?.let { it.onFocusLost?.invoke(it) }

        focusedComponent = null
    }

    fun releaseParentFocus() {
        parent.unfocus()
    }

    fun releaseWindowFocus() {
        Window.of(this).unfocus()
    }

    fun onFocusLost(listener: UIComponent.() -> Unit) = apply {
        onFocusLost = listener
    }

    companion object {
        val IS_DEBUG = System.getProperty("elementa.debug")?.toBoolean() ?: false
        val DEBUG_OUTLINE_WIDTH = System.getProperty("elementa.debug.width")?.toDoubleOrNull() ?: 2.0

        private fun getDebugColor(depth: Int, offset: Double): Color {
            val step = depth.toDouble() / PI + offset

            val red = ((sin((step)) + 0.75) * 170).toInt().coerceIn(0..255)
            val green = ((sin(step + 2 * Math.PI / 3) + 0.75) * 170).toInt().coerceIn(0..255)
            val blue = ((sin(step + 4 * Math.PI / 3) + 0.75) * 170).toInt().coerceIn(0..255)
            return Color(red, green, blue, 255)
        }
    }
}