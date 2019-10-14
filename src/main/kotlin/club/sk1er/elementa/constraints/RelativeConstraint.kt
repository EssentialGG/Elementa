package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class RelativeConstraint(private val value: Float) : GeneralConstraint {
    override fun getXValue(component: UIComponent, parent: UIComponent): Float {
        return parent.getWidth() * value
    }

    override fun getYValue(component: UIComponent, parent: UIComponent): Float {
        return parent.getHeight() * value
    }

    companion object {
        val FULL = RelativeConstraint(1f)
        val ONE_HALF = RelativeConstraint(1 / 2f)
        val ONE_THIRD = RelativeConstraint(1 / 3f)
        val TWO_THIRDS = RelativeConstraint(2 / 3f)
    }
}