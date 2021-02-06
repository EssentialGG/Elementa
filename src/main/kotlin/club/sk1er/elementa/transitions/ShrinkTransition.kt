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
) : Transition() {
    private val xConstraints = mutableMapOf<UIComponent, XConstraint>()
    private val yConstraints = mutableMapOf<UIComponent, YConstraint>()
    private val widthConstraints = mutableMapOf<UIComponent, WidthConstraint>()
    private val heightConstraints = mutableMapOf<UIComponent, HeightConstraint>()

    override fun beforeTransition(component: UIComponent) {
        xConstraints[component] = component.constraints.x
        yConstraints[component] = component.constraints.y
        widthConstraints[component] = component.constraints.width
        heightConstraints[component] = component.constraints.height
    }

    override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
        constraints.apply {
            setXAnimation(animationType, time, xConstraints[component]!! + (component.getWidth() / 2f).pixels())
            setYAnimation(animationType, time, yConstraints[component]!! + (component.getHeight() / 2f).pixels())
            setWidthAnimation(animationType, time, 0.pixels())
            setHeightAnimation(animationType, time, 0.pixels())
        }
    }

    override fun afterTransition(component: UIComponent) {
        if (restoreConstraints) {
            component.setX(xConstraints[component]!!)
            component.setY(yConstraints[component]!!)
            component.setWidth(widthConstraints[component]!!)
            component.setHeight(heightConstraints[component]!!)
        }
        xConstraints.remove(component)
        yConstraints.remove(component)
        widthConstraints.remove(component)
        heightConstraints.remove(component)
    }
}
