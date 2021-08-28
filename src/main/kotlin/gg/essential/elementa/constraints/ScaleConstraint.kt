package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor

class ScaleConstraint(val constraint: SuperConstraint<Float>, val value: Float) : MasterConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constraint as XConstraint).getXPosition(component) * value
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constraint as YConstraint).getYPosition(component) * value
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constraint as WidthConstraint).getWidth(component) * value
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constraint as HeightConstraint).getHeight(component) * value
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constraint as RadiusConstraint).getRadius(component) * value
    }

    override fun to(component: UIComponent): SuperConstraint<Float> {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context, please apply this to the components beforehand.")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        constraint.visit(visitor, type, setNewConstraint = false)
    }
}
