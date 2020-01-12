package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.animate
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.effects.ScissorEffect

/**
 * Basic scroll component that will only draw what is currently visible.
 *
 * Also prevents scrolling past what should be reasonable. TODO
 */
class ScrollComponent : UIContainer() {
    private val actualHolder = UIContainer().constrain {
        width = RelativeConstraint(1f)
        height = RelativeConstraint(1f)
    }

    private var offset = 0f

    init {
        super.addChild(actualHolder)

        this.enableEffects(ScissorEffect())

        onMouseScroll { delta ->
            offset += (delta * 15)
            actualHolder.animate {
                val actualHeight = actualHolder.children.last().getBottom() - actualHolder.children.first().getTop()
                val maxNegative = this.getHeight() - actualHeight

                offset = offset.coerceIn(maxNegative..0f)

                setYAnimation(Animations.IN_SIN, 0.1f, offset.pixels())
            }
        }
    }

    override fun addChild(component: UIComponent) = apply {
        actualHolder.addChild(component)
    }
}