package gg.essential.elementa.state.v2

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.animation.AnimationStrategy
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.effects.Effect
import gg.essential.elementa.state.v2.ReferenceHolder
import java.lang.ref.WeakReference

fun State<Float>.animateTransitions(
    driverComponent: UIComponent,
    duration: Float,
    animationStrategy: AnimationStrategy = Animations.OUT_EXP,
): State<Float> {
    if (duration <= 0f) {
        return this
    }
    val resultState = mutableStateOf(this.getUntracked())
    driverComponent.enableEffect(AnimationDriver(this, WeakReference(resultState), duration, animationStrategy))
    return resultState
}

private class AnimationDriver(
    private val driver: State<Float>,
    private val resultStateWeakReference: WeakReference<MutableState<Float>>,
    private val duration: Float,
    private val animationStrategy: AnimationStrategy
): Effect() {
    private val animationEventList = mutableListOf<AnimationEvent>()
    private lateinit var driverEffect: () -> Unit
    private var durationFrames = 1

    private var previousDriverStateValue = 0f
    private var isDestroying = false

    override fun setup() {
        previousDriverStateValue = driver.getUntracked()
        durationFrames = (Window.of(boundComponent).animationFPS * duration).toInt().coerceAtLeast(1)
        driverEffect = effect(ReferenceHolder.Weak) {
            val input = driver()
            animationEventList.add(AnimationEvent(previousDriverStateValue, input, durationFrames))
            previousDriverStateValue = input
        }
    }

    override fun animationFrame() {
        val resultState = resultStateWeakReference.get()
        if (resultState == null) {
            destroy()
        } else {
            animationEventList.forEach { it.age++ }
            animationEventList.removeIf { it.age >= durationFrames }
            resultState.set(getAnimationValue())
        }
    }

    private fun destroy() {
        if (isDestroying) {
            return
        }
        isDestroying = true
        driverEffect()
        Window.enqueueRenderOperation {
            boundComponent.removeEffect(this)
        }
    }

    private fun getAnimationValue(): Float {
        if (animationEventList.isEmpty()) {
            return previousDriverStateValue
        }

        return animationEventList.fold(animationEventList.first().startValue) { acc, event ->
            val linearProgress = event.age.toFloat() / event.duration.toFloat()
            val animatedProgress = animationStrategy.getValue(linearProgress)
            acc + ((event.endValue - acc) * animatedProgress)
        }
    }

    private data class AnimationEvent(
        val startValue: Float,
        val endValue: Float,
        val duration: Int,
        var age: Int = 0,
    )

}