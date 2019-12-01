package club.sk1er.elementa

import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.effects.Effect
import net.minecraft.client.Minecraft
import org.lwjgl.input.Mouse
import java.util.function.Consumer

/**
 * UIComponent is the base of all drawing, meaning
 * everything visible on the screen is a UIComponent.
 */
abstract class UIComponent {
    open lateinit var parent: UIComponent
    open val children = mutableListOf<UIComponent>()

    private val features = mutableListOf<Effect>()
    private var constraints = UIConstraints(this)

    private var mouseClickAction: (mouseX: Float, mouseY: Float, button: Int) -> Unit = { _, _, _ -> }
    private var mouseReleaseAction: () -> Unit = {}
    private var mouseEnterAction: () -> Unit = {}
    private var mouseLeaveAction: () -> Unit = {}
    private var mouseScrollAction: (delta: Int) -> Unit = {}
    private var mouseDragAction: (mouseX: Float, mouseY: Float, button: Int) -> Unit = { _, _, _ -> }

    private var currentlyHovered = false

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
    fun clearChildren() = apply  {
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
        this.features.addAll(effects)
    }

    /**
     * Enables a single effect to be applied when the component draws.
     */
    fun enableEffect(effect: Effect) = apply {
        this.features.add(effect)
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

    fun setRadius(constraint: WidthConstraint) = apply {
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

    open fun getRight() = getLeft() + constraints.getWidth()

    open fun getBottom() = getTop() + constraints.getHeight()

    open fun getWidth() = constraints.getWidth()

    open fun getHeight() = constraints.getHeight()

    open fun getRadius() = constraints.getRadius()

    open fun getTextScale() = constraints.getTextScale()

    open fun getColor() = constraints.getColor()

    /**
     * Checks if the player's mouse is currently on top of this component.
     *
     * It simply checks the bounds of this component's constraints (i.e. x,y and width,height).
     * If this component has children outside of its parent's bounds (which probably is not a good idea anyways...)
     * that are being hovered, it will NOT consider this component as hovered.
     */
    open fun isHovered(): Boolean {
        val res = Window.of(this).scaledResolution
        val mc = Minecraft.getMinecraft()

        val mouseX = Mouse.getX() * res.scaledWidth / mc.displayWidth
        val mouseY = res.scaledHeight - Mouse.getY() * res.scaledHeight / mc.displayHeight - 1f

        return (mouseX > getLeft() && mouseX < getRight() && mouseY > getTop() && mouseY < getBottom())
    }

    /**
     * Does the actual drawing for this component, meant to be overridden by specific components.
     * Also does some housekeeping dealing with hovering and effects.
     */
    open fun draw() {
        beforeChildrenDraw()

        this.children.forEach(UIComponent::draw)

        if (isHovered() && !currentlyHovered) {
            mouseEnterAction()
            currentlyHovered = true
        }

        if (!isHovered() && currentlyHovered) {
            mouseLeaveAction()
            currentlyHovered = false
        }

        afterDraw()
    }

    open fun beforeDraw() {
        features.forEach { it.beforeDraw(this) }
    }

    open fun afterDraw() {
        features.forEach { it.afterDraw(this) }
    }

    open fun beforeChildrenDraw() {
        features.forEach { it.beforeChildrenDraw(this) }
    }

    /**
     * Runs the set [onMouseClick] method for the component and it's children.
     * Use this in the proper mouse click event to cascade all component's mouse click events.
     * Most common use is on the [Window] object.
     */
    open fun mouseClick(mouseX: Int, mouseY: Int, button: Int) {
        if (isHovered()) mouseClickAction( mouseX - getLeft(), mouseY - getTop(), button)
        this.children.forEach { it.mouseClick(mouseX, mouseY, button) }
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
        if (isHovered()) mouseScrollAction(delta)
        this.children.forEach { it.mouseScroll(delta) }
    }

    /**
     * Runs the set [onMouseDrag] method for the component and it's children.
     * Use this in the proper mouse drag event to cascade all component's mouse scroll events.
     * Most common use is on the [Window] object.
     */
    open fun mouseDrag(mouseX: Int, mouseY: Int, button: Int) {
        mouseDragAction(mouseX - getLeft(), mouseY - getTop(), button)
        children.forEach { it.mouseDrag(mouseX, mouseY, button) }
    }

    open fun animationFrame() {
        val constraints = getConstraints()

        constraints.animationFrame()

        this.children.forEach(UIComponent::animationFrame)
    }

    /**
     * Adds a method to be run when mouse is clicked within the component.
     */
    fun onMouseClick(method: (Float, Float, Int) -> Unit) = apply {
        mouseClickAction = method
    }

    /**
     * Adds a method to be run when mouse is clicked within the component.
     */
//    fun onMouseClick(method: Consumer<Int>) = apply {
//        mouseClickAction = method.accept(int)
//    }

    /**
     * Adds a method to be run when mouse is released within the component.
     */
    fun onMouseRelease(method: () -> Unit) = apply {
        mouseReleaseAction = method
    }

    /**
     * Adds a method to be run when mouse is released within the component.
     */
    fun onMouseRelease(method: Runnable) = apply {
        mouseReleaseAction = method::run
    }

    /**
     * Adds a method to be run when mouse is dragged anywhere on screen.
     * This does not check if mouse is in component.
     */
    fun onMouseDrag(method: (Float, Float, Int) -> Unit) = apply {
        mouseDragAction = method
    }

    /**
     * Adds a method to be run when mouse enters the component.
     */
    fun onMouseEnter(method: () -> Unit) = apply {
        mouseEnterAction = method
    }

    /**
     * Adds a method to be run when mouse enters the component.
     */
    fun onMouseEnter(method: Runnable) = apply {
        mouseEnterAction = method::run
    }

    /**
     * Adds a method to be run when mouse leaves the component.
     */
    fun onMouseLeave(method: () -> Unit) = apply {
        mouseLeaveAction = method
    }

    /**
     * Adds a method to be run when mouse leaves the component.
     */
    fun onMouseLeave(method: Runnable) = apply {
        mouseLeaveAction = method::run
    }

    /**
     * Adds a method to be run when mouse scrolls while in the component.
     */
    fun onMouseScroll(method: (delta: Int) -> Unit) = apply {
        mouseScrollAction = method
    }

    /**
     * Adds a method to be run when mouse scrolls while in the component.
     */
    fun onMouseScroll(method: Consumer<Int>) = apply {
        mouseScrollAction = method::accept
    }
}