package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.effects.ScissorEffect

/**
 * Basic scroll component that will only draw what is currently visible.
 *
 * Also prevents scrolling past what should be reasonable. TODO
 */
class ScrollComponent : UIComponent() {
    private val actualHolder = UIContainer().constrain {
        x = PixelConstraint(0f)
        y = PixelConstraint(0f)
        width = RelativeConstraint(1f)
        height = RelativeConstraint(1f)
    }

    private var offset = 0f
    override val children: MutableList<UIComponent>
        get() = actualHolder.children

    init {
        this.enableEffects(ScissorEffect())

        actualHolder.parent = this

        onMouseScroll { delta ->
            // TODO: Math to make sure I can't scroll past content.
            offset += (delta * 10)
            actualHolder.setX(PixelConstraint(offset))
        }
    }

    override fun addChild(component: UIComponent) = apply {
        actualHolder.addChild(component)
    }
}