package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.getValue
import gg.essential.elementa.utils.setValue
import org.jetbrains.annotations.ApiStatus
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

/**
 * Rounds a constraint to an Int value using one of three modes: floor,
 * ceil, and rounding. Rounding is the default.
 */
class RoundingConstraint constructor(
    val constraint: SuperConstraint<Float>,
    private val roundingModeState: State<Mode>,
) : MasterConstraint, StateRegistry {

    @JvmOverloads constructor(
        constraint: SuperConstraint<Float>,
        roundingMode: Mode = Mode.Round,
    ): this(constraint, BasicState(roundingMode))

    var roundingMode by roundingModeState

    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return doRound((constraint as XConstraint).getXPositionImpl(component))
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return doRound((constraint as YConstraint).getYPositionImpl(component))
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return doRound((constraint as WidthConstraint).getWidthImpl(component))
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return doRound((constraint as HeightConstraint).getHeightImpl(component))
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return doRound((constraint as RadiusConstraint).getRadiusImpl(component))
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        constraint.visit(visitor, type)
    }

    private fun doRound(number: Float) = when (roundingMode) {
        Mode.Floor -> floor(number)
        Mode.Ceil -> ceil(number)
        Mode.Round -> round(number)
    }

    enum class Mode {
       Floor,
       Ceil,
       Round,
   }

    @ApiStatus.Internal
    @get:ApiStatus.Internal
    override val managedStates = listOf(
        ManagedState.OfEnum(roundingModeState, "mode", true),
    )
}
