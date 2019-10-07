package club.sk1er.elementa

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse
import java.util.function.Supplier

abstract class UIComponent {
    private val children = mutableListOf<UIComponent>()
    private val constraints = UIConstraints(this)
    open lateinit var parent: UIComponent
    private var clickAction = Supplier<Any> {}

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

        val ret =  (mouseX > getLeft() && mouseX < getRight() && mouseY > getTop() && mouseY < getBottom())
        println("HOVER: $ret")

        return ret
    }

    open fun draw() {
        this.children.forEach(UIComponent::draw)
    }

    open fun click() {
        if (isHovered()) {
            clickAction.get()
        }
        this.children.forEach(UIComponent::click)
    }

    public fun onClick(method: Supplier<Any>) {
        clickAction = method
    }
}