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
    var shadow: Boolean = true,
    private val selectionBackgroundColor: Color = Color.WHITE,
    private val selectionForegroundColor: Color = Color(64, 139, 229)
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
        y = CenterConstraint() - 0.5f.pixels()
        width = 1.pixels()
        height = 9f.pixels()
    } childOf this
    private var cursorLocation = 0

    private var isSelecting = false
    private var selectionEndLocation = 0
    private var hasMovedSelection = false

    init {
        setHeight(11.pixels())

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
                if (hasSelection()) {
                    removeSelection()
                } else if (cursorLocation > 0) {
                    removeText(cursorLocation - 1, cursorLocation)
                    setCursorLocation(cursorLocation - 1)
                }
            } else if (keyCode == 211) {
                if (hasSelection()) {
                    removeSelection()
                } else if (cursorLocation < text.length) {
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
            if (!active || event.mouseButton != 0)
                return@onMouseClick

            setCursorLocation(textPositionAt(event.relativeX))
            isSelecting = true
            hasMovedSelection = false
        }

        onMouseDrag { mouseX, _, mouseButton ->
            if (mouseButton != 0 || !isSelecting)
                return@onMouseDrag

            selectionEndLocation = textPositionAt(mouseX.coerceIn(0f, getWidth()))
            scrollTextPositionIntoView(selectionEndLocation)
        }

        onMouseRelease {
            isSelecting = false
            hasMovedSelection = false
        }

        cursor.animateAfterUnhide {
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
            selectionEndLocation = cursorLocation
            return
        }

        currentTextBeforeCursor = text.substring(0, newPosition)
        currentWidthBeforeCursor = currentTextBeforeCursor.width().toFloat()

        scrollTextPositionIntoView(newPosition)

        cursorLocation = newPosition
        selectionEndLocation = cursorLocation
    }

    private fun scrollTextPositionIntoView(textPosition: Int) {
        val widthBeforePosition = text.substring(0, textPosition).width().toFloat()

        if (textWidth < getWidth()) {
            currentTextOffset = 0f
        } else if (textPosition < cursorLocation && currentTextOffset > widthBeforePosition) {
            currentTextOffset = widthBeforePosition
        } else if (textPosition > cursorLocation && widthBeforePosition - currentTextOffset > getWidth()) {
            currentTextOffset = widthBeforePosition - getWidth()
        }
    }

    private fun hasSelection() = selectionEndLocation != cursorLocation
    private fun selectionStart() = if (cursorLocation < selectionEndLocation) cursorLocation else selectionEndLocation
    private fun selectionEnd() = if (cursorLocation > selectionEndLocation) cursorLocation else selectionEndLocation

    private fun removeSelection() {
        removeText(selectionStart(), selectionEnd())
        // TODO: fix where cursor ends up?
        selectionEndLocation = cursorLocation
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

    private fun textPositionY() = getTop() + 1f

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

        if (hasSelection()) {
            hasMovedSelection = true

            var currentXPos = getLeft() - currentTextOffset

            if (selectionStart() > 0) {
                val preSelectionText = text.substring(0, selectionStart())

                UniversalGraphicsHandler.drawString(preSelectionText, currentXPos, textPositionY(), getColor().rgb, shadow)
                currentXPos += preSelectionText.width()
            }

            val selectedText = text.substring(selectionStart(), selectionEnd())
            val selectedTextWidth = selectedText.width()

            UIBlock.drawBlock(
                selectionBackgroundColor,
                currentXPos.toDouble(),
                getTop().toDouble(),
                currentXPos.toDouble() + selectedTextWidth,
                getBottom().toDouble()
            )
            UniversalGraphicsHandler.drawString(selectedText, currentXPos, textPositionY(), selectionForegroundColor.rgb, false)

            currentXPos += selectedTextWidth

            if (selectionEnd() < text.length) {
                val postSelectionText = text.substring(selectionEnd())
                UniversalGraphicsHandler.drawString(postSelectionText, currentXPos, textPositionY(), getColor().rgb, shadow)
            }

            return super.draw()
        }

        if (cursorLocation >= text.length || (isSelecting && hasMovedSelection)) { // Only draw one string
            if (!isSelecting) {
                cursor.unhide()
                cursor.setX((currentWidthBeforeCursor).coerceAtMost(getWidth()).pixels())
            }

            UniversalGraphicsHandler.drawString(text, getLeft() - currentTextOffset, textPositionY(), getColor().rgb, shadow)
            return super.draw()
        }

        if (isSelecting) {
            cursor.hide(instantly = true)
        } else {
            cursor.unhide()
            cursor.setX((currentWidthBeforeCursor - currentTextOffset).pixels())
        }

        UniversalGraphicsHandler.drawString(
            currentTextBeforeCursor,
            getLeft() - currentTextOffset,
            textPositionY(),
            getColor().rgb,
            shadow
        )

        UniversalGraphicsHandler.drawString(
            text.substring(cursorLocation),
            getLeft() - currentTextOffset + currentWidthBeforeCursor,
            textPositionY(),
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