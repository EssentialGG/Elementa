package gg.essential.elementa.transitions

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.XConstraint
import gg.essential.elementa.constraints.YConstraint
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.pixels

/**
 * Transitions the component in from the respective direction.
 */
object SlideFromTransition {
    /**
     * Initially sets the component's x position to currentX - getWidth().
     * Transitions back to currentX.
     */
    class Left @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val xConstraints = mutableMapOf<UIComponent, XConstraint>()

        override fun beforeTransition(component: UIComponent) {
            xConstraints[component] = component.constraints.x
            component.setX(xConstraints[component]!! - component.getWidth().pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setX(xConstraints[component]!!)
            xConstraints.remove(component)
        }
    }

    /**
     * Initially sets the component's y position to currentY - getHeight().
     * Transitions back to currentY.
     */
    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val yConstraints = mutableMapOf<UIComponent, YConstraint>()

        override fun beforeTransition(component: UIComponent) {
            yConstraints[component] = component.constraints.y
            component.setY(yConstraints[component]!! - component.getHeight().pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setY(yConstraints[component]!!)
            yConstraints.remove(component)
        }
    }

    /**
     * Initially sets the component's x position to currentX + getWidth().
     * Transitions back to currentX.
     */
    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val xConstraints = mutableMapOf<UIComponent, XConstraint>()

        override fun beforeTransition(component: UIComponent) {
            xConstraints[component] = component.constraints.x
            component.setX(xConstraints[component]!! + component.getWidth().pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setX(xConstraints[component]!!)
            xConstraints.remove(component)
        }
    }

    /**
     * Initially sets the component's y position to currentY + getHeight().
     * Transitions back to currentY.
     */
    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val yConstraints = mutableMapOf<UIComponent, YConstraint>()

        override fun beforeTransition(component: UIComponent) {
            yConstraints[component] = component.constraints.y
            component.setY(yConstraints[component]!! + component.getHeight().pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setY(yConstraints[component]!!)
            yConstraints.remove(component)
        }
    }
}
