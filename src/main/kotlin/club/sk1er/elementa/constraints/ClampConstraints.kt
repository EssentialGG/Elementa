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

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getRadius(component, parent).coerceAtMost(maxConstraint.getRadius(component, parent))
    }
}

/**
 * Clamps the width to be the max of [constraint] and [maxConstraint]
 */
class MaxPositionConstraint(
        private val constraint: PositionConstraint,
        private val maxConstraint: PositionConstraint
) : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        maxConstraint.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getXPosition(component, parent).coerceAtMost(maxConstraint.getXPosition(component, parent))
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getYPosition(component, parent).coerceAtMost(maxConstraint.getYPosition(component, parent))
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

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getRadius(component, parent).coerceAtLeast(minConstraint.getRadius(component, parent))
    }
}

/**
 * Clamps the width to be the min of [constraint] and [minConstraint]
 */
class MinPositionConstraint(
        private val constraint: PositionConstraint,
        private val minConstraint: PositionConstraint
) : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        minConstraint.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getXPosition(component, parent).coerceAtLeast(minConstraint.getXPosition(component, parent))
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint.getYPosition(component, parent).coerceAtLeast(minConstraint.getYPosition(component, parent))
    }
}