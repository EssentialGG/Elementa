package club.sk1er.elementa.transitions

import club.sk1er.elementa.constraints.XConstraint
import club.sk1er.elementa.constraints.YConstraint
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.minus
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.plus

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
    ) : BoundTransition() {
        private lateinit var xConstraint: XConstraint

        override fun beforeTransition() {
            xConstraint = boundComponent.constraints.x
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraint - boundComponent.getWidth().pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints)
                boundComponent.setX(xConstraint)
        }
    }

    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : BoundTransition() {
        private lateinit var yConstraint: YConstraint

        override fun beforeTransition() {
            yConstraint = boundComponent.constraints.y
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraint - boundComponent.getHeight().pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints)
                boundComponent.setY(yConstraint)
        }
    }

    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : BoundTransition() {
        private lateinit var xConstraint: XConstraint

        override fun beforeTransition() {
            xConstraint = boundComponent.constraints.x
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setXAnimation(animationType, time, xConstraint + boundComponent.getWidth().pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints)
                boundComponent.setX(xConstraint)
        }
    }

    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : BoundTransition() {
        private lateinit var yConstraint: YConstraint

        override fun beforeTransition() {
            yConstraint = boundComponent.constraints.y
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setYAnimation(animationType, time, yConstraint + boundComponent.getHeight().pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints)
                boundComponent.setY(yConstraint)
        }
    }
}

