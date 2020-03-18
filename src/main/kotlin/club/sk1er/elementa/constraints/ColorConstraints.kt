package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import net.minecraft.util.MathHelper
import java.awt.Color
import kotlin.math.sin
import kotlin.random.Random

/**
 * Sets the color to be a constant, determined color.
 */
class ConstantColorConstraint(private val color: Color) : ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getColorImpl(component: UIComponent): Color {
        return color
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

/**
 * Sets the color to be constant but with an alpha based off of its parent.
 */
class AlphaAspectColorConstraint(private val color: Color, private val value: Float = 1f): ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getColorImpl(component: UIComponent): Color {
        return Color(color.red, color.green, color.blue, ((constrainTo ?: component.parent).getColor().alpha * value).toInt())
    }
}

/**
 * Changes this component's color every frame, using a sin wave to create
 * a chroma effect.
 */
class RainbowColorConstraint(private val alpha: Int = 255, private val speed: Float = 50f) : ColorConstraint {
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
            MathHelper.clamp_int(red, 0, 255),
            MathHelper.clamp_int(green, 0, 255),
            MathHelper.clamp_int(blue, 0, 255),
            MathHelper.clamp_int(alpha, 0, 255)
        )
    }

    override fun to(component: UIComponent) = apply {
        throw java.lang.UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}