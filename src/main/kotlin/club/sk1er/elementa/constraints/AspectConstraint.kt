package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * For size:
 * Sets the width/height to be [value] multiple of its own height/width respectively.
 *
 * For position:
 * Sets the x/y position to be [value] multiple of its own y/x position respectively.
 */
class AspectConstraint(private val value: Float) : PositionConstraint, SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return component.getTop() * value
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return component.getLeft() * value
    }

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return component.getHeight() * value
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return component.getWidth() * value
    }

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        return component.getRadius() * value
    }
}