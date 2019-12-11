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

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return if (!alignOpposite) parent.getLeft() + value
                else parent.getRight() - value - component.getWidth()
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return if (!alignOpposite) parent.getTop() + value
                else parent.getBottom() - value - component.getHeight()
    }

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return value
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return value
    }

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        return value
    }
}