package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class AspectConstraint(private val value: Float) : PositionConstraint, SizeConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        return component.getTop() * value
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        return component.getLeft() * value
    }

    override fun getXSize(component: UIComponent, parent: UIComponent): Float {
        return component.getHeight() * value
    }

    override fun getYSize(component: UIComponent, parent: UIComponent): Float {
        return component.getWidth() * value
    }
}