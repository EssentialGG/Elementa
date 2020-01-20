package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

open class UITextInput @JvmOverloads constructor(
        text: String = "",
        var wrapped: Boolean = true,
        var shadow: Boolean = true
) : UIComponent() {

    var text: String = text
        set(value) {
            field = value
            textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()
        }
    var textWidth: Float = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()
    var textOffset: Float = 0f

    var cursor: UIComponent = UIBlock(Color(255, 255, 255, 0))
    var active: Boolean = false
        set(value) {
            field = value
            if (value) animateCursor()
            else cursor.setColor(Color(255, 255, 255, 0).asConstraint())
        }

    var maxWidth: WidthConstraint = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).pixels()
    var minWidth: WidthConstraint = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).pixels()

    init {
        this.text = text
        setHeight(9.pixels())
        onKeyType { typedChar, keyCode ->
            if (!active) return@onKeyType

            if (keyCode == 14) {
                if (this.text.isEmpty()) return@onKeyType
                this.text = this.text.substring(0, this.text.length - 1)
            } else if (
                    (keyCode in 2..13)   ||
                    (keyCode in 16..27)  ||
                    (keyCode in 30..41)  ||
                    (keyCode in 43..53)  ||
                    (keyCode in 73..83)  ||
                    (keyCode == 55) || (keyCode == 181) || (keyCode == 57)
            ){
                this.text += typedChar
            }
        }

        cursor.constrain {
            x = (textWidth + 1).pixels()
            y = (0).pixels()
            width = 1.pixels()
            height = 8.pixels()
        } childOf this
    }

    override fun draw() {
        beforeDraw()

        val x = getLeft()
        val y = getTop()
        val width = getWidth() / getTextScale()
        val color = getColor()

        GlStateManager.enableBlend()

        GlStateManager.scale(getTextScale().toDouble(), getTextScale().toDouble(), 1.0)

        if (wrapped) {
            val lines = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(text, width.toInt())
            alignCursor(lines)
            lines.forEachIndexed { index, line ->
                Minecraft.getMinecraft().fontRendererObj.drawString(line, x, y + index * 9, color.rgb, shadow)
            }
        } else {
            alignCursor()
            Minecraft.getMinecraft().fontRendererObj.drawString(text, x + textOffset, y, color.rgb, shadow)
        }

        GlStateManager.scale(1 / getTextScale().toDouble(), 1 / getTextScale().toDouble(), 1.0)


        super.draw()
    }

    private fun alignCursor(lines: List<String> = ArrayList()) {
        if (wrapped) {
            cursor.setX((Minecraft.getMinecraft().fontRendererObj.getStringWidth(lines.last()) + 1).pixels())
            cursor.setY(((lines.size - 1) * 9).pixels())
            setHeight((lines.size * 9).pixels())
        } else {
            cursor.setX(textWidth.pixels())
            setWidth(textWidth.pixels().minMax(minWidth, maxWidth))
            textOffset = if (active) {
                if (textWidth > getWidth()) {
                    cursor.setX(0.pixels(true))
                    getWidth() - textWidth - 1
                } else 0f
            } else 0f
        }
    }

    private fun animateCursor() {
        if (!active) return
        cursor.animate {
            setColorAnimation(Animations.OUT_CIRCULAR, 0.5f, Color.WHITE.asConstraint())
            onComplete {
                if (!active) return@onComplete
                cursor.animate {
                    setColorAnimation(Animations.IN_CIRCULAR, 0.5f, Color(255, 255, 255, 0).asConstraint())
                    onComplete {
                        if (active) animateCursor()
                    }
                }
            }
        }
    }
}