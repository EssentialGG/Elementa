package gg.essential.elementa.transitions

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.HeightConstraint
import gg.essential.elementa.constraints.WidthConstraint
import gg.essential.elementa.constraints.XConstraint
import gg.essential.elementa.constraints.YConstraint
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.pixels

/**
 * Sets the width/height of the component to zero, as well as modifies the
 * component's x/y (if necessary), in such a way that the component shrinks
 * towards the respective direction. Can optionally restore the original
 * constraints when finished.
 */
object ShrinkToTransition {
    class Left @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val widthConstraints = mutableMapOf<UIComponent, WidthConstraint>()

        override fun beforeTransition(component: UIComponent) {
            widthConstraints[component] = component.constraints.width
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setWidthAnimation(animationType, time, 0.pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints)
                component.setWidth(widthConstraints[component]!!)
            widthConstraints.remove(component)
        }
    }

    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val heightConstraints = mutableMapOf<UIComponent, HeightConstraint>()

        override fun beforeTransition(component: UIComponent) {
            heightConstraints[component] = component.constraints.height
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setHeightAnimation(animationType, time, 0.pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints)
                component.setHeight(heightConstraints[component]!!)
            heightConstraints.remove(component)
        }
    }

    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val xConstraints = mutableMapOf<UIComponent, XConstraint>()
        private val widthConstraints = mutableMapOf<UIComponent, WidthConstraint>()

        override fun beforeTransition(component: UIComponent) {
            xConstraints[component] = component.constraints.x
            widthConstraints[component] = component.constraints.width
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setWidthAnimation(animationType, time, 0.pixels())
            constraints.setXAnimation(animationType, time, xConstraints[component]!! + component.getWidth().pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints) {
                component.setX(xConstraints[component]!!)
                component.setWidth(widthConstraints[component]!!)
            }
            xConstraints.remove(component)
            widthConstraints.remove(component)
        }
    }

    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : Transition() {
        private val yConstraints = mutableMapOf<UIComponent, YConstraint>()
        private val heightConstraints = mutableMapOf<UIComponent, HeightConstraint>()

        override fun beforeTransition(component: UIComponent) {
            yConstraints[component] = component.constraints.y
            heightConstraints[component] = component.constraints.height
        }

        override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
            constraints.setHeightAnimation(animationType, time, 0.pixels())
            constraints.setYAnimation(animationType, time, yConstraints[component]!! + component.getHeight().pixels())
        }

        override fun afterTransition(component: UIComponent) {
            if (restoreConstraints) {
                component.setY(yConstraints[component]!!)
                component.setHeight(heightConstraints[component]!!)
            }
            yConstraints.remove(component)
            heightConstraints.remove(component)
        }
    }
}
