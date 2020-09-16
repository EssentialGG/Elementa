package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor

/**
 * Tries to expand to fill all of the remaining width/height available in this component's
 * parent.
 */
class FillConstraint : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getRight() - component.getLeft()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getBottom() - component.getTop()
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return ((constrainTo ?: component.parent).getRadius() - component.getLeft()) / 2f
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.WIDTH -> {
                visitor.visitParent(ConstraintType.X)
                visitor.visitParent(ConstraintType.WIDTH)
                visitor.visitSelf(ConstraintType.X)
            }
            ConstraintType.HEIGHT -> {
                visitor.visitParent(ConstraintType.Y)
                visitor.visitParent(ConstraintType.HEIGHT)
                visitor.visitSelf(ConstraintType.Y)
            }
            ConstraintType.RADIUS -> {
                visitor.visitSelf(ConstraintType.X)
                // TODO: Radius dependency?
            }
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}