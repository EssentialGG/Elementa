package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.RelativeConstraint.Companion.ONE_HALF

/**
 * Sets this component's X/Y position or width/height to be some
 * percentage of its parents.
 *
 * Some are predefined, such as [ONE_HALF]
 */
class RelativeConstraint(private val value: Float) : GeneralConstraint {
    override var cachedValue = 0f
    override var recalculate = true

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