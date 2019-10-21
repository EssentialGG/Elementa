package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Clamps the width to be the max of [constraint] and [maxConstraint]
 */
class MaxSizeConstraint(
    private val constraint: SizeConstraint,
    private val maxConstraint: SizeConstraint
) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        maxConstraint.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getWidth(component, parent).coerceAtMost(maxConstraint.getWidth(component, parent))
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getHeight(component, parent).coerceAtMost(maxConstraint.getHeight(component, parent))
    }
}


/**
 * Clamps the width to be the min of [constraint] and [minConstraint]
 */
class MinSizeConstraint(
    private val constraint: SizeConstraint,
    private val minConstraint: SizeConstraint
) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        minConstraint.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getWidth(component, parent).coerceAtLeast(minConstraint.getWidth(component, parent))
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getHeight(component, parent).coerceAtLeast(minConstraint.getHeight(component, parent))
    }
}