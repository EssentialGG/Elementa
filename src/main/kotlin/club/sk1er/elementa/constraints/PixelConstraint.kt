package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class PixelConstraint(private val value: Float) : GeneralConstraint {
    override fun getXValue(component: UIComponent, parent: UIComponent): Float {
        return if (value >= 0) {
            parent.getLeft() + value
        } else {
            parent.getRight() + value
        }
    }

    override fun getYValue(component: UIComponent, parent: UIComponent): Float {
        return if (value >= 0) {
            parent.getTop() + value
        } else {
            parent.getBottom() + value
        }
    }
}