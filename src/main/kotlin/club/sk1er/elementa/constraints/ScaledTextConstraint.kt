package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIText
import net.minecraft.client.Minecraft

/**
 * Sets the width/height to be a scale of the default text width and height
 */
class ScaledTextConstraint(private var scale: Float) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    private var fontRenderer = Minecraft.getMinecraft().fontRendererObj

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return when (component) {
            is UIText -> scale * fontRenderer.getStringWidth(component.getText())
            else -> throw IllegalAccessException("ScaledTextConstraint can only be used with UIText")
        }
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return when(component) {
            is UIText -> scale * 9
            else -> throw IllegalAccessException("ScaledTextConstraint can only be used with UIText")
        }
    }

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        throw IllegalAccessException("ScaledTextConstraint cannot be used as a radius")
    }
}