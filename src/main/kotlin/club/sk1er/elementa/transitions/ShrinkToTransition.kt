package club.sk1er.elementa.transitions

import club.sk1er.elementa.constraints.HeightConstraint
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.XConstraint
import club.sk1er.elementa.constraints.YConstraint
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.plus
import club.sk1er.elementa.dsl.pixels

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
    ) : BoundTransition() {
        private lateinit var widthConstraint: WidthConstraint

        override fun beforeTransition() {
            widthConstraint = boundComponent.constraints.width
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setWidthAnimation(animationType, time, 0.pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints)
                boundComponent.setWidth(widthConstraint)
        }
    }

    class Top @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : BoundTransition() {
        private lateinit var heightConstraint: HeightConstraint

        override fun beforeTransition() {
            heightConstraint = boundComponent.constraints.height
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setHeightAnimation(animationType, time, 0.pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints)
                boundComponent.setHeight(heightConstraint)
        }
    }

    class Right @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : BoundTransition() {
        private lateinit var xConstraint: XConstraint
        private lateinit var widthConstraint: WidthConstraint

        override fun beforeTransition() {
            xConstraint = boundComponent.constraints.x
            widthConstraint = boundComponent.constraints.width
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setWidthAnimation(animationType, time, 0.pixels())
            constraints.setXAnimation(animationType, time, xConstraint + boundComponent.getWidth().pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints) {
                boundComponent.setX(xConstraint)
                boundComponent.setWidth(widthConstraint)
            }
        }
    }

    class Bottom @JvmOverloads constructor(
        private val time: Float = 1f,
        private val animationType: Animations = Animations.OUT_EXP,
        private val restoreConstraints: Boolean = false
    ) : BoundTransition() {
        private lateinit var yConstraint: YConstraint
        private lateinit var heightConstraint: HeightConstraint

        override fun beforeTransition() {
            yConstraint = boundComponent.constraints.y
            heightConstraint = boundComponent.constraints.height
        }

        override fun doTransition(constraints: AnimatingConstraints) {
            constraints.setHeightAnimation(animationType, time, 0.pixels())
            constraints.setYAnimation(animationType, time, yConstraint + boundComponent.getHeight().pixels())
        }

        override fun afterTransition() {
            if (restoreConstraints) {
                boundComponent.setY(yConstraint)
                boundComponent.setHeight(heightConstraint)
            }
        }
    }
}
