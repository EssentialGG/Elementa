package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.getValue
import org.jetbrains.annotations.ApiStatus

/**
 * For size:
 * Sets the width/height to be [value] multiple of its own height/width respectively.
 *
 * For position:
 * Sets the x/y position to be [value] multiple of its own y/x position respectively.
 */
class AspectConstraint(private val valueState: State<Float>) : PositionConstraint, SizeConstraint, StateRegistry {

    @JvmOverloads constructor(value: Float = 1f) : this(BasicState(value))

    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    val value by valueState

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getTop() * valueState.get()
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getLeft()* valueState.get()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getHeight() * valueState.get()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getWidth() * valueState.get()
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constrainTo ?: component).getRadius() * valueState.get()
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

    @ApiStatus.Internal
    @get:ApiStatus.Internal
    override val managedStates = listOf(
        ManagedState.ManagedFloatState(valueState, "value", true)
    )
}