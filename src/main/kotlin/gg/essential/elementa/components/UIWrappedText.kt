package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.basicHeightConstraint
import gg.essential.elementa.dsl.width
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.pixels
import gg.essential.elementa.utils.getStringSplitToWidth
import gg.essential.elementa.utils.getStringSplitToWidthTruncated
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import java.awt.Color

/**
 * Simple text component that draws its given `text` at the scale determined by
 * this component's width & height constrains.
 */
open class UIWrappedText @JvmOverloads constructor(
    text: String = "",
    shadow: Boolean = true,
    shadowColor: Color? = null,
    private var centered: Boolean = false,
    /**
     * Keeps the rendered text within the bounds of the component,
     * inserting an ellipsis ("...") if text is trimmed
     */
    private val trimText: Boolean = false,
    private val lineSpacing: Float = 9f,
    private val trimmedTextSuffix: String = "..."
) : UIComponent() {
    private val textState = BasicState(text).map { it } // extra map so we can easily rebind it
    private var shadowState: State<Boolean> = BasicState(shadow)
    private var shadowColorState: State<Color?> = BasicState(shadowColor)
    private val textScaleState = constraints.asState { getTextScale() }
    private val fontProviderState = constraints.asState { fontProvider }
    private var textWidthState = textState.zip(textScaleState.zip(fontProviderState)).map { (text, opts) ->
        val (textScale, fontProvider) = opts
        text.width(textScale, fontProvider) / textScale
    }

    private val charWidth = UGraphics.getCharWidth('x')

    /** Guess on whether we should be trying to center or top-align this component. See [BELOW_LINE_HEIGHT]. */
    private val verticallyCenteredState = constraints.asState { y is CenterConstraint }

    private fun <T> UIConstraints.asState(selector: UIConstraints.() -> T) = BasicState(selector(constraints)).also {
        constraints.addObserver { _, _ -> it.set(selector(constraints)) }
    }

    /**
     * Balances out space required below the line by adding empty space above the first one.
     * Also, if there are no shadows, the last line can be shorter so it looks more centered overall.
     */
    private val extraHeightState = verticallyCenteredState.zip(shadowState).map { (verticallyCentered, shadow) ->
        (if (verticallyCentered) fontProviderState.get().getBelowLineHeight() else 0f) + (if (shadow) 0f else -fontProviderState.get().getShadowHeight())
    }

    init {
        setWidth(textWidthState.pixels())
        setHeight(basicHeightConstraint {
            val lines = getStringSplitToWidth(
                getText(),
                getWidth(),
                getTextScale(),
                ensureSpaceAtEndOfLines = false,
                fontProvider = super.getFontProvider()
            )

            (lines.size * lineSpacing + extraHeightState.get()) * getTextScale()
        })
    }

    fun bindText(newTextState: State<String>) = apply {
        textState.rebind(newTextState)
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

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val textScale = getTextScale()
        val x = getLeft()
        val y = getTop() + (if (verticallyCenteredState.get()) fontProviderState.get().getBelowLineHeight() * textScale else 0f)
        val width = getWidth()
        val color = getColor()

        // We aren't visible, don't draw
        if (color.alpha <= 10) {
            return super.draw(matrixStack)
        }

        if (width / textScale <= charWidth) {
            // If we are smaller than a char, we can't physically split this string into
            // "width" strings, so we'll prefer a no-op to an error.
            return super.draw(matrixStack)
        }

        UGraphics.enableBlend()

        val lines = if (trimText) {
            getStringSplitToWidthTruncated(
                textState.get(),
                width,
                textScale,
                ((getHeight() / textScale - extraHeightState.get()) / lineSpacing).toInt(),
                ensureSpaceAtEndOfLines = false,
                fontProvider = getFontProvider(),
                trimmedTextSuffix = trimmedTextSuffix
            )
        } else {
            getStringSplitToWidth(
                textState.get(),
                width,
                textScale,
                ensureSpaceAtEndOfLines = false,
                fontProvider = getFontProvider()
            )
        }.map { it.trimEnd() }

        val shadow = shadowState.get()
        val shadowColor = shadowColorState.get()

        lines.forEachIndexed { i, line ->
            val xOffset = if (centered) {
                (width - line.width(textScale)) / 2f
            } else 0f

            getFontProvider().drawString(
                matrixStack,
                line,
                color,
                x + xOffset,
                y + i * lineSpacing * textScale,
                10f,
                textScale,
                shadow,
                if (shadow) shadowColor else null,
            )
        }

        super.draw(matrixStack)
    }
}
