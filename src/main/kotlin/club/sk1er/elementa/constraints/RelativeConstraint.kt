package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's X/Y position or width/height to be some
 * multiple of its parents.
 */
class RelativeConstraint @JvmOverloads constructor(private val value: Float = 1f) : GeneralConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    var constrainer: UIComponent? = null

    fun constrainTo(component: UIComponent) = apply { constrainer = component }

    override fun getXValue(component: UIComponent, parent: UIComponent): Float {
        return if (constrainer != null) constrainer!!.getWidth() * value
        else parent.getWidth() * value
    }

    override fun getYValue(component: UIComponent, parent: UIComponent): Float {
        return if (constrainer != null) constrainer!!.getHeight() * value
        else parent.getHeight() * value
    }

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        return if (constrainer != null) (constrainer!!.getWidth() * value) / 2f
        else (parent.getWidth() * value) / 2f
    }
}