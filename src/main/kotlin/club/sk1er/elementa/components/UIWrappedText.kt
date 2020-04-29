package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.HeightConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalMinecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constrains.
 */
open class UIWrappedText @JvmOverloads constructor(
        private var text: String = "",
        private var shadow: Boolean = true,
        private var centered: Boolean = false
) : UIComponent() {

    private val charWidth = UniversalGraphicsHandler.getCharWidth('x')
    private var textWidth: Float = UniversalGraphicsHandler.getStringWidth(text).toFloat()

    init {
        setWidth(textWidth.pixels())
        setHeight(object : HeightConstraint {
            override var cachedValue = 0f
            override var recalculate = true
            override var constrainTo: UIComponent? = null

            override fun getHeightImpl(component: UIComponent): Float {
                val width = getWidth() / getTextScale()
                val lines = UniversalGraphicsHandler.listFormattedStringToWidth(text, width.toInt())

                return lines.size * 9f * getTextScale()
            }
        })
    }

    fun getText() = text
    fun setText(text: String) = apply {
        this.text = text
        textWidth = UniversalGraphicsHandler.getStringWidth(text).toFloat()
    }

    fun getShadow() = shadow
    fun setShadow(shadow: Boolean) = apply { this.shadow = shadow }

    /**
     * Returns the text width if no scale is applied to the text
     */
    fun getTextWidth() = textWidth

    override fun draw() {
        beforeDraw()

        val x = getLeft() / getTextScale()
        val y = getTop() / getTextScale()
        val width = getWidth() / getTextScale()
        val color = getColor()

        // We aren't visible, don't draw
        if (color.alpha <= 10) {
            return super.draw()
        }

        if (width <= charWidth) {
            // If we are smaller than a char, we can't physically split this string into
            // "width" strings, so we'll prefer a no-op to an error.
            return super.draw()
        }

        UniversalGraphicsHandler.enableBlend()

        UniversalGraphicsHandler.scale(getTextScale().toDouble(), getTextScale().toDouble(), 1.0)
        UniversalGraphicsHandler.translate(x.toDouble(), y.toDouble(), 0.0)

        try {
            val lines = UniversalGraphicsHandler.listFormattedStringToWidth(text, width.toInt())
            lines.forEachIndexed { i, line ->
                val xOffset = if (centered)
                    (width - UniversalGraphicsHandler.getStringWidth(line)) / 2f
                else 0f
                UniversalGraphicsHandler.drawString(line, xOffset, i * 9f, color.rgb, shadow)
            }
        } catch (e: StackOverflowError) {
            // We probably couldn't wrap the text properly
            text = ""
        }

        UniversalGraphicsHandler.translate(-x.toDouble(), -y.toDouble(), 0.0)
        UniversalGraphicsHandler.scale(1 / getTextScale().toDouble(), 1 / getTextScale().toDouble(), 1.0)

        super.draw()
    }
}