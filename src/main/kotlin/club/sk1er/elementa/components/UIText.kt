package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ColorConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.mods.core.universal.UGraphics
import java.awt.Color

/**
 * Simple text component that draws its given [text] at the scale determined by
 * this component's width & height constraints.
 */
open class UIText @JvmOverloads constructor(
    private var text: String = "",
    private var shadow: Boolean = true,
    private var shadowColor: Color? = null
) :
    UIComponent() {

    private var textWidth: Float = UGraphics.getStringWidth(text).toFloat()

    init {
        setWidth(textWidth.pixels())
        setHeight(9.pixels())
    }

    fun getText() = text

    @JvmOverloads
    fun setText(text: String, adjustWidth: Boolean = true) = apply {
        this.text = text
        textWidth = UGraphics.getStringWidth(text).toFloat()
        if (adjustWidth) setWidth(textWidth.pixels())
    }

    fun getShadow() = shadow
    fun setShadow(shadow: Boolean) = apply { this.shadow = shadow }

    fun getShadowColor() = shadowColor
    fun setShadowColor(shadowColor: Color?) = apply { this.shadowColor = shadowColor }

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

        UGraphics.enableBlend()

        UGraphics.scale(width.toDouble(), height.toDouble(), 1.0)
        if (shadow && shadowColor != null) {
            UGraphics.drawString(text, x / width, y / height, color.rgb, shadowColor!!.rgb)
        } else {
            UGraphics.drawString(text, x / width, y / height, color.rgb, shadow)
        }
        UGraphics.scale(1 / width.toDouble(), 1 / height.toDouble(), 1.0)

        super.draw()
    }
}
