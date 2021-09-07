package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.width
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.pixels
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import java.awt.Color

/**
 * Simple text component that draws its given `text` at the scale determined by
 * this component's width & height constraints.
 */
open class UIText @JvmOverloads constructor(
    text: String = "",
    shadow: Boolean = true,
    shadowColor: Color? = null
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

    /** Guess on whether we should be trying to center or top-align this component. See [BELOW_LINE_HEIGHT]. */
    private val verticallyCenteredState = constraints.asState { y is CenterConstraint }

    private fun <T> UIConstraints.asState(selector: UIConstraints.() -> T) = BasicState(selector(constraints)).also {
        constraints.addObserver { _, _ -> it.set(selector(constraints)) }
    }

    init {
        setWidth(textWidthState.pixels())
        setHeight(shadowState.zip(verticallyCenteredState).map { (shadow, verticallyCentered) ->
            val above = (if (verticallyCentered) BELOW_LINE_HEIGHT else 0f)
            val center = BASE_CHAR_HEIGHT
            val below = BELOW_LINE_HEIGHT + (if (shadow) SHADOW_HEIGHT else 0f)
            above + center + below
        }.pixels())
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

    override fun getWidth(): Float {
        return super.getWidth() * getTextScale()
    }

    override fun getHeight(): Float {
        return super.getHeight() * getTextScale()
    }

    override fun draw(matrixStack: UMatrixStack) {
        val text = textState.get()
        if (text.isEmpty())
            return

        beforeDrawCompat(matrixStack)

        val scale = getWidth() / textWidthState.get()
        val x = getLeft()
        val y = getTop() + (if (verticallyCenteredState.get()) BELOW_LINE_HEIGHT * scale else 0f)
        val color = getColor()

        // We aren't visible, don't draw
        if (color.alpha <= 10) {
            return super.draw(matrixStack)
        }

        UGraphics.enableBlend()

        val shadow = shadowState.get()
        val shadowColor = shadowColorState.get()
        getFontProvider().drawString(
            matrixStack,
            textState.get(), color, x, y,
            10f, scale, shadow, shadowColor
        )
        super.draw(matrixStack)
    }

    companion object {
        /** Most (English) capital letters have this height, so this is what we use to center "the line". */
        internal const val BASE_CHAR_HEIGHT = 7f
        /**
         * Some letters have a few extra pixels below the visually centered line (gjpqy).
         * To accommodate these, we need to add extra height at the bottom and the top (to keep the original line
         * centered). This needs special consideration because the font renderer does not consider it, so we need to
         * adjust the position we give to it accordingly.
         * Additionally, adding the space on top make top-alignment difficult, whereas not adding it makes centering
         * difficult, so we use a simple heuristic to determine which one it is we're most likely looking for and then
         * either add just the bottom one or the top one as well.
         */
        internal const val BELOW_LINE_HEIGHT = 1f
        /** Extra height if shadows are enabled. */
        internal const val SHADOW_HEIGHT = 1f
    }
}
