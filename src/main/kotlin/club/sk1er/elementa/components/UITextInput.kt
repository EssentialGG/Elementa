package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.utils.getStringSplitToWidth
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import java.awt.Color

open class UITextInput @JvmOverloads constructor(
    private val placeholder: String = "",
    var wrapped: Boolean = true,
    var shadow: Boolean = true
) : UIComponent() {

    private val placeholderWidth = UniversalGraphicsHandler.getStringWidth(placeholder).toFloat()
    private val internalMouseClickAction = { mouseX: Float, mouseY: Float, button: Int ->
        if (active) {
            // TODO move cursor
        }
    }

    var text: String = ""
        set(value) {
            field = value
            textWidth = UniversalGraphicsHandler.getStringWidth(text).toFloat()
            updateAction(value)
        }
    var textWidth: Float = UniversalGraphicsHandler.getStringWidth(text).toFloat()
    var textOffset: Float = 0f

    var cursor: UIComponent = UIBlock(Color(255, 255, 255, 0))
    var cursorLocation = 0
    var active: Boolean = false
        set(value) {
            field = value
            if (value)  {
                animateCursor()
                grabWindowFocus()
            }
            else cursor.setColor(Color(255, 255, 255, 0).asConstraint())
        }

    var maxWidth: WidthConstraint = UniversalGraphicsHandler.getStringWidth(placeholder).pixels()
    var minWidth: WidthConstraint = UniversalGraphicsHandler.getStringWidth(placeholder).pixels()

    private var updateAction: (text: String) -> Unit = {}
    private var activateAction: (text: String) -> Unit = {}

    init {
        setHeight(9.pixels())

        alignCursor(if (text.isEmpty()) placeholder else text)

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
        if (isHovered()) {
            internalMouseClickAction(mouseX - getLeft(), mouseY - getTop(), button)
        }

        super.mouseClick(mouseX, mouseY, button)
    }

    override fun draw() {
        beforeDraw()

        val y = getTop()
        val color = getColor()

        UniversalGraphicsHandler.enableBlend()

        UniversalGraphicsHandler.scale(getTextScale().toDouble(), getTextScale().toDouble(), 1.0)

        val displayText = if (text.isEmpty() && !this.active) placeholder else text

        if (wrapped) {
            val lines = alignCursor(displayText)
            lines.forEachIndexed { index, line ->
                UniversalGraphicsHandler.drawString(line, getLeft(), y + index * 9, color.rgb, shadow)
            }
        } else {
            alignCursor()
            UniversalGraphicsHandler.drawString(displayText, getLeft() + textOffset, y, color.rgb, shadow)
        }

        UniversalGraphicsHandler.scale(1 / getTextScale().toDouble(), 1 / getTextScale().toDouble(), 1.0)


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

    private fun alignCursor(displayText: String = ""): List<String> {
        val width = if (text.isEmpty() && !this.active) placeholderWidth else textWidth
        setWidth(width.pixels().minMax(minWidth, maxWidth))

        if (wrapped) {
            val lines = getStringSplitToWidth(
                displayText,
                getWidth() / getTextScale()
            )

            cursor.setX((UniversalGraphicsHandler.getStringWidth(lines.last()) + 1).pixels())
            cursor.setY(((lines.size - 1) * 9).pixels())
            setHeight((lines.size * 9).pixels())
            return lines
        } else {
            cursor.setX(width.pixels())
            textOffset = if (active) {
                if (width > getWidth()) {
                    cursor.setX(0.pixels(true))
                    getWidth() - width - 1
                } else 0f
            } else 0f
        }

        return emptyList()
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