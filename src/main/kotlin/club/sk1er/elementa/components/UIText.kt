package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.pixels
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalMinecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constrains.
 */
open class UIText @JvmOverloads constructor(private var text: String = "", private var shadow: Boolean = true) : UIComponent() {

    private var textWidth: Float = UniversalGraphicsHandler.getStringWidth(text).toFloat()

    init {
        setWidth(textWidth.pixels())
        setHeight(9.pixels())
    }

    fun getText() = text
    @JvmOverloads fun setText(text: String, adjustWidth: Boolean = true) = apply {
        this.text = text
        textWidth =UniversalGraphicsHandler.getStringWidth(text).toFloat()
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

        // We aren't visible, don't draw
        if (color.alpha <= 10) {
            return super.draw()
        }

        UniversalGraphicsHandler.enableBlend()

        UniversalGraphicsHandler.scale(width.toDouble(), height.toDouble(), 1.0)
        UniversalGraphicsHandler.drawString(text, x / width, y / height, color.rgb, shadow)
        UniversalGraphicsHandler.scale(1 / width.toDouble(), 1 / height.toDouble(), 1.0)

        super.draw()
    }
}