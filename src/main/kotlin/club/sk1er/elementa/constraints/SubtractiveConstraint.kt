package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class SubtractivePositionConstraint(private val constraint1: PositionConstraint, private val constraint2: PositionConstraint) : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun animationFrame() {
        super.animationFrame()
        constraint1.animationFrame()
        constraint2.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint1.getXPosition(component, parent) - constraint2.getXPosition(component, parent)
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint1.getYPosition(component, parent) - constraint2.getYPosition(component, parent)
    }
}

class SubtractiveSizeConstraint(private val constraint1: SizeConstraint, private val constraint2: SizeConstraint) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun animationFrame() {
        super.animationFrame()
        constraint1.animationFrame()
        constraint2.animationFrame()
    }

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint1.getWidth(component, parent) - constraint2.getWidth(component, parent)
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return constraint1.getHeightImpl(component, parent) - constraint2.getHeightImpl(component, parent)
    }
}