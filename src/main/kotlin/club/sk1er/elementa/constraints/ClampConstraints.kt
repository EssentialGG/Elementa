package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Clamps [constraint] to be AT MOST [maxConstraint]
 */
class MaxConstraint(
    private val constraint: SuperConstraint<Float>,
    private val maxConstraint: SuperConstraint<Float>
) : MasterConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        maxConstraint.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constraint as WidthConstraint).getWidth(component)
            .coerceAtMost((maxConstraint as WidthConstraint).getWidth(component))
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constraint as HeightConstraint).getHeight(component)
            .coerceAtMost((maxConstraint as HeightConstraint).getHeight(component))
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constraint as RadiusConstraint).getRadius(component)
            .coerceAtMost((maxConstraint as RadiusConstraint).getRadius(component))
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constraint as XConstraint).getXPosition(component)
            .coerceAtMost((maxConstraint as XConstraint).getXPosition(component))
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constraint as YConstraint).getYPosition(component)
            .coerceAtMost((maxConstraint as YConstraint).getYPosition(component))
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context, please apply this to the components beforehand.")
    }
}

/**
 * Clamps [constraint] to be AT LEAST [minConstraint]
 */
class MinConstraint(
    private val constraint: SuperConstraint<Float>,
    private val minConstraint: SuperConstraint<Float>
) : MasterConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
        minConstraint.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constraint as WidthConstraint).getWidth(component)
            .coerceAtLeast((minConstraint as WidthConstraint).getWidth(component))
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constraint as HeightConstraint).getHeight(component)
            .coerceAtLeast((minConstraint as HeightConstraint).getHeight(component))
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constraint as RadiusConstraint).getRadius(component)
            .coerceAtLeast((minConstraint as RadiusConstraint).getRadius(component))
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constraint as XConstraint).getXPosition(component)
            .coerceAtLeast((minConstraint as XConstraint).getXPosition(component))
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constraint as YConstraint).getYPosition(component)
            .coerceAtLeast((minConstraint as YConstraint).getYPosition(component))
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context, please apply this to the components beforehand.")
    }
}