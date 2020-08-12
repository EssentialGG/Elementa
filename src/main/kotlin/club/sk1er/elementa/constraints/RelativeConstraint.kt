package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's X/Y position or width/height to be some
 * multiple of its parents.
 */
class RelativeConstraint @JvmOverloads constructor(val value: Float = 1f) : PositionConstraint, SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getLeft() + getWidth(component)
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getTop() + getHeight(component)
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getWidth() * value
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getHeight() * value
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return ((constrainTo ?: component.parent).getWidth() * value) / 2f
    }
}