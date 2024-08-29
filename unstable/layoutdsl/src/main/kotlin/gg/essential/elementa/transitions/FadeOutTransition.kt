package gg.essential.elementa.transitions

import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.transitions.BoundTransition
import gg.essential.elementa.effects.AlphaEffect
import kotlin.properties.Delegates

/**
 * Fades a component and all of its children out. This is done using
 * [AlphaEffect]. When the transition is finished, the effect is removed.
 * Typically, one would hide the component after this transition is finished.
 */
class FadeOutTransition @JvmOverloads constructor(
    private val time: Float = 1f,
    private val animationType: Animations = Animations.OUT_EXP,
) : BoundTransition() {

    private val alphaState = BasicState(1f)
    private var alpha by Delegates.observable(1f) { _, _, newValue ->
        alphaState.set(newValue)
    }

        private val effect = AlphaEffect(alphaState)

    override fun beforeTransition() {
        boundComponent.enableEffect(effect)
    }

    override fun doTransition(constraints: AnimatingConstraints) {
        constraints.setExtraDelay(time)
        boundComponent.apply {
            ::alpha.animate(animationType, time, 0f)
        }
    }

    override fun afterTransition() {
        boundComponent.removeEffect(effect)
        effect.cleanup()
    }
}