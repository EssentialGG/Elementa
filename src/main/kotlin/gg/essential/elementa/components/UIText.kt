package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.width
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.pixels
import gg.essential.universal.UGraphics
import java.awt.Color

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constraints.
 */
open class UIText @JvmOverloads constructor(
    text: String = "",
    shadow: Boolean = true,
    shadowColor: Color? = null
) : UIComponent() {
    private var textState: State<String> = BasicState(text)
    private var shadowState: State<Boolean> = BasicState(shadow)
    private var shadowColorState: State<Color?> = BasicState(shadowColor)
    private var textWidthState = this.textState.map { it.width(getTextScale(), getFontProvider()) }

    init {
        setWidth(textWidthState.pixels())
        setHeight(9.pixels())
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

    override fun getWidth(): Float {
        return super.getWidth() * getTextScale()
    }

    override fun getHeight(): Float {
        return super.getHeight() * getTextScale()
    }

    override fun draw() {
        val text = textState.get()
        if (text.isEmpty())
            return

        beforeDraw()

        val x = getLeft()
        val y = getTop()
        val width = getWidth() / textWidthState.get()
        val height = getHeight() / 9f
        val color = getColor()

        // We aren't visible, don't draw
        if (color.alpha <= 10) {
            return super.draw()
        }

        UGraphics.enableBlend()

        val shadow = shadowState.get()
        val shadowColor = shadowColorState.get()
        getFontProvider().drawString(
            textState.get(), color, x, y,
            10f, width, shadow, shadowColor
        )
        super.draw()
    }
}
