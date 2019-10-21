package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's X/Y position or width/height to be some
 * multiple of its parents.
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
}