package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import club.sk1er.elementa.state.state

/**
 * Sets this component's X/Y position or width/height to be some
 * multiple of its parents.
 */
class RelativeConstraint @JvmOverloads constructor(value: Float = 1f) : PositionConstraint, SizeConstraint {
    var value by state(value)

    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    fun bindValue(newState: State<Float>) = apply {
        State.setDelegate(::value, newState)
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getLeft() + getWidth(component)
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getTop() + getHeight(component)
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getWidth() * value
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getHeight() * value
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return ((constrainTo ?: component.parent).getWidth() * value) / 2f
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.X -> {
                visitor.visitParent(ConstraintType.X)
                visitor.visitParent(ConstraintType.WIDTH)
            }
            ConstraintType.Y -> {
                visitor.visitParent(ConstraintType.Y)
                visitor.visitParent(ConstraintType.HEIGHT)
            }
            ConstraintType.WIDTH -> visitor.visitParent(ConstraintType.WIDTH)
            ConstraintType.HEIGHT -> visitor.visitParent(ConstraintType.HEIGHT)
            ConstraintType.RADIUS -> visitor.visitParent(ConstraintType.WIDTH)
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}
