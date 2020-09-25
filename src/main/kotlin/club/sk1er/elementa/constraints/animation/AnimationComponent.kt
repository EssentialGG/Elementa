package club.sk1er.elementa.constraints.animation

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.*
import java.awt.Color
import java.lang.UnsupportedOperationException
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.reflect.KMutableProperty0

sealed class AnimationComponent<T>(
    val strategy: AnimationStrategy,
    val totalFrames: Int,
    val delayFrames: Int
) : SuperConstraint<T> {
    var elapsedFrames = 0
    var animationPaused = false

    override fun animationFrame() {
        super.animationFrame()

        if (isComplete() || animationPaused) return

        elapsedFrames++
    }

    fun stop() {
        elapsedFrames = totalFrames + delayFrames
    }

    fun pause() {
        animationPaused = true
    }

    fun resume() {
        animationPaused = false
    }

    fun isComplete() = elapsedFrames - delayFrames >= totalFrames

    fun getPercentComplete() = strategy.getValue(max(elapsedFrames - delayFrames, 0).toFloat() / totalFrames.toFloat())
}

class XAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    val oldConstraint: XConstraint,
    val newConstraint: XConstraint,
    delay: Int
) : AnimationComponent<Float>(strategy, totalFrames, delay), XConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        val startX = oldConstraint.getXPosition(component)
        val finalX = newConstraint.getXPosition(component)

        return startX + ((finalX - startX) * getPercentComplete())
    }

    // TODO: This is gross, can probably be done in parent!
    override fun animationFrame() {
        super<AnimationComponent>.animationFrame()

        oldConstraint.animationFrame()
        newConstraint.animationFrame()
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

class YAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    val oldConstraint: YConstraint,
    val newConstraint: YConstraint,
    delay: Int
) : AnimationComponent<Float>(strategy, totalFrames, delay), YConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getYPositionImpl(component: UIComponent): Float {
        val startX = oldConstraint.getYPosition(component)
        val finalX = newConstraint.getYPosition(component)

        return startX + ((finalX - startX) * getPercentComplete())
    }
    override fun animationFrame() {
        super<AnimationComponent>.animationFrame()

        oldConstraint.animationFrame()
        newConstraint.animationFrame()
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

class RadiusAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    val oldConstraint: RadiusConstraint,
    val newConstraint: RadiusConstraint,
    delay: Int
) : AnimationComponent<Float>(strategy, totalFrames, delay), RadiusConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getRadiusImpl(component: UIComponent): Float {
        val startX = oldConstraint.getRadius(component)
        val finalX = newConstraint.getRadius(component)

        return startX + ((finalX - startX) * getPercentComplete())
    }

    override fun animationFrame() {
        super<AnimationComponent>.animationFrame()

        oldConstraint.animationFrame()
        newConstraint.animationFrame()
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

class WidthAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    val oldConstraint: WidthConstraint,
    val newConstraint: WidthConstraint,
    delay: Int
) : AnimationComponent<Float>(strategy, totalFrames, delay), WidthConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent): Float {
        val startX = oldConstraint.getWidth(component)
        val finalX = newConstraint.getWidth(component)

        return startX + ((finalX - startX) * getPercentComplete())
    }

    override fun animationFrame() {
        super<AnimationComponent>.animationFrame()

        oldConstraint.animationFrame()
        newConstraint.animationFrame()
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

class HeightAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    val oldConstraint: HeightConstraint,
    val newConstraint: HeightConstraint,
    delay: Int
) : AnimationComponent<Float>(strategy, totalFrames, delay), HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getHeightImpl(component: UIComponent): Float {
        val startX = oldConstraint.getHeight(component)
        val finalX = newConstraint.getHeight(component)

        return startX + ((finalX - startX) * getPercentComplete())
    }

    override fun animationFrame() {
        super<AnimationComponent>.animationFrame()

        oldConstraint.animationFrame()
        newConstraint.animationFrame()
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

class ColorAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    val oldConstraint: ColorConstraint,
    val newConstraint: ColorConstraint,
    delay: Int
) : AnimationComponent<Color>(strategy, totalFrames, delay), ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getColorImpl(component: UIComponent): Color {
        val startColor = oldConstraint.getColor(component)
        val endColor = newConstraint.getColor(component)
        val percentComplete = getPercentComplete()

        val newR = startColor.red + ((endColor.red - startColor.red) * percentComplete)
        val newG = startColor.green + ((endColor.green - startColor.green) * percentComplete)
        val newB = startColor.blue + ((endColor.blue - startColor.blue) * percentComplete)
        val newA = startColor.alpha + ((endColor.alpha - startColor.alpha) * percentComplete)

        return Color(newR.roundToInt(), newG.roundToInt(), newB.roundToInt(), newA.roundToInt())
    }

    override fun animationFrame() {
        super<AnimationComponent>.animationFrame()

        oldConstraint.animationFrame()
        newConstraint.animationFrame()
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

sealed class FieldAnimationComponent<T>(
    strategy: AnimationStrategy,
    totalFrames: Int,
    delay: Int
): AnimationComponent<T>(strategy, totalFrames, delay) {
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    abstract val field: KMutableProperty0<*>

    override fun animationFrame() {
        super.animationFrame()

        if (!isComplete())
            setValue(getPercentComplete())
    }

    abstract fun setValue(percentComplete: Float)

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}

class FloatFieldAnimationComponent(
    override val field: KMutableProperty0<Float>,
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldValue: Float,
    private val newValue: Float,
    delay: Int
): FieldAnimationComponent<Float>(strategy, totalFrames, delay) {
    override var cachedValue = 0f

    override fun setValue(percentComplete: Float) {
        field.set(oldValue + percentComplete * (newValue - oldValue))
    }
}

class DoubleFieldAnimationComponent(
    override val field: KMutableProperty0<Double>,
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldValue: Double,
    private val newValue: Double,
    delay: Int
): FieldAnimationComponent<Double>(strategy, totalFrames, delay) {
    override var cachedValue = 0.0

    override fun setValue(percentComplete: Float) {
        field.set(oldValue + percentComplete * (newValue - oldValue))
    }
}

class IntFieldAnimationComponent(
    override val field: KMutableProperty0<Int>,
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldValue: Int,
    private val newValue: Int,
    delay: Int
): FieldAnimationComponent<Int>(strategy, totalFrames, delay) {
    override var cachedValue = 0

    override fun setValue(percentComplete: Float) {
        field.set((oldValue + percentComplete * (newValue - oldValue)).toInt())
    }
}

class LongFieldAnimationComponent(
    override val field: KMutableProperty0<Long>,
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldValue: Long,
    private val newValue: Long,
    delay: Int
): FieldAnimationComponent<Long>(strategy, totalFrames, delay) {
    override var cachedValue = 0L

    override fun setValue(percentComplete: Float) {
        field.set((oldValue + percentComplete * (newValue - oldValue)).toLong())
    }
}

class ColorFieldAnimationComponent(
    override val field: KMutableProperty0<Color>,
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldValue: Color,
    private val newValue: Color,
    delay: Int
): FieldAnimationComponent<Color>(strategy, totalFrames, delay) {
    override var cachedValue: Color = Color.WHITE

    override fun setValue(percentComplete: Float) {
        val newR = oldValue.red + ((newValue.red - oldValue.red) * percentComplete)
        val newG = oldValue.green + ((newValue.green - oldValue.green) * percentComplete)
        val newB = oldValue.blue + ((newValue.blue - oldValue.blue) * percentComplete)
        val newA = oldValue.alpha + ((newValue.alpha - oldValue.alpha) * percentComplete)

        field.set(Color(newR.roundToInt(), newG.roundToInt(), newB.roundToInt(), newA.roundToInt()))
    }
}
