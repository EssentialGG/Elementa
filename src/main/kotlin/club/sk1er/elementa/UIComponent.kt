package club.sk1er.elementa

import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse

abstract class UIComponent {
    private val children = mutableListOf<UIComponent>()
    private val constraints = UIConstraints(this)
    open lateinit var parent: UIComponent
    private var clickAction: () -> Unit = {}

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

    fun makeAnimation() = AnimatingConstraints(this)

    open fun getConstraints() = constraints

    open fun getLeft() = constraints.getX()

    open fun getTop() = constraints.getY()

    open fun getRight() = getLeft() + constraints.getWidth()

    open fun getBottom() = getTop() + constraints.getHeight()

    open fun getWidth() = constraints.getWidth()

    open fun getHeight() = constraints.getHeight()

    open fun isHovered(): Boolean {
        val res = ScaledResolution(Minecraft.getMinecraft())
        val mc = Minecraft.getMinecraft()

        val mouseX = Mouse.getX() * res.scaledWidth / mc.displayWidth
        val mouseY = Mouse.getY() * res.scaledHeight / mc.displayHeight

        return (mouseX > getLeft() && mouseX < getRight() && mouseY > getTop() && mouseY < getBottom())
    }

    open fun draw() {
        this.children.forEach(UIComponent::draw)
    }

    open fun click() {
        if (isHovered()) {
            clickAction()
        }
        this.children.forEach(UIComponent::click)
    }

    fun onClick(method: () -> Unit) {
        clickAction = method
    }

    fun onClick(method: Runnable) {
        clickAction = { method.run() }
    }
}