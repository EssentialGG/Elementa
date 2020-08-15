package club.sk1er.elementa.components.input

import club.sk1er.elementa.constraints.HeightConstraint
import club.sk1er.elementa.dsl.max
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.width
import club.sk1er.mods.core.universal.UniversalKeyboard
import java.awt.Color

class UIMultilineTextInput @JvmOverloads constructor(
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
    private var maxHeight: HeightConstraint? = null

    fun setMaxHeight(maxHeight: HeightConstraint) = apply {
        this.maxHeight = maxHeight
    }

    fun setMaxLines(maxLines: Int) = apply {
        this.maxHeight = (9 * maxLines).pixels()
    }

    override fun getText() = textualLines.joinToString("\n") { it.text }

    override fun textToLines(text: String): List<String> {
        return text.split('\n')
    }

    override fun scrollIntoView(pos: LinePosition) {
        val visualPos = pos.toVisualPos()

        val visualLineOffset = visualPos.line * -9f

        if (targetVerticalScrollingOffset < visualLineOffset) {
            targetVerticalScrollingOffset = visualLineOffset
        } else if (visualLineOffset - 9f < targetVerticalScrollingOffset - getHeight()) {
            targetVerticalScrollingOffset += visualLineOffset - 9f - (targetVerticalScrollingOffset - getHeight())
        }
    }

    override fun recalculateDimensions() {
        if (maxHeight == null)
            return

        setHeight((9 * visualLines.size).pixels().max(maxHeight!!))
    }

    override fun onEnterPressed() {
        if (UniversalKeyboard.isShiftKeyDown()) {
            commitTextAddition("\n")
            updateAction(getText())
        } else {
            activateAction(getText())
        }
    }

    override fun draw() {
        if (!active && !hasText()) {
            // TODO: Avoid splitting the placeholder every draw frame
            val placeholderLines = splitTextForWrapping(placeholder, getWidth())
            for ((i, placeHolderLine) in placeholderLines.withIndex())
                drawUnselectedText(placeHolderLine, getLeft(), i)

            return super.draw()
        }

        if (hasSelection()) {
            cursorComponent.hide(instantly = true)
        } else {
            cursorComponent.unhide()
            val (cursorPosX, cursorPosY) = cursor.toScreenPos()
            cursorComponent.setX(cursorPosX.pixels())
            cursorComponent.setY(cursorPosY.pixels())
        }

        val (selectionStart, selectionEnd) = getSelection()

        for ((i, visualLine) in visualLines.withIndex()) {
            val topOffset = (9 * i) + verticalScrollingOffset
            if (topOffset < -9 || topOffset > getHeight() + 9)
                continue

            if (!hasSelection() || i < selectionStart.line || i > selectionEnd.line) {
                drawUnselectedText(visualLine.text, getLeft(), i)
            } else {
                val startText = when {
                    i == selectionStart.line && selectionStart.column > 0 -> {
                        visualLine.text.substring(0, selectionStart.column)
                    }
                    else -> ""
                }

                val selectedText = when {
                    selectionStart.line == selectionEnd.line -> visualLine.text.substring(
                        selectionStart.column,
                        selectionEnd.column
                    )
                    i > selectionStart.line && i < selectionEnd.line -> visualLine.text
                    i == selectionStart.line -> visualLine.text.substring(selectionStart.column)
                    i == selectionEnd.line -> visualLine.text.substring(0, selectionEnd.column)
                    else -> ""
                }

                val endText = when {
                    i == selectionEnd.line && selectionEnd.column < visualLines[i].length -> {
                        visualLine.text.substring(selectionEnd.column)
                    }
                    else -> ""
                }

                val startTextWidth = startText.width()
                val selectedTextWidth = selectedText.width()

                val newlinePadding = if (i < selectionEnd.line) spaceWidth else 0f

                if (startText.isNotEmpty())
                    drawUnselectedText(startText, getLeft(), i)

                if (selectedText.isNotEmpty() || newlinePadding != 0f) {
                    drawSelectedText(
                        selectedText,
                        getLeft() + startTextWidth,
                        getLeft() + startTextWidth + selectedTextWidth + newlinePadding,
                        i
                    )
                }

                if (endText.isNotEmpty())
                    drawUnselectedText(endText, getLeft() + startTextWidth + selectedTextWidth, i)
            }
        }

        super.draw()
    }

    override fun screenPosToVisualPos(x: Float, y: Float): LinePosition {
        val realY = y - verticalScrollingOffset

        if (realY <= 0)
            return LinePosition(0, 0, isVisual = true)

        val line = (realY / 9).toInt()
        if (line > visualLines.lastIndex)
            return LinePosition(visualLines.lastIndex, visualLines.last().text.length, isVisual = true)

        val text = visualLines[line].text
        var column = 0
        var currWidth = 0f

        if (x <= 0)
            return LinePosition(line, 0, isVisual = true)
        if (x >= getWidth())
            return LinePosition(line, visualLines[line].text.length, isVisual = true)

        for (char in text.toCharArray()) {
            val charWidth = char.width()
            if (currWidth + (charWidth / 2) >= x)
                return LinePosition(line, column, isVisual = true)

            currWidth += charWidth
            column++
        }

        return LinePosition(line, column, isVisual = true)
    }


}