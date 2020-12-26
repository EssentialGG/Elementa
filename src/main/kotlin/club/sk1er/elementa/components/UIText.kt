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
    // private var textWidthState by State.map(::textState) { UGraphics.getStringWidth(it).toFloat() }

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

    fun getText() = textState.getValue()
    fun setText(text: String) = apply { textState.setValue(text) }

    fun getShadow() = shadowState.getValue()
    fun setShadow(shadow: Boolean) = apply { shadowState.setValue(shadow) }

    fun getShadowColor() = shadowColorState
    fun setShadowColor(shadowColor: Color?) = apply { shadowColorState.setValue(shadowColor) }

    /**
     * Returns the text width if no scale is applied to the text
     */
    fun getTextWidth() = textWidthState.getValue()

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
        val width = getWidth() / textWidthState.getValue()
        val height = getHeight() / 9f
        val color = getColor()

        // We aren't visible, don't draw
        if (color.alpha <= 10) {
            return super.draw()
        }

        UGraphics.enableBlend()

        UGraphics.scale(width.toDouble(), height.toDouble(), 1.0)
        val shadow = shadowState.getValue()
        val shadowColor = shadowColorState.getValue()
        if (shadow && shadowColor != null) {
            UGraphics.drawString(textState.getValue(), x / width, y / height, color.rgb, shadowColor.rgb)
        } else {
            UGraphics.drawString(textState.getValue(), x / width, y / height, color.rgb, shadow)
        }
        UGraphics.scale(1 / width.toDouble(), 1 / height.toDouble(), 1.0)

        super.draw()
    }
}
