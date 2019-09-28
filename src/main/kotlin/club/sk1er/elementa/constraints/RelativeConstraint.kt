package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class RelativeConstraint(private val value: Float) : PositionConstraint, SizeConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        return parent.getWidth() * value
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        return parent.getHeight() * value
    }

    override fun getXSize(component: UIComponent, parent: UIComponent) = getXPosition(component, parent)

    override fun getYSize(component: UIComponent, parent: UIComponent) = getYPosition(component, parent)
}