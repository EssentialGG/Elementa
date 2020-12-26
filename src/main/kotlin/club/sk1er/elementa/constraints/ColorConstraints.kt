package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import club.sk1er.elementa.state.state
import java.awt.Color
import kotlin.math.sin
import kotlin.random.Random

/**
 * Sets the color to be a constant, determined color.
 */
class ConstantColorConstraint(color: Color = Color.WHITE) : ColorConstraint {
    var color by state(color)

    override var cachedValue: Color = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    fun bindColor(newState: State<Color>) = apply {
        State.setDelegate(::color, newState)
    }

    override fun getColorImpl(component: UIComponent): Color {
        return color
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }

    // Color constraints will only ever have parent dependencies, so there is no possibility
    // of an invalid constraint here
    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

/**
 * Sets the color to be constant but with an alpha based off of its parent.
 */
class AlphaAspectColorConstraint(color: Color = Color.WHITE, alphaValue: Float = 1f) : ColorConstraint {
    var color by state(color)
    var alpha by state(alphaValue)

    override var cachedValue: Color = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    fun bindColor(newState: State<Color>) = apply {
        State.setDelegate(::color, newState)
    }

    fun bindAlpha(newState: State<Float>) = apply {
        State.setDelegate(::alpha, newState)
    }

    override fun getColorImpl(component: UIComponent): Color {
        return color.let { color ->
            Color(
                color.red,
                color.green,
                color.blue,
                ((constrainTo ?: component.parent).getColor().alpha * alpha).toInt()
            )
        }
    }

    // Color constraints will only ever have parent dependencies, so there is no possibility
    // of an invalid constraint here
    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

/**
 * Changes this component's color every frame, using a sin wave to create
 * a chroma effect.
 */
class RainbowColorConstraint(val alpha: Int = 255, val speed: Float = 50f) : ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private var currentColor: Color = Color.WHITE
    private var currentStep = Random.nextInt(500)

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
    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}
