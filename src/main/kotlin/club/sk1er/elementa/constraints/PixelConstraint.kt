package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class PixelConstraint(private val value: Float) : GeneralConstraint {
    override fun getXValue(component: UIComponent, parent: UIComponent): Float {
        return parent.getLeft() + value
    }

    override fun getYValue(component: UIComponent, parent: UIComponent): Float {
        return parent.getTop() + value
    }
}