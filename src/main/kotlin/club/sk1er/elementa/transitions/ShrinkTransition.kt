package club.sk1er.elementa.transitions

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.HeightConstraint
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.XConstraint
import club.sk1er.elementa.constraints.YConstraint
import club.sk1er.elementa.constraints.animation.AnimatingConstraints
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.plus

/**
 * Shrinks a component towards its center.
 */
class ShrinkTransition @JvmOverloads constructor(
    private val time: Float = 1f,
    private val animationType: Animations = Animations.OUT_EXP,
    private val restoreConstraints: Boolean = false
) : BoundTransition() {
    private lateinit var xConstraint: XConstraint
    private lateinit var yConstraint: YConstraint
    private lateinit var widthConstraint: WidthConstraint
    private lateinit var heightConstraint: HeightConstraint

    override fun beforeTransition() {
        xConstraint = boundComponent.constraints.x
        yConstraint = boundComponent.constraints.y
        widthConstraint = boundComponent.constraints.width
        heightConstraint = boundComponent.constraints.height
    }

    override fun doTransition(constraints: AnimatingConstraints) {
        constraints.apply {
            setXAnimation(animationType, time, xConstraint + (boundComponent.getWidth() / 2f).pixels())
            setYAnimation(animationType, time, yConstraint + (boundComponent.getHeight() / 2f).pixels())
            setWidthAnimation(animationType, time, 0.pixels())
            setHeightAnimation(animationType, time, 0.pixels())
        }
    }

    override fun afterTransition() {
        if (restoreConstraints) {
            boundComponent.setX(xConstraint)
            boundComponent.setY(yConstraint)
            boundComponent.setWidth(widthConstraint)
            boundComponent.setHeight(heightConstraint)
        }
    }
}
