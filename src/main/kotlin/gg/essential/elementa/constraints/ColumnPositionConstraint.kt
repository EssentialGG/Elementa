package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import org.jetbrains.annotations.ApiStatus

class ColumnPositionConstraint(
    private val padding: State<Float>,
) : XConstraint, PaddingConstraint, StateRegistry {

    constructor(padding: Float) : this(BasicState(padding))

    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        val target = component.parent
        val index = target.children.indexOf(component) - 1
        if (index < 0) {
            return component.parent.getLeft()
        }
        return target.parent.children.maxOf {
            it.children.getOrNull(index)?.getRight() ?: 0f
        } + padding.get()
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
    }

    override fun getHorizontalPadding(component: UIComponent): Float {
        return padding.get()
    }

    override fun getVerticalPadding(component: UIComponent): Float {
        return 0f
    }

    @ApiStatus.Internal
    override fun getManagedStates(): List<ManagedState> = listOf(
        ManagedState.ManagedFloatState(padding, "padding", true)
    )
}