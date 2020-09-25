package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor

/**
 * Centers this box on the X or Y axis.
 */
class CenterConstraint : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        val parent = constrainTo ?: component.parent

        return if (component.isPositionCenter()) {
            parent.getLeft() + parent.getWidth() / 2
        } else {
            parent.getLeft() + parent.getWidth() / 2 - component.getWidth() / 2
        }
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        val parent = constrainTo ?: component.parent

        return if (component.isPositionCenter()) {
            parent.getTop() + parent.getHeight() / 2
        } else {
            parent.getTop() + parent.getHeight() / 2 - component.getHeight() / 2
        }
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.X -> {
                visitor.visitParent(ConstraintType.X)
                visitor.visitParent(ConstraintType.WIDTH)
                if (!visitor.component.isPositionCenter())
                    visitor.visitSelf(ConstraintType.WIDTH)
            }
            ConstraintType.Y -> {
                visitor.visitParent(ConstraintType.Y)
                visitor.visitParent(ConstraintType.HEIGHT)
                if (!visitor.component.isPositionCenter())
                    visitor.visitSelf(ConstraintType.HEIGHT)
            }
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}