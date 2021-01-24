package club.sk1er.elementa.transitions

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.effects.RecursiveFadeEffect
import club.sk1er.elementa.state.BasicState
import kotlin.properties.Delegates

/**
 * Fades a component and all of its children to alpha 100%. When
 * the transition starts, the alpha is set to 0%, and climbs to
 * 100% during the transition. When the transition is over, the
 * effect is removed and all of the component alphas will be
 * set to their original value.
 */
class RecursiveFadeInTransition @JvmOverloads constructor(
    private val time: Float = 1f,
    private val animationType: Animations = Animations.OUT_EXP
) : Transition() {
    private val isOverridden = BasicState(false)
    private val overriddenAlphaPercentage = BasicState(0f)
    private var alpha by Delegates.observable(0f) { _, _, newValue ->
        overriddenAlphaPercentage.set(newValue)
    }
    private val effect = RecursiveFadeEffect(isOverridden, overriddenAlphaPercentage)

    override fun beforeTransition(component: UIComponent) {
        effect.bindComponent(component)
        component.effects.add(effect)
        effect.setup()

        alpha = 0f
        isOverridden.set(true)
    }

    override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
        constraints.setExtraDelay(time)
        component.apply {
            ::alpha.animate(animationType, time, 1f)
        }
    }

    override fun afterTransition(component: UIComponent) {
        effect.remove()
    }
}
