package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.getValue
import gg.essential.elementa.utils.setValue
import gg.essential.universal.UGraphics
import org.jetbrains.annotations.ApiStatus
import java.lang.UnsupportedOperationException

/**
 * Sets the width/height to be a scale of the default text width and height
 */
class ScaledTextConstraint(
    private val scaleState: State<Float>,
) : SizeConstraint, StateRegistry {

    constructor(scale: Float): this(BasicState(scale))

    var scale: Float by scaleState

    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null


    override fun getWidthImpl(component: UIComponent): Float {
        return when (component) {
            is UIText -> scale * UGraphics.getStringWidth(component.getText())
            else -> throw IllegalAccessException("ScaledTextConstraint can only be used with UIText")
        }
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return when(component) {
            is UIText -> scale * 9
            else -> throw IllegalAccessException("ScaledTextConstraint can only be used with UIText")
        }
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        throw IllegalAccessException("ScaledTextConstraint cannot be used as a radius")
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }

    @ApiStatus.Internal
    @get:ApiStatus.Internal
    override val managedStates = listOf(
        ManagedState.OfFloat(scaleState, "scale", true),
    )
}
