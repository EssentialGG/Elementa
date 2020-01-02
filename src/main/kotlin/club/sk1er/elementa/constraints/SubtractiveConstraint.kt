package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class SubtractivePositionConstraint(private val constraint1: PositionConstraint, private val constraint2: PositionConstraint) : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint1.animationFrame()
        constraint2.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return constraint1.getXPosition(component) - constraint2.getXPosition(component) + (constrainTo ?: component.parent).getLeft()
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return constraint1.getYPosition(component) - constraint2.getYPosition(component) + (constrainTo ?: component.parent).getTop()
    }
}

class SubtractiveSizeConstraint(private val constraint1: SizeConstraint, private val constraint2: SizeConstraint) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint1.animationFrame()
        constraint2.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return constraint1.getWidth(component) - constraint2.getWidth(component)
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return constraint1.getHeightImpl(component) - constraint2.getHeightImpl(component)
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return constraint1.getRadiusImpl(component) - constraint2.getRadiusImpl(component)
    }

    override fun to(component: UIComponent) {
        throw(IllegalStateException("Constraint.to(UIComponent) is not available in this context!"))
    }
}