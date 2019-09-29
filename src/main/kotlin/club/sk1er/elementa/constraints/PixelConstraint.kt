package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class PixelConstraint(private val value: Float) : GeneralConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        return parent.getLeft() + value
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        return parent.getTop() + value
    }

    override fun getXSize(component: UIComponent, parent: UIComponent): Float {
        return value
    }

    override fun getYSize(component: UIComponent, parent: UIComponent): Float {
        return value
    }
}