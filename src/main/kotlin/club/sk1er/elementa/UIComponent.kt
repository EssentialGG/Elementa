package club.sk1er.elementa

abstract class UIComponent {
    private val children = mutableListOf<UIComponent>()
    val constraints: UIConstraints = UIConstraints(this)
    open lateinit var parent: UIComponent

    fun addChild(component: UIComponent) {
        component.parent = this
        children.add(component)
    }

    open fun getLeft() = constraints.getX()

    open fun getTop() = constraints.getY()

    open fun getRight() = getLeft() + constraints.getWidth()

    open fun getBottom() = getTop() + constraints.getHeight()

    open fun getWidth() = constraints.getWidth()

    open fun getHeight() = constraints.getHeight()

    open fun draw() {
        this.children.forEach(UIComponent::draw)
    }
}