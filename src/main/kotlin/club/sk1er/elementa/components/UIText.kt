package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.pixels
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constrains.
 */
open class UIText @JvmOverloads constructor(private var text: String = "", private var shadow: Boolean = true) : UIComponent() {

    private var textWidth: Float = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()

    init {
        setWidth(textWidth.pixels())
        setHeight(9.pixels())
    }

    fun getText() = text
    @JvmOverloads fun setText(text: String, adjustWidth: Boolean = true) = apply {
        this.text = text
        textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()
        if (adjustWidth) setWidth(textWidth.pixels())
    }

    fun getShadow() = shadow
    fun setShadow(shadow: Boolean) = apply { this.shadow = shadow }

    /**
     * Returns the text width if no scale is applied to the text
     */
    fun getTextWidth() = textWidth

    override fun getWidth(): Float {
        return super.getWidth() * getTextScale()
    }

    override fun getHeight(): Float {
        return super.getHeight() * getTextScale()
    }

    override fun draw() {
        beforeDraw()

        val x = getLeft()
        val y = getTop()
        val width = getWidth() / textWidth
        val height = getHeight() / 9f
        val color = getColor()

        GlStateManager.enableBlend()

        GlStateManager.scale(width.toDouble(), height.toDouble(), 1.0)
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x / width, y / height, color.rgb, shadow)
        GlStateManager.scale(1 / width.toDouble(), 1 / height.toDouble(), 1.0)

        super.draw()
    }
}