package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's X/Y position or width/height to be a constant
 * number of pixels.
 */
class PixelConstraint @JvmOverloads constructor(
    var value: Float,
    var alignOpposite: Boolean = false,
    var alignOutside:  Boolean = false
) : MasterConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    fun alignOutside(value: Boolean) = apply {
        this.alignOutside = value
    }

    fun alignOpposite(value: Boolean) = apply {
        this.alignOpposite = value
    }

    fun setValue(value: Float) = apply {
        this.value = value
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)

        return if (alignOpposite) {
            if (alignOutside) {
                target.getRight() + value
            } else {
                target.getRight() - value - component.getWidth()
            }
        } else {
            if (alignOutside) {
                target.getLeft() - component.getWidth() - value
            } else {
                target.getLeft() + value
            }
        }
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)

        return if (alignOpposite) {
            if (alignOutside) {
                target.getBottom() + value
            } else {
                target.getBottom() - value - component.getHeight()
            }
        } else {
            if (alignOutside) {
                target.getTop() - component.getHeight() - value
            } else {
                target.getTop() + value
            }
        }
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