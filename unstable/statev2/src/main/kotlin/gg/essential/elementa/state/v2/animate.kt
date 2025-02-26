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
    return State { stateOf(this@animateTransitions()) }.animateTransitions(driverComponent, duration, animationStrategy)
}

@JvmName("animateTransitionsNested")
fun State<State<Float>>.animateTransitions(
    driverComponent: UIComponent,
    duration: Float,
    animationStrategy: AnimationStrategy = Animations.OUT_EXP,
): State<Float> {
    if (duration <= 0f) {
        return State { this@animateTransitions()() }
    }
    val resultState = mutableStateOf(this.getUntracked())
    driverComponent.enableEffect(AnimationDriver(this, WeakReference(resultState), duration, animationStrategy))
    return memo { resultState()() }
}

private class AnimationDriver(
    private val driver: State<State<Float>>,
    private val resultStateWeakReference: WeakReference<MutableState<State<Float>>>,
    private val duration: Float,
    private val animationStrategy: AnimationStrategy
): Effect() {
    private val animationEventList = mutableListOf<AnimationEvent>()
    private lateinit var driverEffect: () -> Unit
    private var durationFrames = 1

    private var previousDriverStateValue = stateOf(0f)
    private var isDestroying = false

    override fun setup() {
        previousDriverStateValue = driver.getUntracked()
        durationFrames = (Window.of(boundComponent).animationFPS * duration).toInt().coerceAtLeast(1)
        driverEffect = driver.onChange(ReferenceHolder.Weak) { input ->
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
            resultState.set(State { getAnimationValue() })
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

    private fun Observer.getAnimationValue(): Float {
        if (animationEventList.isEmpty()) {
            return previousDriverStateValue()
        }

        return animationEventList.fold(animationEventList.first().startValue()) { acc, event ->
            val linearProgress = event.age.toFloat() / event.duration.toFloat()
            val animatedProgress = animationStrategy.getValue(linearProgress)
            acc + ((event.endValue() - acc) * animatedProgress)
        }
    }

    private data class AnimationEvent(
        val startValue: State<Float>,
        val endValue: State<Float>,
        val duration: Int,
        var age: Int = 0,
    )

}