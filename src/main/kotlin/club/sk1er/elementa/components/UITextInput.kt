package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
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
    var cursor = UIBlock(Color.WHITE)
    var active = false
        set(value) {
            field = value
            if (value) {
                animateCursor()
            } else {
                cursor.setColor(Color(255, 255, 255, 0).asConstraint())
            }
        }


    init {
        this.text = text
        setWidth(textWidth.pixels())
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
            cursor.setX(textWidth.pixels())
        }

        cursor.constrain {
            x = (textWidth + 1).pixels()
            y = (-1).pixels()
            width = 1.pixels()
            height = 8.pixels()
        } childOf this
    }

    private fun animateCursor() {
        cursor.animate {
            setColorAnimation(Animations.OUT_CIRCULAR, 0.5f, Color.WHITE.asConstraint())
            onComplete {
                cursor.animate {
                    setColorAnimation(Animations.IN_CIRCULAR, 0.5f, Color(255, 255, 255, 0).asConstraint())
                    onComplete {
                        animateCursor()
                    }
                }
            }
        }
    }

    override fun draw() {
        beforeDraw()

        val fontRenderer = Minecraft.getMinecraft().fontRendererObj

        val x = getLeft()
        val y = getTop()
        val width = getWidth() / getTextScale()
        val color = getColor()
        val lines = fontRenderer.listFormattedStringToWidth(text, width.toInt())

        GlStateManager.enableBlend()

        GlStateManager.scale(getTextScale().toDouble(), getTextScale().toDouble(), 1.0)

        lines.forEachIndexed { index, line ->
            fontRenderer.drawString(line, x, y + index * 9, color.rgb, shadow)
        }

        GlStateManager.scale(1 / getTextScale().toDouble(), 1 / getTextScale().toDouble(), 1.0)

        cursor.setX((fontRenderer.getStringWidth(lines.last()) + 1).pixels())
        cursor.setY(((lines.size - 1) * 9).pixels())
        if (wrapped) {
            setHeight((lines.size * 9).pixels())
        }

        super.draw()
    }
}