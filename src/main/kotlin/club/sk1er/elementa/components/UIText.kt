package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import club.sk1er.elementa.state.pixels
import club.sk1er.mods.core.universal.UGraphics
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
    private var textWidthState = this.textState.map { UGraphics.getStringWidth(it).toFloat() }

    init {
        setWidth(textWidthState.pixels())
        setHeight(9.pixels())
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

        UGraphics.scale(width.toDouble(), height.toDouble(), 1.0)
        val shadow = shadowState.get()
        val shadowColor = shadowColorState.get()
        getFontProvider().drawString(textState.get(), color, x / width, y / height, 10f, shadow, shadowColor)
        UGraphics.scale(1 / width.toDouble(), 1 / height.toDouble(), 1.0)

        super.draw()
    }
}
