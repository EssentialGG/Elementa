package gg.essential.elementa.transitions

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.effects.RecursiveFadeEffect
import gg.essential.elementa.state.BasicState
import kotlin.properties.Delegates

/**
 * Fades a component and all of its children to alpha 0. When
 * the transition is finished, the fade effect is removed,
 * meaning that the component will go back to its original
 * transparency. Typically, one would hide the component
 * after this transition is finished.
 */
class RecursiveFadeOutTransition @JvmOverloads constructor(
    private val time: Float = 1f,
    private val animationType: Animations = Animations.OUT_EXP
) : Transition() {
    private val isOverridden = BasicState(false)
    private val overriddenAlphaPercentage = BasicState(1f)
    private var alpha by Delegates.observable(1f) { _, _, newValue ->
        overriddenAlphaPercentage.set(newValue)
    }
    private val effect = RecursiveFadeEffect(isOverridden, overriddenAlphaPercentage)

    override fun beforeTransition(component: UIComponent) {
        effect.bindComponent(component)
        component.effects.add(effect)
        effect.setup()

        alpha = 1f
        isOverridden.set(true)
    }

    override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
        constraints.setExtraDelay(time)
        component.apply {
            ::alpha.animate(animationType, time, 0f)
        }
    }

    override fun afterTransition(component: UIComponent) {
        effect.remove()
    }
}
