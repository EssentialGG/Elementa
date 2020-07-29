package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
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

    private var currentTextBeforeCursor = ""
    private var currentWidthBeforeCursor = 0f
    private var currentTextOffset = 0f

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
                if (cursorLocation != 0) setCursorLocation(cursorLocation - 1)
            } else if (keyCode == 205) { // Right Arrow
                // TODO: shift + ctrl checks
                if (cursorLocation < text.length) setCursorLocation(cursorLocation + 1)
            } else if (keyCode == 14) {
                if (cursorLocation > 0) {
                    removeText(cursorLocation - 1, cursorLocation)
                    setCursorLocation(cursorLocation - 1)
                }
            } else if (keyCode == 211) {
                if (cursorLocation < text.length) {
                    removeText(cursorLocation, cursorLocation + 1)
                }
            } else if (keyCode == 199) {
                setCursorLocation(0)
            } else if (keyCode == 207) {
                setCursorLocation(text.length)
            } else if (keyCode == 28) {
                activateAction(text)
            }
        }

        onMouseClick { event ->
            if (!active) return@onMouseClick

            // TODO: optimize?
            setCursorLocation(textPositionAt(event.relativeX))
        }
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
            if (text.isNotEmpty())
                cursorLocation = text.length
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

        textWidth = text.width().toFloat()
        setCursorLocation(cursorLocation + newText.length)
        updateAction(text)
    }

    private fun removeText(startPos: Int, endPos: Int) {
        text = text.substring(0, startPos) + text.substring(endPos)
        textWidth = text.width().toFloat()
        updateAction(text)
    }

    private fun setCursorLocation(newPosition: Int) {
        if (newPosition == cursorLocation)
            return

        recalculateWidth()

        if (newPosition >= text.length && textWidth >= getWidth()) {
            currentTextBeforeCursor = text
            currentWidthBeforeCursor = text.width().toFloat()
            currentTextOffset = textWidth - getWidth()
            cursorLocation = newPosition
            return
        }

        currentTextBeforeCursor = text.substring(0, newPosition)
        currentWidthBeforeCursor = currentTextBeforeCursor.width().toFloat()

        if (textWidth < getWidth()) {
            currentTextOffset = 0f
        } else if (newPosition < cursorLocation && currentTextOffset > currentWidthBeforeCursor) {
            currentTextOffset = currentWidthBeforeCursor
        } else if (newPosition > cursorLocation && currentWidthBeforeCursor - currentTextOffset > getWidth()) {
            currentTextOffset = currentWidthBeforeCursor - getWidth()
        }

        cursorLocation = newPosition
    }

    private fun textPositionAt(relativeXPosition: Float): Int {
        // TODO: Perhaps optimize this by only searching up until/only after the cursor depending on the click pos
        val targetXPos = relativeXPosition + currentTextOffset
        var currentX = 0f

        for (i in text.indices) {
            val charWidth = text[i].width()
            if (currentX + (charWidth / 2) >= targetXPos) return i
            currentX += charWidth
        }

        return text.length
    }

    private fun recalculateWidth() {
        if (minWidth != null && maxWidth != null) {
            val width = if (text.isEmpty() && !this.active) placeholderWidth else textWidth
            setWidth(width.pixels().minMax(minWidth!!, maxWidth!!))
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

    override fun draw() {
        if (!active && text.isEmpty()) {
            UniversalGraphicsHandler.drawString(placeholder, getLeft(), getTop(), getColor().rgb, shadow)
            return super.draw()
        }

        if (cursorLocation >= text.length) { // Only draw one string
            cursor.setX((currentWidthBeforeCursor + 1).coerceAtMost(getWidth()).pixels())

            UniversalGraphicsHandler.drawString(text, getLeft() - currentTextOffset, getTop(), getColor().rgb, shadow)
            return super.draw()
        }

        cursor.setX((currentWidthBeforeCursor - currentTextOffset + 1).pixels())

        UniversalGraphicsHandler.drawString(
            currentTextBeforeCursor,
            getLeft() - currentTextOffset,
            getTop(),
            getColor().rgb,
            shadow
        )

        UniversalGraphicsHandler.drawString(
            text.substring(cursorLocation),
            getLeft() - currentTextOffset + currentWidthBeforeCursor + 3,
            getTop(),
            getColor().rgb,
            shadow
        )

        super.draw()
    }

    override fun animationFrame() {
        super.animationFrame()

        recalculateWidth()
    }
}