package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Centers this box on the X or Y axis.
 */
class CenterConstraint : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return parent.getLeft() + parent.getWidth() / 2 - component.getWidth() / 2
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return parent.getTop() + parent.getHeight() / 2 - component.getHeight() / 2
    }
}