package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.dsl.basicHeightConstraint
import gg.essential.elementa.dsl.width
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.pixels
import gg.essential.elementa.utils.getStringSplitToWidth
import gg.essential.elementa.utils.getStringSplitToWidthTruncated
import gg.essential.universal.UGraphics
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
    private var textWidthState = this.textState.map { it.width(getTextScale(), getFontProvider()) }

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
        Window.enqueueRenderOperation {
            textWidthState.rebind(textState); //Needed so that the text scale and font provider are now present
        }
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

        UGraphics.translate(x.toDouble() * textScale, y.toDouble() * textScale, 0.0)

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

//            println(textScale)
            if (shadow) {
                getFontProvider().drawString(
                    line,
                    color,
                    xOffset,
                    i * 9f * textScale,
                    10f,
                    textScale,
                    true,
                    shadowColor
                )
            } else {
                getFontProvider().drawString(line, color, xOffset, i * 9f, 10f, textScale, shadow = false)
            }
        }

        UGraphics.translate(-x.toDouble() * textScale, -y.toDouble() * textScale, 0.0)

        super.draw()
    }
}
