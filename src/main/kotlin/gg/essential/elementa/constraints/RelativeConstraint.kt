package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.MappedState
import gg.essential.elementa.state.State

/**
 * Sets this component's X/Y position or width/height to be some
 * multiple of its parents.
 */
class RelativeConstraint constructor(value: State<Float>) : PositionConstraint, SizeConstraint {
    @JvmOverloads constructor(value: Float = 1f) : this(BasicState(value))
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private val valueState: MappedState<Float, Float> = value.map { it }

    var value: Float
        get() = valueState.get()
        set(value) { valueState.set(value) }

    fun bindValue(newState: State<Float>) = apply {
        valueState.rebind(newState)
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getLeft() + getWidth(component)
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getTop() + getHeight(component)
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getWidth() * valueState.get()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component.parent).getHeight() * valueState.get()
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return ((constrainTo ?: component.parent).getWidth() * valueState.get()) / 2f
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
