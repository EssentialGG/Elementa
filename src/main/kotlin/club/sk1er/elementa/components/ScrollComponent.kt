package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.features.ScissorFeature

class ScrollComponent : UIComponent() {
    private val actualHolder = UIContainer().constrain {
        x = PixelConstraint(0f)
        y = PixelConstraint(0f)
        width = RelativeConstraint(1f)
        height = RelativeConstraint(1f)
    }

    private var offset = 0f

    init {
        this.enableFeatures(ScissorFeature())

        actualHolder childOf this

        onScroll { delta ->
            // TODO: Math to make sure I can't scroll past content.
            offset += (delta * 10)
            actualHolder.setX(PixelConstraint(offset))
        }
    }

    override fun addChild(component: UIComponent) = apply {
        actualHolder.addChild(component)
    }

    override fun <T> childrenOfType(clazz: Class<T>): List<T> {
        return actualHolder.children.filterIsInstance(clazz)
    }
}