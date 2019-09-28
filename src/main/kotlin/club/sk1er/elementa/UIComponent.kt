package club.sk1er.elementa

abstract class UIComponent {
    private val children = mutableListOf<UIComponent>()
    val constraints: UIConstraints = UIConstraints(this)
    open lateinit var parent: UIComponent

    fun addChild(component: UIComponent) {
        component.parent = this
        children.add(component)
    }

    open fun getLeft(): Int {
        return parent.getLeft() + constraints.getX()
    }

    open fun getTop(): Int {
        return parent.getTop() + constraints.getY()
    }

    open fun draw() {
        this.children.forEach(UIComponent::draw)
    }
}