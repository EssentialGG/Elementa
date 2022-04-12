package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import java.lang.UnsupportedOperationException

/**
 * Similar to a [SiblingConstraint], except it tries to fit
 * itself inline if possible. If not possible, it falls back to the next line.
 *
 * @param padding spacing to apply selectively when it makes sense.
 * On the X axis this means it will apply when crammed,
 * on the Y axis this means it will apply when NOT crammed.
 */
class CramSiblingConstraint(padding: Float = 0f) : SiblingConstraint(padding) {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        val index = component.parent.children.indexOf(component)

        if (index == 0) {
            return component.parent.getLeft()
        }

        val sibling = component.parent.children[index - 1]

        if (sibling.getRight() + component.getWidth() + padding <= component.parent.getRight() + precisionAdjustmentFactor) {
            return sibling.getRight() + padding
        }

        return component.parent.getLeft()
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        val index = component.parent.children.indexOf(component)

        if (index == 0) {
            return component.parent.getTop()
        }

        val sibling = component.parent.children[index - 1]

        if (sibling.getRight() + component.getWidth() + padding <= component.parent.getRight() + precisionAdjustmentFactor) {
            return sibling.getTop()
        }

        return getLowestPoint(sibling, component.parent, index) + padding
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }



    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        val indexInParent = visitor.component.let { it.parent.children.indexOf(it) }

        when (type) {
            ConstraintType.X -> {
                if (indexInParent <= 0) {
                    visitor.visitParent(ConstraintType.X)
                    return
                }

                visitor.visitSibling(ConstraintType.X, indexInParent - 1)
                visitor.visitSibling(ConstraintType.WIDTH, indexInParent - 1)
                visitor.visitSelf(ConstraintType.WIDTH)
                visitor.visitParent(ConstraintType.X)
                visitor.visitParent(ConstraintType.WIDTH)
            }
            ConstraintType.Y -> {
                if (indexInParent <= 0) {
                    visitor.visitParent(ConstraintType.Y)
                    return
                }

                visitor.visitSibling(ConstraintType.X, indexInParent - 1)
                visitor.visitSibling(ConstraintType.WIDTH, indexInParent - 1)
                visitor.visitSelf(ConstraintType.WIDTH)
                visitor.visitParent(ConstraintType.X)
                visitor.visitParent(ConstraintType.WIDTH)
            }
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
    override fun getHorizontalPadding(component: UIComponent): Float {
        val index = component.parent.children.indexOf(component)

        if (index == 0) {
            return 0F
        }

        val sibling = component.parent.children[index - 1]

        if (sibling.getRight() + component.getWidth() + padding <= component.parent.getRight() + precisionAdjustmentFactor) {
            return padding
        }
        return 0F
    }
    override fun getVerticalPadding(component: UIComponent): Float {
        val index = component.parent.children.indexOf(component)

        if (index == 0) {
            return 0F
        }

        val sibling = component.parent.children[index - 1]

        if (sibling.getRight() + component.getWidth() + padding <= component.parent.getRight() + precisionAdjustmentFactor) {
            return 0F
        }

        return padding
    }

    private companion object {

        // Due to the precision and accuracy limitations of floating point numbers,
        // situations can arise where two numbers should be exactly the same but in practice
        // have a small difference in their value. In order to correct for this, a very small
        // offset is introduced to ensure this constraint behaves as expended
        private const val precisionAdjustmentFactor = 0.01f
    }
}