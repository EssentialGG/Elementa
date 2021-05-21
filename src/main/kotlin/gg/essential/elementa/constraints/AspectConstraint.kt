package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor

/**
 * For size:
 * Sets the width/height to be [value] multiple of its own height/width respectively.
 *
 * For position:
 * Sets the x/y position to be [value] multiple of its own y/x position respectively.
 */
class AspectConstraint @JvmOverloads constructor(val value: Float = 1f) : PositionConstraint, SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getTop() * value
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getLeft()* value
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getHeight() * value
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getWidth() * value
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getRadius() * value
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.X -> visitor.visitSelf(ConstraintType.Y)
            ConstraintType.Y -> visitor.visitSelf(ConstraintType.X)
            ConstraintType.WIDTH -> visitor.visitSelf(ConstraintType.HEIGHT)
            ConstraintType.HEIGHT -> visitor.visitSelf(ConstraintType.WIDTH)
            ConstraintType.RADIUS -> {} // TODO: ???
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}