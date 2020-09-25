package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIImage
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import java.lang.UnsupportedOperationException

/**
 * Tries to expand to fill all of the remaining width/height available in this component's
 * parent.
 */
class MaxImageConstraint : WidthConstraint, HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent): Float {
        if (component !is UIImage) throw UnsupportedOperationException("MaxImageConstraint is not available in this context!")
        return (constrainTo ?: component.parent).getRight() - component.getLeft()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        if (component !is UIImage) throw UnsupportedOperationException("MaxImageConstraint is not available in this context!")
        return (constrainTo ?: component.parent).getBottom() - component.getTop()
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
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}