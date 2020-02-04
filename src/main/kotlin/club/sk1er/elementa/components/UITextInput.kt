package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

open class UITextInput @JvmOverloads constructor(
    private val placeholder: String = "",
    var wrapped: Boolean = true,
    var shadow: Boolean = true
) : UIComponent() {

    private val placeholderWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(placeholder).toFloat()
    private val internalMouseClickAction = { mouseX: Float, mouseY: Float, button: Int ->
        if (active) {
            // TODO move cursor
        }
    }

    var text: String = ""
        set(value) {
            field = value
            textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()
            updateAction(value)
        }
    var textWidth: Float = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()
    var textOffset: Float = 0f

    var cursor: UIComponent = UIBlock(Color(255, 255, 255, 0))
    var cursorLocation = 0
    var active: Boolean = false
        set(value) {
            field = value
            if (value) animateCursor()
            else cursor.setColor(Color(255, 255, 255, 0).asConstraint())
        }

    var maxWidth: WidthConstraint = Minecraft.getMinecraft().fontRendererObj.getStringWidth(placeholder).pixels()
    var minWidth: WidthConstraint = Minecraft.getMinecraft().fontRendererObj.getStringWidth(placeholder).pixels()

    private var updateAction: (text: String) -> Unit = {}
    private var activateAction: (text: String) -> Unit = {}

    init {
        setHeight(9.pixels())
        onKeyType { typedChar, keyCode ->
            if (!active) return@onKeyType

            if (keyCode == 14) {
                // backspace
                if (text.isEmpty()) return@onKeyType
                text = text.substring(0, text.length - 1)
            } else if (keyCode == 203) {
                // left arrow
                if (cursorLocation > 0) cursorLocation--
            } else if (keyCode == 205) {
                // right arrow
                if (cursorLocation < text.length) cursorLocation++
            } else if (keyCode == 28 || keyCode == 156) {
                activateAction(text)
            } else if (
                keyCode in 2..13 ||
                keyCode in 16..27 ||
                keyCode in 30..41 ||
                keyCode in 43..53 ||
                keyCode in 71..83 ||
                keyCode in 145..147 ||
                keyCode == 55 ||
                keyCode == 181 ||
                keyCode == 57
            ) {
                // normal key input
                text += typedChar
                cursorLocation++
            }
        }

        cursor.constrain {
            x = (textWidth + 1).pixels()
            y = (0).pixels()
            width = 1.pixels()
            height = 8.pixels()
        } childOf this
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int) {
        if (isHovered()) internalMouseClickAction(mouseX - getLeft(), mouseY - getTop(), button)
        super.mouseClick(mouseX, mouseY, button)
    }

    override fun draw() {
        beforeDraw()

        val x = getLeft()
        val y = getTop()
        val width = getWidth() / getTextScale()
        val color = getColor()

        GlStateManager.enableBlend()

        GlStateManager.scale(getTextScale().toDouble(), getTextScale().toDouble(), 1.0)

        val displayText = if (text.isEmpty() && !this.active) placeholder else text

        if (wrapped) {
            val lines = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(displayText, width.toInt())
            alignCursor(lines)
            lines.forEachIndexed { index, line ->
                Minecraft.getMinecraft().fontRendererObj.drawString(line, x, y + index * 9, color.rgb, shadow)
            }
        } else {
            alignCursor()
            Minecraft.getMinecraft().fontRendererObj.drawString(displayText, x + textOffset, y, color.rgb, shadow)
        }

        GlStateManager.scale(1 / getTextScale().toDouble(), 1 / getTextScale().toDouble(), 1.0)


        super.draw()
    }

    /**
     * Callback to run whenever the text in the input changes,
     * i.e. every time a valid key is pressed.
     */
    fun onUpdate(action: (text: String) -> Unit) = apply {
        updateAction = action
    }

    /**
     * Callback to run when the user hits the Return key, thus
     * "activating" the input.
     */
    fun onActivate(action: (text: String) -> Unit) = apply {
        activateAction = action
    }

    private fun alignCursor(lines: List<String> = emptyList()) {
        val width = if (text.isEmpty() && !this.active) placeholderWidth else textWidth

        if (wrapped) {
            cursor.setX((Minecraft.getMinecraft().fontRendererObj.getStringWidth(lines.last()) + 1).pixels())
            cursor.setY(((lines.size - 1) * 9).pixels())
            setHeight((lines.size * 9).pixels())
        } else {
            cursor.setX(width.pixels())
            setWidth(width.pixels().minMax(minWidth, maxWidth))
            textOffset = if (active) {
                if (width > getWidth()) {
                    cursor.setX(0.pixels(true))
                    getWidth() - width - 1
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