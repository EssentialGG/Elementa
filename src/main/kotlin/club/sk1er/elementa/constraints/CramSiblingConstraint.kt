package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
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

        if (sibling.getRight() + component.getWidth() + padding <= component.parent.getRight()) {
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

        if (sibling.getRight() + component.getWidth() + padding <= component.parent.getRight()) {
            return sibling.getTop()
        }

        return getLowestPoint(sibling, component.parent, index) + padding
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}