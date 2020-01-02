package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Centers this box on the X or Y axis.
 */
class CenterConstraint : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getLeft() + (constrainTo ?: component.parent).getWidth() / 2 - component.getWidth() / 2
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getTop() + (constrainTo ?: component.parent).getHeight() / 2 - component.getHeight() / 2
    }
}