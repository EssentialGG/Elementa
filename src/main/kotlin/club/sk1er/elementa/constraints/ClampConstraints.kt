package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import java.lang.UnsupportedOperationException

/**
 * Clamps the width to be the max of [constraint] and [maxConstraint]
 */
class MaxSizeConstraint(
    private val constraint: SizeConstraint,
    private val maxConstraint: SizeConstraint
) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        maxConstraint.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return constraint.getWidth(component).coerceAtMost(maxConstraint.getWidth(component))
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return constraint.getHeight(component).coerceAtMost(maxConstraint.getHeight(component))
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return constraint.getRadius(component).coerceAtMost(maxConstraint.getRadius(component))
    }

    override fun to(component: UIComponent) {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
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
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        maxConstraint.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return constraint.getXPosition(component).coerceAtMost(maxConstraint.getXPosition(component))
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return constraint.getYPosition(component).coerceAtMost(maxConstraint.getYPosition(component))
    }

    override fun to(component: UIComponent) {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
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
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        minConstraint.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return constraint.getWidth(component).coerceAtLeast(minConstraint.getWidth(component))
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return constraint.getHeight(component).coerceAtLeast(minConstraint.getHeight(component))
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return constraint.getRadius(component).coerceAtLeast(minConstraint.getRadius(component))
    }

    override fun to(component: UIComponent) {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
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
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        minConstraint.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return constraint.getXPosition(component).coerceAtLeast(minConstraint.getXPosition(component))
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return constraint.getYPosition(component).coerceAtLeast(minConstraint.getYPosition(component))
    }

    override fun to(component: UIComponent) {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}