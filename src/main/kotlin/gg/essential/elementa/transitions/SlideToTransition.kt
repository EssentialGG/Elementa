package gg.essential.elementa.transitions

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.XConstraint
import gg.essential.elementa.constraints.YConstraint
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus

/**
 * Slides a component towards a certain direction by animations its
 * x or y constraint. Can optionally restore the original constraint
 * when finished.
 */
object SlideToTransition {
    class Left @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val xConstraints = mutableMapOf<UIComponent, XConstraint>()

        override fun beforeTransition(component: UIComponent) {
            xConstraints[component] = component.constraints.x
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraints[component]!! - component.getWidth().pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints)
                component.setX(xConstraints[component]!!)
            xConstraints.remove(component)
        }
    }

    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val yConstraints = mutableMapOf<UIComponent, YConstraint>()

        override fun beforeTransition(component: UIComponent) {
            yConstraints[component] = component.constraints.y
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraints[component]!! - component.getHeight().pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints)
                component.setY(yConstraints[component]!!)
            yConstraints.remove(component)
        }
    }

    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val xConstraints = mutableMapOf<UIComponent, XConstraint>()

        override fun beforeTransition(component: UIComponent) {
            xConstraints[component] = component.constraints.x
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraints[component]!! + component.getWidth().pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints)
                component.setX(xConstraints[component]!!)
            xConstraints.remove(component)
        }
    }

    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val yConstraints = mutableMapOf<UIComponent, YConstraint>()

        override fun beforeTransition(component: UIComponent) {
            yConstraints[component] = component.constraints.y
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraints[component]!! + component.getHeight().pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints)
                component.setY(yConstraints[component]!!)
            yConstraints.remove(component)
        }
    }
}

