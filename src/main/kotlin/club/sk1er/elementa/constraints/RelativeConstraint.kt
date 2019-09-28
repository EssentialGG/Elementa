package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class RelativeConstraint(private val value: Float) : GeneralConstraint {
    override fun getXValue(component: UIComponent, parent: UIComponent): Float {
        return parent.getWidth() * value
    }

    override fun getYValue(component: UIComponent, parent: UIComponent): Float {
        return parent.getHeight() * value
    }
}