package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.basicHeightConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.utils.getStringSplitToWidth
import club.sk1er.elementa.utils.getStringSplitToWidthTruncated
import club.sk1er.mods.core.universal.UniversalGraphicsHandler

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constrains.
 */
open class UIWrappedText @JvmOverloads constructor(
    private var text: String = "",
    private var shadow: Boolean = true,
    private var centered: Boolean = false,
    /**
     * Keeps the rendered text without the bounds of the component,
     * inserting an ellipsis ("...") if text is trimmed
     */
    private val trimText: Boolean = false
) : UIComponent() {

    private val charWidth = UniversalGraphicsHandler.getCharWidth('x')
    private var textWidth: Float = UniversalGraphicsHandler.getStringWidth(text).toFloat()

    init {
        setWidth(textWidth.pixels())
        setHeight(basicHeightConstraint {
            val lines = getStringSplitToWidth(text, getWidth(), getTextScale())

            lines.size * 9f * getTextScale()
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

        val textScale = getTextScale()
        val x = getLeft() / textScale
        val y = getTop() / textScale
        val width = getWidth()
        val scaledWidth = width / textScale
        val color = getColor()

        // We aren't visible, don't draw
        if (color.alpha <= 10) {
            return super.draw()
        }

        if (scaledWidth <= charWidth) {
            // If we are smaller than a char, we can't physically split this string into
            // "width" strings, so we'll prefer a no-op to an error.
            return super.draw()
        }

        UniversalGraphicsHandler.enableBlend()

        UniversalGraphicsHandler.scale(textScale.toDouble(), textScale.toDouble(), 1.0)
        UniversalGraphicsHandler.translate(x.toDouble(), y.toDouble(), 0.0)

        val lines = if (trimText) {
            getStringSplitToWidthTruncated(text, width, textScale, (getHeight() / 9f / textScale).toInt())
        } else getStringSplitToWidth(text, width, textScale)

        lines.forEachIndexed { i, line ->
            val xOffset = if (centered)
                (scaledWidth - line.width(textScale)) / 2f
            else 0f
            UniversalGraphicsHandler.drawString(line, xOffset, i * 9f, color.rgb, shadow)
        }

        UniversalGraphicsHandler.translate(-x.toDouble(), -y.toDouble(), 0.0)
        UniversalGraphicsHandler.scale(1 / textScale.toDouble(), 1 / textScale.toDouble(), 1.0)

        super.draw()
    }
}
