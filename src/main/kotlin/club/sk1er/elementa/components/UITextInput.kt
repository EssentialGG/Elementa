package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import java.awt.Color

open class UITextInput @JvmOverloads constructor(
    private val placeholder: String = "",
    var shadow: Boolean = true
) : UIComponent() {
    private var minWidth: WidthConstraint? = null
    private var maxWidth: WidthConstraint? = null
    private var updateAction: (text: String) -> Unit = {}
    private var activateAction: (text: String) -> Unit = {}

    private val placeholderWidth = placeholder.width().toFloat()
    private var textWidth = 0f

    private var text = ""
    private var active = false

    private var cursor: UIComponent = UIBlock(Color(255, 255, 255, 0)).constrain {
        y = CenterConstraint()
        width = 1.pixels()
        height = 7.pixels()
    } childOf this
    private var cursorLocation = 0

    init {
        setHeight(9.pixels())

        onKeyType { typedChar, keyCode ->
            if (!active) return@onKeyType

            if (keyCode == 1) {
                releaseWindowFocus()
            } else if (typedChar in ' '..'~') { // Most of the ASCII characters
                // TODO: ctrl checks (ctrl+c, ctrl+v)
                addText(typedChar.toString())
            } else if (keyCode == 203) { // Left Arrow
                // TODO: shift + ctrl checks
                if (cursorLocation != 0) cursorLocation--
            } else if (keyCode == 205) { // Right Arrow
                // TODO: shift + ctrl checks
                if (cursorLocation < text.length) cursorLocation++
            } else if (keyCode == 14) {
                if (cursorLocation > 0) {
                    removeText(cursorLocation - 1, cursorLocation)
                    cursorLocation--
                }
            } else if (keyCode == 211) {
                if (cursorLocation < text.length) {
                    removeText(cursorLocation, cursorLocation + 1)
                }
            } else if (keyCode == 199) {
                cursorLocation = 0
            } else if (keyCode == 207) {
                cursorLocation = text.length
            }
        }

        onMouseClick { event ->
            if (!active) return@onMouseClick

            // TODO: optimize?
            cursorLocation = if (cursorLocation < text.length && event.relativeX > text.substring(0, cursorLocation).width()) {
                textPositionAt(cursorLocation, text.substring(0, cursorLocation).width().toFloat() + 3f, event.relativeX)
            } else {
                textPositionAt(0, 0f, event.relativeX)
            }
        }

        enableEffect(ScissorEffect())
    }

    fun setText(newText: String) = apply {
        text = newText
        textWidth = newText.width().toFloat()
        updateAction(newText)


    }

    fun getText() = text

    fun setActive(isActive: Boolean) = apply {
        active = isActive

        if (isActive) {
            animateCursor()
        } else {
            cursor.setColor(Color(255, 255, 255, 0).asConstraint())
        }
    }

    fun isActive() = active

    fun setMinWidth(constraint: WidthConstraint) = apply {
        minWidth = constraint
    }

    fun setMaxWidth(constraint: WidthConstraint) = apply {
        maxWidth = constraint
    }

    private fun addText(newText: String) {
        if (cursorLocation >= text.length) {
            text += newText
        } else {
            text = text.substring(0, cursorLocation) + newText + text.substring(cursorLocation)
        }

        cursorLocation += newText.length
    }

    private fun removeText(startPos: Int, endPos: Int) {
        text = text.substring(0, startPos) + text.substring(endPos)
    }

    private fun textPositionAt(textSearchStartPos: Int, baseX: Float, xPosition: Float): Int {
        var currentX = baseX

        for (i in textSearchStartPos until text.length) {
            val charWidth = text[i].width()
            if (currentX + (charWidth / 2) >= xPosition) return i
            currentX += charWidth
        }

        return text.length
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

    override fun getWidth(): Float {
        return super.getWidth()
    }

    override fun draw() {
        if (!active && text.isEmpty()) {
            UniversalGraphicsHandler.drawString(placeholder, getLeft(), getTop(), getColor().rgb, shadow)
            return super.draw()
        }

        if (cursorLocation >= text.length) {
            // Only draw one string
            UniversalGraphicsHandler.drawString(text, getLeft(), getTop(), getColor().rgb, shadow)
            cursor.setX((UniversalGraphicsHandler.getStringWidth(text) + 1).pixels())
            return super.draw()
        }

        val firstStringPart = text.substring(0, cursorLocation) // Does it make more sense to only calculate this once when the cursor moves?

        val firstStringWidth = UniversalGraphicsHandler.getStringWidth(firstStringPart)
        cursor.setX((firstStringWidth + 1).pixels())

        UniversalGraphicsHandler.drawString(
            firstStringPart,
            getLeft(),
            getTop(),
            getColor().rgb,
            shadow
        )

        UniversalGraphicsHandler.drawString(
            text.substring(cursorLocation),
            getLeft() + firstStringWidth + 3,
            getTop(),
            getColor().rgb,
            shadow
        )

        super.draw()
    }

    override fun animationFrame() {
        super.animationFrame()

        // TODO: Fix!
        if (minWidth != null && maxWidth != null) {
            val width = if (text.isEmpty() && !this.active) placeholderWidth else textWidth
            setWidth(width.pixels().minMax(minWidth!!, maxWidth!!))
        }
    }

    //
//    var text: String = ""
//        set(value) {
//            field = value
//            textWidth = UniversalGraphicsHandler.getStringWidth(text).toFloat()
//            updateAction(value)
//        }
//    var textWidth: Float = UniversalGraphicsHandler.getStringWidth(text).toFloat()
//    var textOffset: Float = 0f
//
//    var cursor: UIComponent = UIBlock(Color(255, 255, 255, 0))
//    var cursorLocation = 0
//    var active: Boolean = false
//        set(value) {
//            field = value
//            if (value) {
//                animateCursor()
//                cursorLocation = text.length
//            } else {
//                cursor.setColor(Color(255, 255, 255, 0).asConstraint())
//            }
//        }
//
//    var maxWidth: WidthConstraint = UniversalGraphicsHandler.getStringWidth(placeholder).pixels()
//    var minWidth: WidthConstraint = UniversalGraphicsHandler.getStringWidth(placeholder).pixels()
//
//    private var updateAction: (text: String) -> Unit = {}
//    private var activateAction: (text: String) -> Unit = {}
//
//    init {
//        setHeight(9.pixels())
//
//        alignCursor(if (text.isEmpty()) placeholder else text)
//
//        onKeyType { typedChar, keyCode ->
//            if (!active) return@onKeyType
//
//            if (keyCode == 1) {
//                releaseWindowFocus()
//            } else if (keyCode == 14) {
//                // backspace
//                if (text.isEmpty()) return@onKeyType
//                text = text.substring(0, text.length - 1)
//            } else if (keyCode == 203) {
//                // left arrow
//                if (cursorLocation > 0) cursorLocation--
//            } else if (keyCode == 205) {
//                // right arrow
//                if (cursorLocation < text.length) cursorLocation++
//            } else if (keyCode == 28 || keyCode == 156) {
//                activateAction(text)
//            } else if (
//                keyCode in 2..13 ||
//                keyCode in 16..27 ||
//                keyCode in 30..41 ||
//                keyCode in 43..53 ||
//                keyCode in 71..83 ||
//                keyCode in 145..147 ||
//                keyCode == 55 ||
//                keyCode == 181 ||
//                keyCode == 57
//            ) {
//                // normal key input
//                text += typedChar
//                cursorLocation++
//            }
//        }
//
//        cursor.constrain {
//            x = (textWidth + 1).pixels()
//            y = (0).pixels()
//            width = 1.pixels()
//            height = 8.pixels()
//        } childOf this
//    }
//
//    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int) {
//        if (isHovered() && active) {
//
//        }
//
//        super.mouseClick(mouseX, mouseY, button)
//    }
//
//    override fun draw() {
//        beforeDraw()
//
//        val y = getTop()
//        val color = getColor()
//
//        UniversalGraphicsHandler.enableBlend()
//
//        UniversalGraphicsHandler.scale(getTextScale().toDouble(), getTextScale().toDouble(), 1.0)
//
//        val displayText = if (text.isEmpty() && !this.active) placeholder else text
//
//        if (wrapped) {
//            val lines = alignCursor(displayText)
//            lines.forEachIndexed { index, line ->
//                UniversalGraphicsHandler.drawString(line, getLeft(), y + index * 9, color.rgb, shadow)
//            }
//        } else {
//            alignCursor()
//            UniversalGraphicsHandler.drawString(displayText, getLeft() + textOffset, y, color.rgb, shadow)
//        }
//
//        UniversalGraphicsHandler.scale(1 / getTextScale().toDouble(), 1 / getTextScale().toDouble(), 1.0)
//
//
//        super.draw()
//    }
//
//    /**
//     * Callback to run whenever the text in the input changes,
//     * i.e. every time a valid key is pressed.
//     */
//    fun onUpdate(action: (text: String) -> Unit) = apply {
//        updateAction = action
//    }
//
//    /**
//     * Callback to run when the user hits the Return key, thus
//     * "activating" the input.
//     */
//    fun onActivate(action: (text: String) -> Unit) = apply {
//        activateAction = action
//    }
//
//    private fun alignCursor(displayText: String = ""): List<String> {
//        val width = if (text.isEmpty() && !this.active) placeholderWidth else textWidth
//        setWidth(width.pixels().minMax(minWidth, maxWidth))
//
//        if (wrapped) {
//            val lines = getStringSplitToWidth(
//                displayText,
//                getWidth() / getTextScale()
//            )
//
//            cursor.setX((UniversalGraphicsHandler.getStringWidth(lines.last()) + 1).pixels())
//            cursor.setY(((lines.size - 1) * 9).pixels())
//            setHeight((lines.size * 9).pixels())
//            return lines
//        } else {
//            cursor.setX((text.substring(0, cursorLocation)).pixels())
//
//            textOffset = if (active) {
//                if (width > getWidth()) {
//                    cursor.setX(0.pixels(true))
//                    getWidth() - width - 1
//                } else 0f
//            } else 0f
//        }
//
//        return emptyList()
//    }
//
//    private fun animateCursor() {
//        if (!active) return
//        cursor.animate {
//            setColorAnimation(Animations.OUT_CIRCULAR, 0.5f, Color.WHITE.asConstraint())
//            onComplete {
//                if (!active) return@onComplete
//                cursor.animate {
//                    setColorAnimation(Animations.IN_CIRCULAR, 0.5f, Color(255, 255, 255, 0).asConstraint())
//                    onComplete {
//                        if (active) animateCursor()
//                    }
//                }
//            }
//        }
//    }
}