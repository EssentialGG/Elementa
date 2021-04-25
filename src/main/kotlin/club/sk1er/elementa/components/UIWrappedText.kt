package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.basicHeightConstraint
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import club.sk1er.elementa.state.pixels
import club.sk1er.elementa.utils.getStringSplitToWidth
import club.sk1er.elementa.utils.getStringSplitToWidthTruncated
import club.sk1er.mods.core.universal.UGraphics
import java.awt.Color

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constrains.
 */
open class UIWrappedText @JvmOverloads constructor(
    text: String = "",
    shadow: Boolean = true,
    shadowColor: Color? = null,
    private var centered: Boolean = false,
    /**
     * Keeps the rendered text without the bounds of the component,
     * inserting an ellipsis ("...") if text is trimmed
     */
    private val trimText: Boolean = false
) : UIComponent() {
    private var textState: State<String> = BasicState(text)
    private var shadowState: State<Boolean> = BasicState(shadow)
    private var shadowColorState: State<Color?> = BasicState(shadowColor)
    private var textWidthState = this.textState.map { UGraphics.getStringWidth(it).toFloat() }

    private val charWidth = UGraphics.getCharWidth('x')

    init {
        setWidth(textWidthState.pixels())
        setHeight(basicHeightConstraint {
            val lines = getStringSplitToWidth(
                text,
                getWidth(),
                getTextScale(),
                ensureSpaceAtEndOfLines = false,
                fontProvider = super.getFontProvider()
            )

            lines.size * 9f * getTextScale()
        })
    }

    fun bindText(newTextState: State<String>) = apply {
        textState = newTextState
        textWidthState.rebind(newTextState)
    }

    fun bindShadow(newShadowState: State<Boolean>) = apply {
        this.shadowState = newShadowState
    }

    fun bindShadowColor(newShadowColorState: State<Color?>) = apply {
        this.shadowColorState = newShadowColorState
    }

    fun getText() = textState.get()
    fun setText(text: String) = apply { textState.set(text) }

    fun getShadow() = shadowState.get()
    fun setShadow(shadow: Boolean) = apply { shadowState.set(shadow) }

    fun getShadowColor() = shadowColorState
    fun setShadowColor(shadowColor: Color?) = apply { shadowColorState.set(shadowColor) }

    /**
     * Returns the text width if no scale is applied to the text
     */
    fun getTextWidth() = textWidthState.get()

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

        UGraphics.enableBlend()

        UGraphics.scale(textScale.toDouble(), textScale.toDouble(), 1.0)
        UGraphics.translate(x.toDouble(), y.toDouble(), 0.0)

        val lines = if (trimText) {
            getStringSplitToWidthTruncated(
                textState.get(),
                width,
                textScale,
                (getHeight() / 9f / textScale).toInt(),
                ensureSpaceAtEndOfLines = false,
                fontProvider = getFontProvider()
            )
        } else getStringSplitToWidth(
            textState.get(),
            width,
            textScale,
            ensureSpaceAtEndOfLines = false,
            fontProvider = getFontProvider()
        )

        val shadow = shadowState.get()
        val shadowColor = shadowColorState.get()

        lines.forEachIndexed { i, line ->
            val xOffset = if (centered) {
                (scaledWidth - line.width(textScale)) / 2f
            } else 0f

            if (shadow) {
                getFontProvider().drawString(line, color, xOffset, i * 9f, 10f, true, shadowColor)
            } else {
                getFontProvider().drawString(line, color, xOffset, i * 9f, 10f, shadow = false)
            }
        }

        UGraphics.translate(-x.toDouble(), -y.toDouble(), 0.0)
        UGraphics.scale(1 / textScale.toDouble(), 1 / textScale.toDouble(), 1.0)

        super.draw()
    }
}
