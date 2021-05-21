package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.resolution.ConstraintVisitor

/**
 * Sets this component's X/Y position or width/height to be some percentage
 * of the Window
 */
class RelativeWindowConstraint @JvmOverloads constructor(val value: Float = 1f) : PositionConstraint, SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        ensureConstrainedToWindow(component)
        return constrainTo!!.getLeft() + getWidth(component)
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        ensureConstrainedToWindow(component)
        return constrainTo!!.getTop() + getHeight(component)
    }

    override fun getWidthImpl(component: UIComponent): Float {
        ensureConstrainedToWindow(component)
        return constrainTo!!.getWidth() * value
    }

    override fun getHeightImpl(component: UIComponent): Float {
        ensureConstrainedToWindow(component)
        return constrainTo!!.getHeight() * value
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        ensureConstrainedToWindow(component)
        return (constrainTo!!.getWidth() * value) / 2f
    }

    override fun to(component: UIComponent): SuperConstraint<Float> {
        throw IllegalStateException("RelativeWindowConstraint cannot be bound to components")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        // TODO: Some kind of visitWindow method?
    }

    private fun ensureConstrainedToWindow(component: UIComponent) {
        if (constrainTo == null)
            constrainTo = Window.of(component)
    }
}
