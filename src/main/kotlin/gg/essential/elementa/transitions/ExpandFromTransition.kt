package gg.essential.elementa.transitions

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.pixels

/**
 * Sets the width/height of the component to zero, and transitions
 * it to its original value such that it expands from the respective
 * direction.
 */
object ExpandFromTransition {
    /**
     * Initially sets the component's width to 0, and transitions back
     * to the original width.
     */
    class Left @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val widthConstraints = mutableMapOf<UIComponent, WidthConstraint>()

        override fun beforeTransition(component: UIComponent) {
            widthConstraints[component] = component.constraints.width
            component.setWidth(0.pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setWidthAnimation(animationType, time, widthConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setWidth(widthConstraints[component]!!)
            widthConstraints.remove(component)
        }
    }

    /**
     * Initially sets the component's height to 0, and transitions back
     * to the original height.
     */
    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val heightConstraints = mutableMapOf<UIComponent, HeightConstraint>()

        override fun beforeTransition(component: UIComponent) {
            heightConstraints[component] = component.constraints.height
            component.setHeight(0.pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setHeightAnimation(animationType, time, heightConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setHeight(heightConstraints[component]!!)
            heightConstraints.remove(component)
        }
    }

    /**
     * Initially sets the component's width to 0 and x to getWidth(), and
     * transitions back to the original x and width.
     */
    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val xConstraints = mutableMapOf<UIComponent, XConstraint>()
        private val widthConstraints = mutableMapOf<UIComponent, WidthConstraint>()

        override fun beforeTransition(component: UIComponent) {
            xConstraints[component] = component.constraints.x
            widthConstraints[component] = component.constraints.width
            component.setX(component.constraints.x + component.getWidth().pixels())
            component.setWidth(0.pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraints[component]!!)
            constraints.setWidthAnimation(animationType, time, widthConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setX(xConstraints[component]!!)
            component.setWidth(widthConstraints[component]!!)
            xConstraints.remove(component)
            widthConstraints.remove(component)
        }
    }

    /**
     * Initially sets the component's height to 0 and y to getHeight(), and
     * transitions back to the original y and height.
     */
    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : Transition() {
        private val yConstraints = mutableMapOf<UIComponent, YConstraint>()
        private val heightConstraints = mutableMapOf<UIComponent, HeightConstraint>()

        override fun beforeTransition(component: UIComponent) {
            yConstraints[component] = component.constraints.y
            heightConstraints[component] = component.constraints.height
            component.setY(component.constraints.y + component.getHeight().pixels())
            component.setHeight(0.pixels())
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraints[component]!!)
            constraints.setHeightAnimation(animationType, time, heightConstraints[component]!!)
        }

        override fun afterTransition(component: UIComponent) {
            component.setY(yConstraints[component]!!)
            component.setHeight(heightConstraints[component]!!)
            yConstraints.remove(component)
            heightConstraints.remove(component)
        }
    }
}
