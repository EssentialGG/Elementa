package club.sk1er.elementa.components.input

import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.dsl.minMax
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.width
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import java.awt.Color

open class UITextInput @JvmOverloads constructor(
    placeholder: String = "",
    shadow: Boolean = true,
    selectionBackgroundColor: Color = Color.WHITE,
    selectionForegroundColor: Color = Color(64, 139, 229),
    allowInactiveSelection: Boolean = false,
    inactiveSelectionBackgroundColor: Color = Color(176, 176, 176),
    inactiveSelectionForegroundColor: Color = Color.WHITE
) : AbstractTextInput(
    placeholder,
    shadow,
    selectionBackgroundColor,
    selectionForegroundColor,
    allowInactiveSelection,
    inactiveSelectionBackgroundColor,
    inactiveSelectionForegroundColor
) {
    private var minWidth: WidthConstraint? = null
    private var maxWidth: WidthConstraint? = null

    private val placeholderWidth = placeholder.width().toFloat()

    fun setMinWidth(constraint: WidthConstraint) = apply {
        minWidth = constraint
    }

    fun setMaxWidth(constraint: WidthConstraint) = apply {
        maxWidth = constraint
    }

    override fun getText() = textualLines.first().text

    override fun textToLines(text: String): List<String> {
        return listOf(text.replace('\n', ' '))
    }

    override fun scrollIntoView(pos: LinePosition) {
        val column = pos.column
        val lineText = getText()
        if (column < 0 || column > lineText.length)
            return

        val widthBeforePosition = lineText.substring(0, column).width().toFloat()

        when {
            getText().width() < getWidth() -> {
                horizontalScrollingOffset = 0f
            }
            horizontalScrollingOffset > widthBeforePosition -> {
                horizontalScrollingOffset = widthBeforePosition
            }
            widthBeforePosition - horizontalScrollingOffset > getWidth() -> {
                horizontalScrollingOffset = widthBeforePosition - getWidth()
            }
        }
    }

    override fun screenPosToVisualPos(x: Float, y: Float): LinePosition {
        val targetXPos = x + horizontalScrollingOffset
        var currentX = 0f

        val line = getText()

        for (i in line.indices) {
            val charWidth = line[i].width()
            if (currentX + (charWidth / 2) >= targetXPos) return LinePosition(0, i, isVisual = true)
            currentX += charWidth
        }

        return LinePosition(0, line.length, isVisual = true)
    }

    override fun recalculateDimensions() {
        if (minWidth != null && maxWidth != null) {
            val width = if (!hasText() && !this.active) placeholderWidth else getText().width().toFloat()
            setWidth(width.pixels().minMax(minWidth!!, maxWidth!!))
        }
    }

    override fun splitTextForWrapping(text: String, maxLineWidth: Float): List<String> {
        return listOf(text)
    }

    override fun onEnterPressed() {
        activateAction(getText())
    }

    override fun draw() {
        beforeDraw()

        if (!active && !hasText()) {
            UniversalGraphicsHandler.drawString(placeholder, getLeft(), getTop(), getColor().rgb, shadow)
            return super.draw()
        }

        val lineText = getText()

        if (hasSelection()) {
            var currentX = getLeft()
            cursorComponent.hide(instantly = true)

            if (!selectionStart().isAtLineStart) {
                val preSelectionText = lineText.substring(0, selectionStart().column)
                drawUnselectedText(preSelectionText, currentX, row = 0)
                currentX += preSelectionText.width()
            }

            val selectedText = lineText.substring(selectionStart().column, selectionEnd().column)
            val selectedTextWidth = selectedText.width()
            drawSelectedText(selectedText, currentX, currentX + selectedTextWidth, row = 0)
            currentX += selectedTextWidth

            if (!selectionEnd().isAtLineEnd) {
                drawUnselectedText(lineText.substring(selectionEnd().column), currentX, row = 0)
            }
        } else {
            cursorComponent.unhide()
            val (cursorPosX, _) = cursor.toScreenPos()
            cursorComponent.setX(cursorPosX.pixels())

            drawUnselectedText(lineText, getLeft(), 0)
        }

        super.draw()
    }
}