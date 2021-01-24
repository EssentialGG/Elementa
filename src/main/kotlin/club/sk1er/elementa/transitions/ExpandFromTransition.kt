package club.sk1er.elementa.transitions

import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.plus
import club.sk1er.elementa.dsl.pixels

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
    ) : BoundTransition() {
        private lateinit var widthConstraint: WidthConstraint

        override fun beforeTransition() {
            widthConstraint = boundComponent.constraints.width
            boundComponent.setWidth(0.pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setWidthAnimation(animationType, time, widthConstraint)
        }

        override fun afterTransition() {
            boundComponent.setWidth(widthConstraint)
        }
    }

    /**
     * Initially sets the component's height to 0, and transitions back
     * to the original height.
     */
    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : BoundTransition() {
        private lateinit var heightConstraint: HeightConstraint

        override fun beforeTransition() {
            heightConstraint = boundComponent.constraints.height
            boundComponent.setHeight(0.pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setHeightAnimation(animationType, time, heightConstraint)
        }

        override fun afterTransition() {
            boundComponent.setHeight(heightConstraint)
        }
    }

    /**
     * Initially sets the component's width to 0 and x to getWidth(), and
     * transitions back to the original x and width.
     */
    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : BoundTransition() {
        private lateinit var xConstraint: XConstraint
        private lateinit var widthConstraint: WidthConstraint

        override fun beforeTransition() {
            xConstraint = boundComponent.constraints.x
            widthConstraint = boundComponent.constraints.width
            boundComponent.setX(xConstraint + boundComponent.getWidth().pixels())
            boundComponent.setWidth(0.pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraint)
            constraints.setWidthAnimation(animationType, time, widthConstraint)
        }

        override fun afterTransition() {
            boundComponent.setX(xConstraint)
            boundComponent.setWidth(widthConstraint)
        }
    }

    /**
     * Initially sets the component's height to 0 and y to getHeight(), and
     * transitions back to the original y and height.
     */
    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : BoundTransition() {
        private lateinit var yConstraint: YConstraint
        private lateinit var heightConstraint: HeightConstraint

        override fun beforeTransition() {
            yConstraint = boundComponent.constraints.y
            heightConstraint = boundComponent.constraints.height
            boundComponent.setY(yConstraint + boundComponent.getHeight().pixels())
            boundComponent.setHeight(0.pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraint)
            constraints.setHeightAnimation(animationType, time, heightConstraint)
        }

        override fun afterTransition() {
            boundComponent.setY(yConstraint)
            boundComponent.setHeight(heightConstraint)
        }
    }
}
