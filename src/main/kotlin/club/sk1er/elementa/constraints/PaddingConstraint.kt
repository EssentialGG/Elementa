package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Adds [padding] to this component's width.
 */
class WidthPaddingConstraint(private val widthConstraint: WidthConstraint, private val padding: Float) : WidthConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return widthConstraint.getWidth(component, parent) + padding
    }
}

/**
 * Adds [padding] to this component's height.
 */
class HeightPaddingConstraint(private val widthConstraint: HeightConstraint, private val padding: Float) : HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return widthConstraint.getHeight(component, parent) + padding
    }
}