package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's X/Y position or width/height to be a constant
 * number of pixels.
 */
class PixelConstraint @JvmOverloads constructor(
    private val value: Float,
    private val alignOpposite: Boolean = false
) : PositionConstraint, SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return if (!alignOpposite) (constrainTo ?: component.parent).getLeft() + value
                else (constrainTo ?: component.parent).getRight() - value - component.getWidth()
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return if (!alignOpposite) (constrainTo ?: component.parent).getTop() + value
                else (constrainTo ?: component.parent).getBottom() - value - component.getHeight()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return value
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return value
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return value
    }
}