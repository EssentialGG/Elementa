package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.utils.roundToRealPixels

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
            parent.getLeft() + (parent.getWidth() / 2).roundToRealPixels()
        } else {
            parent.getLeft() + (parent.getWidth() / 2 - component.getWidth() / 2).roundToRealPixels()
        }
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        val parent = constrainTo ?: component.parent

        return if (component.isPositionCenter()) {
            parent.getTop() + (parent.getHeight() / 2).roundToRealPixels()
        } else {
            parent.getTop() + (parent.getHeight() / 2 - component.getHeight() / 2).roundToRealPixels()
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