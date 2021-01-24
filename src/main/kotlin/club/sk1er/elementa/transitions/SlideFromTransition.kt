package club.sk1er.elementa.transitions

import club.sk1er.elementa.constraints.XConstraint
import club.sk1er.elementa.constraints.YConstraint
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.plus
import club.sk1er.elementa.dsl.minus
import club.sk1er.elementa.dsl.pixels

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
    ) : BoundTransition() {
        private lateinit var xConstraint: XConstraint

        override fun beforeTransition() {
            xConstraint = boundComponent.constraints.x
            boundComponent.setX(xConstraint - boundComponent.getWidth().pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraint)
        }

        override fun afterTransition() {
            boundComponent.setX(xConstraint)
        }
    }

    /**
     * Initially sets the component's y position to currentY - getHeight().
     * Transitions back to currentY.
     */
    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : BoundTransition() {
        private lateinit var yConstraint: YConstraint

        override fun beforeTransition() {
            yConstraint = boundComponent.constraints.y
            boundComponent.setY(yConstraint - boundComponent.getHeight().pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraint)
        }

        override fun afterTransition() {
            boundComponent.setY(yConstraint)
        }
    }

    /**
     * Initially sets the component's x position to currentX + getWidth().
     * Transitions back to currentX.
     */
    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : BoundTransition() {
        private lateinit var xConstraint: XConstraint

        override fun beforeTransition() {
            xConstraint = boundComponent.constraints.x
            boundComponent.setX(xConstraint + boundComponent.getWidth().pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraint)
        }

        override fun afterTransition() {
            boundComponent.setX(xConstraint)
        }
    }

    /**
     * Initially sets the component's y position to currentY + getHeight().
     * Transitions back to currentY.
     */
    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP
    ) : BoundTransition() {
        private lateinit var yConstraint: YConstraint

        override fun beforeTransition() {
            yConstraint = boundComponent.constraints.y
            boundComponent.setY(yConstraint + boundComponent.getHeight().pixels())
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraint)
        }

        override fun afterTransition() {
            boundComponent.setY(yConstraint)
        }
    }
}
