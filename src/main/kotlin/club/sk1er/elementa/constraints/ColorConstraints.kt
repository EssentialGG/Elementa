package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.mods.core.universal.UniversalMathHelper
import java.awt.Color
import kotlin.math.sin
import kotlin.random.Random

/**
 * Sets the color to be a constant, determined color.
 */
class ConstantColorConstraint(val color: Color) : ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

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
class AlphaAspectColorConstraint(val color: Color, val value: Float = 1f) : ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getColorImpl(component: UIComponent): Color {
        return Color(color.red, color.green, color.blue, ((constrainTo
                ?: component.parent).getColor().alpha * value).toInt())
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
            UniversalMathHelper.clamp_int(red, 0, 255),
            UniversalMathHelper.clamp_int(green, 0, 255),
            UniversalMathHelper.clamp_int(blue, 0, 255),
            UniversalMathHelper.clamp_int(alpha, 0, 255)
        )
    }

    override fun to(component: UIComponent) = apply {
        throw java.lang.UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }

    // Color constraints will only ever have parent dependencies, so there is no possibility
    // of an invalid constraint here
    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}