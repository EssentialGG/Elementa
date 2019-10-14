package club.sk1er.elementa

import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.ColorConstraint
import club.sk1er.elementa.constraints.PositionConstraint
import club.sk1er.elementa.constraints.SizeConstraint
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.features.Feature
import net.minecraft.client.Minecraft
import org.lwjgl.input.Mouse

abstract class UIComponent {
    open lateinit var parent: UIComponent
    val children = mutableListOf<UIComponent>()
    val features = mutableListOf<Feature>()

    private var constraints = UIConstraints(this)

    private var clickAction: () -> Unit = {}
    private var hoverAction: () -> Unit = {}
    private var unHoverAction: () -> Unit = {}
    private var currentlyHovered = false

    fun addChild(component: UIComponent) = apply {
        component.parent = this
        children.add(component)
    }

    fun addChildren(vararg components: UIComponent) = apply {
        components.forEach { addChild(it) }
    }

    fun removeChild(component: UIComponent) = apply {
        children.remove(component)
    }

    fun clearChildren() = apply  {
        children.clear()
    }

    fun makeAnimation() = AnimatingConstraints(this, constraints)

    fun animateTo(constraints: AnimatingConstraints) {
        this.setConstraints(constraints)
    }

    fun setConstraints(constraints: UIConstraints) {
        this.constraints = constraints
    }

    fun enableFeatures(vararg features: Feature) {
        this.features.addAll(features)
    }

    fun setX(constraint: PositionConstraint) = apply {
        this.constraints.setX(constraint)
    }

    fun setY(constraint: PositionConstraint) = apply {
        this.constraints.setY(constraint)
    }

    fun setWidth(constraint: SizeConstraint) = apply {
        this.constraints.setWidth(constraint)
    }

    fun setHeight(constraint: SizeConstraint) = apply {
        this.constraints.setHeight(constraint)
    }

    fun setColor(constraint: ColorConstraint) = apply {
        this.constraints.setColor(constraint)
    }

    open fun getConstraints() = constraints

    open fun getLeft() = constraints.getX()

    open fun getTop() = constraints.getY()

    open fun getRight() = getLeft() + constraints.getWidth()

    open fun getBottom() = getTop() + constraints.getHeight()

    open fun getWidth() = constraints.getWidth()

    open fun getHeight() = constraints.getHeight()

    open fun getColor() = constraints.getColor()

    open fun isHovered(): Boolean {
        val res = Window.of(this).scaledResolution
        val mc = Minecraft.getMinecraft()

        val mouseX = Mouse.getX() * res.scaledWidth / mc.displayWidth
        val mouseY = res.scaledHeight - Mouse.getY() * res.scaledHeight / mc.displayHeight - 1f

        return (mouseX > getLeft() && mouseX < getRight() && mouseY > getTop() && mouseY < getBottom())
    }

    open fun draw() {
        this.children.forEach(UIComponent::draw)

        if (isHovered() && !currentlyHovered) {
            hoverAction()
            currentlyHovered = true
        }

        if (!isHovered() && currentlyHovered) {
            unHoverAction()
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

    open fun click() {
        if (isHovered()) clickAction()
        this.children.forEach(UIComponent::click)
    }

    open fun animationFrame() {
        val constraints = getConstraints()

        constraints.animationFrame()

        this.children.forEach(UIComponent::animationFrame)
    }

    fun onClick(method: () -> Unit) = apply {
        clickAction = method
    }

    fun onClick(method: Runnable) = apply {
        clickAction = { method.run() }
    }

    fun onHover(method: () -> Unit) = apply {
        hoverAction = method
    }

    fun onHover(method: Runnable) = apply {
        hoverAction = { method.run() }
    }

    fun onUnHover(method: () -> Unit) = apply {
        unHoverAction = method
    }

    fun onUnHover(method: Runnable) = apply {
        unHoverAction = { method.run() }
    }
}