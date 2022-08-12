package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.MappedState
import gg.essential.elementa.utils.getValue
import org.jetbrains.annotations.ApiStatus
import java.awt.Color
import kotlin.math.sin
import kotlin.random.Random

/**
 * Sets the color to be a constant, determined color.
 */
class ConstantColorConstraint(
    color: State<Color>,
) : ColorConstraint, StateRegistry {
    @JvmOverloads
    constructor(color: Color = Color.WHITE) : this(BasicState(color))

    override var cachedValue: Color = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private val colorState: MappedState<Color, Color> = color.map { it }

    var color: Color
        get() = colorState.get()
        set(value) {
            colorState.set(value)
        }

    fun bindColor(newState: State<Color>) = apply {
        colorState.rebind(newState)
    }

    override fun getColorImpl(component: UIComponent): Color {
        return colorState.get()
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }

    // Color constraints will only ever have parent dependencies, so there is no possibility
    // of an invalid constraint here
    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}

    @ApiStatus.Internal
    override fun getManagedStates(): List<ManagedState> = listOf(
        ManagedState.ManagedColorState(colorState, "color", true)
    )
}

/**
 * Sets the color to be constant but with an alpha based off of its parent.
 */
class AlphaAspectColorConstraint(
    color: State<Color>,
    alphaValue: State<Float>,
) : ColorConstraint, StateRegistry {
    constructor(color: Color = Color.WHITE, alphaValue: Float = 1f) : this(BasicState(color), BasicState(alphaValue))
    constructor() : this(Color.WHITE, 1f)

    override var cachedValue: Color = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private val colorState: MappedState<Color, Color> = color.map { it }
    private val alphaState: MappedState<Float, Float> = alphaValue.map { it }

    var color: Color
        get() = colorState.get()
        set(value) {
            colorState.set(value)
        }
    var alpha: Float
        get() = alphaState.get()
        set(value) {
            alphaState.set(value)
        }

    fun bindColor(newState: State<Color>) = apply {
        colorState.rebind(newState)
    }

    fun bindAlpha(newState: State<Float>) = apply {
        alphaState.rebind(newState)
    }

    override fun getColorImpl(component: UIComponent): Color {
        val (r, g, b) = colorState.get()
        val a = ((constrainTo ?: component.parent).getColor().alpha * alphaState.get()).toInt()
        return Color(r, g, b, a)
    }

    // Color constraints will only ever have parent dependencies, so there is no possibility
    // of an invalid constraint here
    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}

    @ApiStatus.Internal
    override fun getManagedStates(): List<ManagedState> = listOf(
        ManagedState.ManagedColorState(colorState, "color", true),
        ManagedState.ManagedFloatState(alphaState, "alpha", true)
    )
}

/**
 * Changes this component's color every frame, using a sin wave to create
 * a chroma effect.
 */
class RainbowColorConstraint(
    private val alphaState: State<Int>,
    private val speedState: State<Float>,
) : ColorConstraint, StateRegistry {

    @JvmOverloads
    constructor(alpha: Int = 255, speed: Float = 50f) : this(BasicState(alpha), BasicState(speed))

    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private var currentColor: Color = Color.WHITE
    private var currentStep = Random.nextInt(500)

    val speed by speedState
    val alpha by alphaState

    override fun getColorImpl(component: UIComponent): Color {
        return currentColor
    }

    override fun animationFrame() {
        currentStep++

        val red = ((sin((currentStep / speed).toDouble()) + 0.75) * 170).toInt()
        val green = ((sin(currentStep / speed + 2 * Math.PI / 3) + 0.75) * 170).toInt()
        val blue = ((sin(currentStep / speed + 4 * Math.PI / 3) + 0.75) * 170).toInt()

        currentColor = Color(
            red.coerceIn(0, 255),
            green.coerceIn(0, 255),
            blue.coerceIn(0, 255),
            alpha.coerceIn(0, 255)
        )
    }

    override fun to(component: UIComponent) = apply {
        throw java.lang.UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }

    // Color constraints will only ever have parent dependencies, so there is no possibility
    // of an invalid constraint here
    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}

    @ApiStatus.Internal
    override fun getManagedStates(): List<ManagedState> = listOf(
        ManagedState.ManagedFloatState(speedState, "speed", true),
        ManagedState.ManagedIntState(alphaState, "alpha", true)
    )
}
