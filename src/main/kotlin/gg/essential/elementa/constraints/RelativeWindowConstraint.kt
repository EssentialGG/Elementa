package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.getValue
import org.jetbrains.annotations.ApiStatus

/**
 * Sets this component's X/Y position or width/height to be some percentage
 * of the Window
 */
class RelativeWindowConstraint(
    private val valueState: State<Float>,
) : PositionConstraint, SizeConstraint, StateRegistry {
    @JvmOverloads constructor(value: Float = 1f): this(BasicState(value))

    val value by valueState

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

    @ApiStatus.Internal
    @get:ApiStatus.Internal
    override val managedStates = listOf(
        ManagedState.OfFloat(valueState, "value", true),
    )
}
