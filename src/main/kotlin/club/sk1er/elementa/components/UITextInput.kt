package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalKeyboard
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import kotlin.math.abs

open class UITextInput @JvmOverloads constructor(
    private val placeholder: String = "",
    var shadow: Boolean = true,
    private val selectionBackgroundColor: Color = Color.WHITE,
    private val selectionForegroundColor: Color = Color(64, 139, 229),
    private val allowInactiveSelection: Boolean = false,
    private val inactiveSelectionBackgroundColor: Color = Color(176, 176, 176),
    private val inactiveSelectionForegroundColor: Color = Color.WHITE
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
    private var lastSelectionMoveTimestamp = System.currentTimeMillis()

    init {
        setHeight(11.pixels())

        onKeyType { typedChar, keyCode ->
            if (!active) return@onKeyType

            if (keyCode == 1) {
                releaseWindowFocus()
            } else if (UniversalKeyboard.isKeyComboCtrlA(keyCode)) {
                setCursorLocation(text.length)
                selectionEndLocation = 0
            } else if (UniversalKeyboard.isKeyComboCtrlC(keyCode) && hasSelection()) {
                copySelection()
            } else if (UniversalKeyboard.isKeyComboCtrlX(keyCode) && hasSelection()) {
                copySelection()
                removeSelection()
            } else if (UniversalKeyboard.isKeyComboCtrlV(keyCode)) {
                addText(Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String)
            } else if (typedChar in ' '..'~') { // Most of the ASCII characters
                addText(typedChar.toString())
            } else if (keyCode == 203) { // Left Arrow
                val newCursorLocation = when {
                    cursorLocation == 0 -> 0
                    UniversalKeyboard.isCtrlKeyDown() && (!hasSelection() || UniversalKeyboard.isShiftKeyDown()) -> {
                        firstWordBreakBefore(cursorLocation)
                    }
                    hasSelection() -> selectionStart()
                    else -> cursorLocation - 1
                }

                if (UniversalKeyboard.isShiftKeyDown()) {
                    if (!isSelecting) {
                        isSelecting = true
                        hasMovedSelection = true
                        selectionEndLocation = cursorLocation
                    }

                    scrollTextPositionIntoView(newCursorLocation)
                    cursorLocation = newCursorLocation
                } else {
                    setCursorLocation(newCursorLocation)
                }

                if (!hasSelection()) {
                    isSelecting = false
                    hasMovedSelection = false
                }
            } else if (keyCode == 205) { // Right Arrow
                val newCursorLocation = when {
                    cursorLocation >= text.length -> text.length
                    UniversalKeyboard.isCtrlKeyDown() && (!hasSelection() || UniversalKeyboard.isShiftKeyDown()) -> {
                        firstWordBreakAfter(cursorLocation)
                    }
                    hasSelection() -> selectionEnd()
                    else -> cursorLocation + 1
                }

                if (UniversalKeyboard.isShiftKeyDown()) {
                    if (!isSelecting) {
                        isSelecting = true
                        hasMovedSelection = true
                        selectionEndLocation = cursorLocation
                    }

                    scrollTextPositionIntoView(newCursorLocation)
                    cursorLocation = newCursorLocation
                } else {
                    setCursorLocation(newCursorLocation)
                }

                if (!hasSelection()) {
                    isSelecting = false
                    hasMovedSelection = false
                }
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

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSelectionMoveTimestamp > 50) {
                if (mouseX >= getWidth()) {
                    scrollTextPositionIntoView(selectionEnd() + 1)
                    lastSelectionMoveTimestamp = currentTime
                } else if (mouseX <= 0) {
                    scrollTextPositionIntoView(selectionStart() - 1)
                    lastSelectionMoveTimestamp = currentTime
                }
            }
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
            if (text.isNotEmpty() && (!allowInactiveSelection || !hasSelection())) {
                setCursorLocation(text.length)
            }
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
        when {
            hasSelection() -> {
                cursorLocation = selectionStart()
                text = text.substring(0, selectionStart()) + newText + text.substring(selectionEnd())
            }
            cursorLocation >= text.length -> {
                text += newText
            }
            else -> {
                text = text.substring(0, cursorLocation) + newText + text.substring(cursorLocation)
            }
        }

        textWidth = text.width().toFloat()
        setCursorLocation(cursorLocation + newText.length)
        updateAction(text)
    }

    private fun removeText(startPos: Int, endPos: Int) {
        text = text.substring(0, startPos) + text.substring(endPos)
        textWidth = text.width().toFloat()

        if (abs(startPos - endPos) > 1)
            setCursorLocation(startPos)

        if (cursorLocation < text.length) {
            val visibleWidthBeforeCursor = currentWidthBeforeCursor - currentTextOffset
            val widthAfterCursor = text.substring(cursorLocation).width()

            if (visibleWidthBeforeCursor + widthAfterCursor < getWidth()) {
                currentTextOffset = (textWidth - getWidth()).coerceAtLeast(0f)
            }
        }

        updateAction(text)
    }

    private fun setCursorLocation(newPosition: Int) {
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
        if (textPosition < 0 || textPosition > text.length)
            return

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
        selectionEndLocation = cursorLocation
        isSelecting = false
        hasMovedSelection = false
    }

    private fun copySelection() {
        val string = StringSelection(text.substring(selectionStart(), selectionEnd()))
        Toolkit.getDefaultToolkit().systemClipboard.setContents(string, string)
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

    private fun firstWordBreakAfter(location: Int): Int {
        if (location >= text.length)
            return text.length

        val startedAtWhiteSpace = text[location].isWhitespace()

        for (i in (location + 1) until text.length) {
            if ((startedAtWhiteSpace && !text[i].isWhitespace()) || (!startedAtWhiteSpace && text[i].isWhitespace()))
                return i
        }

        return text.length
    }

    private fun firstWordBreakBefore(location: Int): Int {
        if (location <= 0)
            return 0

        val startedAtWhiteSpace = text[location - 1].isWhitespace()

        for (i in (location - 1) downTo 1) {
            if ((startedAtWhiteSpace && !text[i - 1].isWhitespace()) || (!startedAtWhiteSpace && text[i - 1].isWhitespace()))
                return i
        }

        return 0
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
            cursor.hide(instantly = true)

            var currentXPos = getLeft() - currentTextOffset

            if (selectionStart() > 0) {
                val preSelectionText = text.substring(0, selectionStart())

                UniversalGraphicsHandler.drawString(
                    preSelectionText,
                    currentXPos,
                    textPositionY(),
                    getColor().rgb,
                    shadow
                )
                currentXPos += preSelectionText.width()
            }

            val selectedText = text.substring(selectionStart(), selectionEnd())
            val selectedTextWidth = selectedText.width()

            UIBlock.drawBlock(
                if (active) selectionBackgroundColor else inactiveSelectionBackgroundColor,
                currentXPos.toDouble(),
                getTop().toDouble(),
                currentXPos.toDouble() + selectedTextWidth,
                getBottom().toDouble()
            )
            UniversalGraphicsHandler.drawString(
                selectedText,
                currentXPos,
                textPositionY(),
                if (active) selectionForegroundColor.rgb else inactiveSelectionForegroundColor.rgb,
                false
            )

            currentXPos += selectedTextWidth

            if (selectionEnd() < text.length) {
                val postSelectionText = text.substring(selectionEnd())
                UniversalGraphicsHandler.drawString(
                    postSelectionText,
                    currentXPos,
                    textPositionY(),
                    getColor().rgb,
                    shadow
                )
            }

            return super.draw()
        }

        if (cursorLocation >= text.length || (isSelecting && hasMovedSelection)) { // Only draw one string
            if (!isSelecting) {
                cursor.unhide()
                cursor.setX((currentWidthBeforeCursor).coerceAtMost(getWidth()).pixels())
            }

            UniversalGraphicsHandler.drawString(
                text,
                getLeft() - currentTextOffset,
                textPositionY(),
                getColor().rgb,
                shadow
            )
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