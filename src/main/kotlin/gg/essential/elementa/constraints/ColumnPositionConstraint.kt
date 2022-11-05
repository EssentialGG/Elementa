package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import org.jetbrains.annotations.ApiStatus

/**
 * Column Position Constraint is a constraint that allows the creation of a vertically
 * align column of components. The X position of the column is determined by the
 * finding the largest right edge of all siblings one index less than the component
 * and adding the padding.
 *
 * Example:
 * UIContainer ("Box")
 *  - UIContainer ("Row 1")
 *     - UIComponent ("Component 1") with width=15
 *     - UIComponent ("Component 2") with x=ColumnPositionConstraint(10f)
 *  - UIContainer ("Row 2")
 *     - UIComponent ("Component 3") with width=25
 *     - UIComponent ("Component 4") with x=ColumnPositionConstraint(10f)
 *  The X value component 2 and 4 will be 25 + 10 = 35 pixels to the right of Box
 *
 */
class ColumnPositionConstraint(
    padding: State<Float>,
) : XConstraint, PaddingConstraint, StateRegistry {

    private val paddingState = padding.map { it }

    constructor(padding: Float) : this(BasicState(padding))

    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null
        set(_) = throw IllegalArgumentException("Cannot constrain ColumnPositionConstraint to a component!")

    override fun getXPositionImpl(component: UIComponent): Float {
        val currentRow = component.parent
        val previousColumnIndex = currentRow.children.indexOf(component) - 1
        if (previousColumnIndex < 0) {
            return currentRow.getLeft()
        }
        return currentRow.parent.children.maxOf {
            it.children.getOrNull(previousColumnIndex)?.getRight() ?: 0f
        } + paddingState.get()
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
    }

    override fun getHorizontalPadding(component: UIComponent): Float {
        val currentRow = component.parent
        val previousColumnIndex = currentRow.children.indexOf(component) - 1
        return if (previousColumnIndex < 0) {
            return 0f
        } else {
            paddingState.get()
        }
    }

    override fun getVerticalPadding(component: UIComponent): Float {
        return 0f
    }

    @ApiStatus.Internal
    @get:ApiStatus.Internal
    override val managedStates = listOf(
        ManagedState.OfFloat(paddingState, "padding", true)
    )
}