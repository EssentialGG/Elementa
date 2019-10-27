package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.PixelConstraint
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constrains.
 */
open class UIText @JvmOverloads constructor(private var text: String = "", private var shadow: Boolean = true) : UIComponent() {

    private var textWidth: Float = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()

    init {
        super.getConstraints().setWidth(PixelConstraint(textWidth))
        super.getConstraints().setHeight(PixelConstraint(9f))
    }

    fun getText() = text
    fun setText(text: String) = apply {
        this.text = text
        textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()
    }

    fun getShadow() = shadow
    fun setShadow(shadow: Boolean) = apply { this.shadow = shadow }

    /**
     * Returns the text width if no scale is applied to the text
     */
    fun getTextWidth() = textWidth

    override fun draw() {
        beforeDraw()

        val x = this.getLeft()
        val y = this.getTop()
        val width = this.getWidth() / textWidth
        val height = this.getHeight() / 9f
        val color = this.getColor()

        GlStateManager.enableBlend()

        GlStateManager.scale(width.toDouble(), height.toDouble(), 1.0)
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x / width, y / height, color.rgb, shadow)
        GlStateManager.scale(1 / width.toDouble(), 1 / height.toDouble(), 1.0)

        super.draw()
    }
}