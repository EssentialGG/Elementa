package club.sk1er.elementa.components.input

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalKeyboard
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import kotlin.math.abs

abstract class AbstractTextInput(
    var placeholder: String,
    var shadow: Boolean,
    protected val selectionBackgroundColor: Color,
    protected val selectionForegroundColor: Color,
    protected val allowInactiveSelection: Boolean,
    protected val inactiveSelectionBackgroundColor: Color,
    protected val inactiveSelectionForegroundColor: Color
) : UIComponent() {
    protected var active = false

    protected var updateAction: (text: String) -> Unit = {}
    protected var activateAction: (text: String) -> Unit = {}

    protected val textualLines = mutableListOf(TextualLine("", 0..0))
    protected val visualLines = mutableListOf(VisualLine("", 0))

    protected var verticalScrollingOffset = 0f
    protected var targetVerticalScrollingOffset = 0f
    protected var horizontalScrollingOffset = 0f
    protected var cursorNeedsRefocus = false

    protected var lastSelectionMoveTimestamp = System.currentTimeMillis()
    protected var selectionMode = SelectionMode.None
    protected var initiallySelectedLine = -1
    protected var initiallySelectedWord = LinePosition(0, 0, true) to LinePosition(0, 0, true)

    protected val spaceWidth = ' '.width()

    protected var cursorComponent: UIComponent = UIBlock(Color(255, 255, 255, 0)).constrain {
        y = CenterConstraint() - 0.5f.pixels()
        width = 1.pixels()
        height = 9f.pixels()
    } childOf this

    protected var cursor = LinePosition(0, 0, isVisual = true)
        set(value) {
            field = value.toVisualPos()
        }

    protected var otherSelectionEnd = LinePosition(0, 0, isVisual = true)
        set(value) {
            field = value.toVisualPos()
        }

    enum class SelectionMode {
        None,
        Character,
        Word,
        Line,
    }

    init {
        setHeight(9.pixels())

        onKeyType { typedChar, keyCode ->
            if (!active) return@onKeyType

            if (keyCode == 1) {
                releaseWindowFocus()
            } else if (UniversalKeyboard.isKeyComboCtrlA(keyCode)) {
                selectAll()
            } else if (UniversalKeyboard.isKeyComboCtrlC(keyCode) && hasSelection()) {
                copySelection()
            } else if (UniversalKeyboard.isKeyComboCtrlX(keyCode) && hasSelection()) {
                copySelection()
                deleteSelection()
            } else if (UniversalKeyboard.isKeyComboCtrlV(keyCode)) {
                addText(Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String)
            } else if (typedChar in ' '..'~') { // Most of the ASCII characters
                addText(typedChar.toString())
            } else if (keyCode == 203) { // Left Arrow
                val holdingShift = UniversalKeyboard.isShiftKeyDown()
                val holdingCtrl = UniversalKeyboard.isCtrlKeyDown()

                val newCursorPosition = when {
                    holdingCtrl -> getNearestWordBoundary(cursor, Direction.Left)
                    hasSelection() -> if (holdingShift) cursor.offsetColumn(-1) else selectionStart()
                    else -> cursor.offsetColumn(-1)
                }

                if (!holdingShift) {
                    setCursorPosition(newCursorPosition)
                    return@onKeyType
                }

                cursor = newCursorPosition
                cursorNeedsRefocus = true
            } else if (keyCode == 205) { // Right Arrow
                val holdingShift = UniversalKeyboard.isShiftKeyDown()
                val holdingCtrl = UniversalKeyboard.isCtrlKeyDown()

                val newCursorPosition = when {
                    holdingCtrl -> getNearestWordBoundary(cursor, Direction.Right)
                    hasSelection() -> if (holdingShift) cursor.offsetColumn(1) else selectionEnd()
                    else -> cursor.offsetColumn(1)
                }

                if (!holdingShift) {
                    setCursorPosition(newCursorPosition)
                    return@onKeyType
                }

                cursor = newCursorPosition
                cursorNeedsRefocus = true
            } else if (keyCode == 200) { // Up arrow
                val newVisualPos = if (cursor.line == 0) {
                    LinePosition(0, 0, isVisual = true)
                } else {
                    val (currX, currY) = cursor.toScreenPos()
                    screenPosToVisualPos(currX, currY - 9)
                }

                if (UniversalKeyboard.isShiftKeyDown()) {
                    cursor = newVisualPos
                    cursorNeedsRefocus = true
                } else {
                    setCursorPosition(newVisualPos)
                }
            } else if (keyCode == 208) { // Down arrow
                val newVisualPos = if (cursor.line == visualLines.lastIndex) {
                    LinePosition(visualLines.lastIndex, visualLines.last().length, isVisual = true)
                } else {
                    val (currX, currY) = cursor.toScreenPos()
                    screenPosToVisualPos(currX, currY + 9)
                }

                if (UniversalKeyboard.isShiftKeyDown()) {
                    cursor = newVisualPos
                    cursorNeedsRefocus = true
                } else {
                    setCursorPosition(newVisualPos)
                }
            } else if (keyCode == 14) { // Backspace
                if (hasSelection()) {
                    deleteSelection()
                } else if (!cursor.isAtAbsoluteStart) {
                    val startPos = cursor.offsetColumn(-1).toTextualPos()
                    val endPos = cursor.toTextualPos()
                    removeText(startPos, endPos)
                    setCursorPosition(startPos.toVisualPos())
                }
            } else if (keyCode == 211) { // Delete
                if (hasSelection()) {
                    deleteSelection()
                } else if (!cursor.isAtAbsoluteEnd) {
                    removeText(cursor, cursor.offsetColumn(1))
                }
            } else if (keyCode == 199) { // Home
                if (UniversalKeyboard.isShiftKeyDown()) {
                    cursor = cursor.withColumn(0)
                    cursorNeedsRefocus = true
                } else {
                    setCursorPosition(cursor.withColumn(0))
                }
            } else if (keyCode == 207) { // End
                cursor.withColumn(visualLines[cursor.line].length).also {
                    if (UniversalKeyboard.isShiftKeyDown()) {
                        cursor = it
                        cursorNeedsRefocus = true
                    } else {
                        setCursorPosition(it)
                    }
                }
            } else if (keyCode == 28) { // Enter
                onEnterPressed()
            }
        }

        onMouseScroll { delta ->
            val heightDifference = getHeight() - visualLines.size * 9f
            if (heightDifference > 0)
                return@onMouseScroll
            targetVerticalScrollingOffset = (targetVerticalScrollingOffset + delta * 9f).coerceIn(heightDifference, 0f)
        }

        onMouseClick { event ->
            if (!active || event.mouseButton != 0)
                return@onMouseClick

            val clickedVisualPos = screenPosToVisualPos(event.relativeX, event.relativeY)

            var clickCount = event.clickCount % 3
            if (clickCount == 0 && clickedVisualPos.line != cursor.line)
                clickCount = 1
            else if (clickCount == 2 && cursor != clickedVisualPos)
                clickCount = 1

            when (clickCount) {
                0 -> {
                    selectionMode = SelectionMode.Line
                    otherSelectionEnd = clickedVisualPos.withColumn(visualLines[cursor.line].length)
                    initiallySelectedLine = cursor.line
                }
                1 -> {
                    selectionMode = SelectionMode.Character
                    setCursorPosition(clickedVisualPos)
                }
                2 -> {
                    selectionMode = SelectionMode.Word
                    cursor = getNearestWordBoundary(
                        clickedVisualPos,
                        Direction.Left
                    )
                    cursorNeedsRefocus = true
                    otherSelectionEnd = getNearestWordBoundary(
                        clickedVisualPos,
                        Direction.Right
                    )
                    initiallySelectedWord = cursor to otherSelectionEnd
                }
            }
        }

        onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton != 0 || selectionMode == SelectionMode.None)
                return@onMouseDrag

            val draggedVisualPos = screenPosToVisualPos(mouseX, mouseY)

            when (selectionMode) {
                SelectionMode.Character -> otherSelectionEnd = draggedVisualPos
                SelectionMode.Line -> if (initiallySelectedLine < draggedVisualPos.line) {
                    cursor = LinePosition(initiallySelectedLine, 0, isVisual = true)
                    otherSelectionEnd = draggedVisualPos.withColumn(visualLines[draggedVisualPos.line].length)
                } else {
                    cursor = draggedVisualPos.withColumn(0)
                    otherSelectionEnd = LinePosition(
                        initiallySelectedLine,
                        visualLines[initiallySelectedLine].length,
                        isVisual = true
                    )
                }
                SelectionMode.Word -> when {
                    draggedVisualPos < initiallySelectedWord.first -> {
                        cursor = getNearestWordBoundary(
                            draggedVisualPos,
                            Direction.Left
                        )
                        otherSelectionEnd = initiallySelectedWord.second
                    }
                    draggedVisualPos > initiallySelectedWord.second -> {
                        cursor = initiallySelectedWord.first
                        otherSelectionEnd = getNearestWordBoundary(
                            draggedVisualPos,
                            Direction.Right
                        )
                    }
                    else -> {
                        cursor = initiallySelectedWord.first
                        otherSelectionEnd = initiallySelectedWord.second
                    }
                }
                SelectionMode.None -> {
                }
            }

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSelectionMoveTimestamp > 50) {
                if (mouseY <= 0) {
                    targetVerticalScrollingOffset = (targetVerticalScrollingOffset + 9).coerceAtMost(0f)
                    lastSelectionMoveTimestamp = currentTime
                } else if (mouseY >= getHeight()) {
                    val heightDifference = getHeight() - visualLines.size * 9f
                    targetVerticalScrollingOffset = (targetVerticalScrollingOffset - 9).coerceIn(heightDifference, 0f)
                    lastSelectionMoveTimestamp = currentTime
                } else if (mouseX <= 0) {
                    scrollIntoView(draggedVisualPos.offsetColumn(-1))
                    lastSelectionMoveTimestamp = currentTime
                } else if (mouseX >= getWidth()) {
                    scrollIntoView(draggedVisualPos.offsetColumn(1))
                    lastSelectionMoveTimestamp = currentTime
                }
            }
        }

        onMouseRelease {
            selectionMode = SelectionMode.None
        }

        cursorComponent.animateAfterUnhide {
            setColorAnimation(Animations.OUT_CIRCULAR, 0.5f, Color.WHITE.asConstraint())
            onComplete {
                if (!active) return@onComplete
                cursorComponent.animate {
                    setColorAnimation(Animations.IN_CIRCULAR, 0.5f, Color(255, 255, 255, 0).asConstraint())
                    onComplete {
                        if (active) animateCursor()
                    }
                }
            }
        }

        enableEffect(ScissorEffect())
    }

    abstract fun getText(): String
    fun setText(text: String) {
        removeText(
            LinePosition(0, 0, isVisual = true),
            LinePosition(visualLines.lastIndex, visualLines.last().length, isVisual = true)
        )
        addText(text)
    }

    protected abstract fun scrollIntoView(pos: LinePosition)
    protected abstract fun screenPosToVisualPos(x: Float, y: Float): LinePosition
    protected abstract fun recalculateDimensions()
    protected abstract fun textToLines(text: String): List<String>
    protected abstract fun onEnterPressed()

    fun setActive(isActive: Boolean) = apply {
        active = isActive

        if (isActive) {
            cursorComponent.unhide()
            animateCursor()
        } else {
            cursorComponent.setColor(Color(255, 255, 255, 0).asConstraint())
            if (hasText() && (!allowInactiveSelection || !hasSelection())) {
                setCursorPosition(LinePosition(visualLines.lastIndex, visualLines.last().length, isVisual = true))
            }
        }
    }

    fun isActive() = active

    fun onUpdate(listener: (text: String) -> Unit) {
        updateAction = listener
    }

    fun onActivate(listener: (text: String) -> Unit) {
        activateAction = listener
    }

    protected fun addText(newText: String) {
        if (hasSelection()) {
            deleteSelection()
            addText(newText)
            return
        }

        val textPos = cursor.toTextualPos()
        val textualLine = textualLines[textPos.line]

        val lines = textToLines(newText)
        when {
            lines.isEmpty() -> {
                return
            }
            lines.size == 1 -> {
                textualLine.addTextAt(lines.first(), textPos.column)
            }
            else -> {
                val newTextualLines = lines.drop(1).map { TextualLine(it) }

                if (textPos.column < textualLine.text.length) {
                    val textAfterInsertion = textualLine.text.substring(textPos.column)
                    textualLine.text = textualLine.text.substring(0, textPos.column) + lines.first()
                    newTextualLines.last().text += textAfterInsertion
                } else {
                    textualLine.addTextAt(lines.first(), textPos.column)
                }

                textualLines.addAll(textPos.line + 1, newTextualLines)
            }
        }

        recalculateAllVisualLines()
        setCursorPosition(textPos.offsetColumn(newText.length).toVisualPos())

        updateAction(getText())
    }

    protected open fun recalculateVisualLinesFor(textualLineIndex: Int) {
        val textualLine = textualLines[textualLineIndex]
        val firstVisualIndex = textualLine.visualIndices.first
        repeat(textualLine.visualIndices.count()) {
            if (firstVisualIndex < visualLines.size)
                visualLines.removeAt(firstVisualIndex)
        }
        val splitLines = splitTextForWrapping(textualLine.text, getWidth())

        visualLines.addAll(firstVisualIndex, splitLines.map { VisualLine(it, textualLineIndex) })
        textualLine.visualIndices = firstVisualIndex until firstVisualIndex + splitLines.size
    }

    // TODO: This probably isn't necessary. Remove when feeling not lazy :)
    protected open fun recalculateAllVisualLines() {
        visualLines.clear()

        for ((index, textualLine) in textualLines.withIndex()) {
            val splitLines = splitTextForWrapping(textualLine.text, getWidth())
            textualLine.visualIndices = visualLines.size..visualLines.size + splitLines.size
            visualLines.addAll(splitLines.map { VisualLine(it, index) })
        }
    }

    // TODO: Look into optimization of this algorithm
    protected open fun splitTextForWrapping(text: String, maxLineWidth: Float): List<String> {
        val maxLineWidthSpace = maxLineWidth - spaceWidth
        val lineList = mutableListOf<String>()
        val currLine = StringBuilder()
        var currLineWidth = 0f
        var textPos = 0

        fun pushLine(newLineWidth: Float = 0f) {
            lineList.add(currLine.toString())
            currLine.clear()
            currLineWidth = newLineWidth
        }

        while (textPos < text.length) {
            val builder = StringBuilder()

            while (textPos < text.length && text[textPos] != ' ') {
                builder.append(text[textPos])
                textPos++
            }

            val word = builder.toString()
            val wordWidth = word.width().toFloat()

            if (currLineWidth + wordWidth > maxLineWidthSpace) {
                if (wordWidth > maxLineWidthSpace) {
                    // Split up the word into it's own lines
                    if (currLineWidth > 0)
                        pushLine()

                    for (char in word.toCharArray()) {
                        currLineWidth += char.width()
                        if (currLineWidth > maxLineWidthSpace)
                            pushLine(char.width())
                        currLine.append(char)
                    }
                } else {
                    pushLine(wordWidth)
                    currLine.append(word)
                }

                // Check if we have a space, and if so, append it to the new line
                if (textPos < text.length) {
                    if (currLineWidth + spaceWidth > maxLineWidthSpace)
                        pushLine()
                    currLine.append(' ')
                    currLineWidth += spaceWidth
                    textPos++
                }
            } else {
                currLine.append(word)
                currLineWidth += wordWidth

                // Check if we have a space, and if so, append it to a line
                if (textPos < text.length) {
                    textPos++
                    currLine.append(' ')
                    currLineWidth += spaceWidth
                }
            }
        }

        lineList.add(currLine.toString())

        return lineList
    }

    private fun removeText(startPos: LinePosition, endPos: LinePosition) {
        val textualStartPos = startPos.toTextualPos()
        val textualEndPos = endPos.toTextualPos()

        val startTextualLine = textualLines[textualStartPos.line]
        val endTextualLine = textualLines[textualEndPos.line]

        startTextualLine.text = startTextualLine.text.substring(
            0,
            textualStartPos.column
        ) + endTextualLine.text.substring(textualEndPos.column)

        val firstItemToDelete = textualStartPos.line + 1
        repeat(textualEndPos.line - firstItemToDelete + 1) {
            textualLines.removeAt(firstItemToDelete)
        }

        recalculateAllVisualLines()

        val heightDifference = getHeight() - visualLines.size * 9f
        if (verticalScrollingOffset < heightDifference)
            targetVerticalScrollingOffset = heightDifference.coerceAtMost(0f)

        updateAction(getText())
    }

    private fun setCursorPosition(newPosition: LinePosition) {
        newPosition.toVisualPos().run {
            cursor = this
            otherSelectionEnd = this
            cursorNeedsRefocus = true
        }
    }

    protected open fun selectAll() {
        cursor = LinePosition(0, 0, isVisual = true)
        otherSelectionEnd = LinePosition(visualLines.size - 1, visualLines.last().length, isVisual = true)
    }

    protected open fun hasSelection() = cursor != otherSelectionEnd
    protected open fun selectionStart() = minOf(cursor, otherSelectionEnd)
    protected open fun selectionEnd() = maxOf(cursor, otherSelectionEnd)
    protected open fun getSelection() = selectionStart() to selectionEnd()

    protected open fun deleteSelection() {
        if (!hasSelection())
            return

        var startPos = selectionStart()
        removeText(startPos, selectionEnd())
        if (startPos.line > visualLines.lastIndex)
            startPos = LinePosition(startPos.line - 1, visualLines[startPos.line - 1].length, isVisual = true)
        cursor = startPos
        otherSelectionEnd = startPos
        cursorNeedsRefocus = true
    }

    protected open fun copySelection() {
        val (visualSelectionStart, visualSelectionEnd) = getSelection()
        if (visualSelectionStart == visualSelectionEnd)
            return

        val selectionStart = visualSelectionStart.toTextualPos()
        val selectionEnd = visualSelectionEnd.toTextualPos()

        val text = if (selectionStart.line == selectionEnd.line) {
            textualLines[selectionStart.line].text.substring(selectionStart.column, selectionEnd.column)
        } else {
            val lines = mutableListOf<String>()
            lines.add(textualLines[selectionStart.line].text.substring(selectionStart.column))

            for (i in selectionStart.line + 1 until selectionEnd.line)
                lines.add(textualLines[i].text)

            lines.add(textualLines[selectionEnd.line].text.substring(0, selectionEnd.column))
            lines.joinToString("\n")
        }

        val string = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(string, string)
    }

    protected open fun charBefore(pos: LinePosition) = pos.toTextualPos().let {
        when {
            it.isAtAbsoluteStart -> null
            it.isAtLineStart -> '\n'
            else -> textualLines[it.line].text[it.column - 1]
        }
    }

    protected open fun charAfter(pos: LinePosition) = pos.toTextualPos().let {
        when {
            it.isAtAbsoluteEnd -> null
            it.isAtLineEnd -> '\n'
            else -> textualLines[it.line].text[it.column]
        }
    }

    enum class Direction {
        Left,
        Right
    }

    protected open fun isBreakingCharacter(ch: Char): Boolean {
        return !ch.isLetterOrDigit() && ch != '_'
    }

    protected open fun getNearestWordBoundary(pos: LinePosition, direction: Direction): LinePosition {
        /*
         * Algorithm:
         *   1. If we can't go further in the specified direction, return pos
         *   2. First, ignore all breaking characters until a non-breaking character is found
         *      or the beginning is reached
         *   3. Consume until a breaking character is found or the beginning is reached
         *   4. If our direction is left and we are at the end of a visual line, and we are not
         *      at the last line, return the position at the beginning of the next visual line
         *   5. if our direction is right and we are at the beginning of a visual
         *      line, and we are not the first line, return the position at the end of the
         *      last visual line
         *   6. Return the position
         *
         * Other conditions:
         *   - If a newline is encountered, one of the following actions happens:
         *     - If this is the first character, return the position past that newline
         *     - Otherwise, return the position before that newline
         *   - If our direction is left and we are at the end of a visual line, and we are not
         *     at the last line, return the position at the beginning of the next visual line
         *   - If our direction is right and we are at the beginning of a visual line, and we
         *     are not the first line, return the position at the end of the last visual line
         */

        // Step 1
        val atEndOfDirection = if (direction == Direction.Left) pos::isAtAbsoluteStart else pos::isAtAbsoluteEnd
        if (atEndOfDirection())
            return pos

        var textualPos = pos.toTextualPos()
        val columnOffset = if (direction == Direction.Left) -1 else 1
        val nextChar = if (direction == Direction.Left) ::charBefore else ::charAfter

        if (direction == Direction.Left && textualPos.isAtLineStart) {
            val previousLine = textualLines[textualPos.line - 1]
            return LinePosition(textualPos.line - 1, previousLine.length, isVisual = false)
        } else if (direction == Direction.Right && textualPos.isAtLineEnd) {
            return LinePosition(textualPos.line + 1, 0, isVisual = false)
        }

        var ch = nextChar(textualPos)

        // Step 2
        while (!atEndOfDirection() && ch?.let(::isBreakingCharacter) == true) {
            textualPos = textualPos.offsetColumn(columnOffset)
            ch = nextChar(textualPos)
            if (ch == '\n')
                return textualPos
        }

        // Step 3
        while (!atEndOfDirection() && ch?.let(::isBreakingCharacter) == false) {
            textualPos = textualPos.offsetColumn(columnOffset)
            ch = nextChar(textualPos)
            if (ch == '\n')
                return textualPos
        }

        // Note that if we go into either of the if cases below, we will end up returning
        // a visual position rather than a textual position. This is intentional, as a
        // textual position cannot distinguish a visual end of line from a visual start
        // of line (in other words, if you call `.toTextualPos()` on a visual EOL on line 5
        // and on a visual start of line on line 6, they will give you the same position)
        //
        // In order to distinguish this, if either of these are true, we have to return a
        // visual position. Fortunately, because of the way we handle visual vs textual
        // lines, this does not cause a problem
        val visualPos = textualPos.toVisualPos()
        if (direction == Direction.Left && visualPos.isAtLineEnd && !visualPos.isInLastLine) { // Step 4
            textualPos = LinePosition(visualPos.line + 1, 0, isVisual = true)
        } else if (direction == Direction.Right && visualPos.isAtLineStart && !visualPos.isInFirstLine) { // Step 5
            textualPos = LinePosition(visualPos.line - 1, visualLines[visualPos.line - 1].text.length, isVisual = true)
        }

        // Step 6
        return textualPos
    }

    protected open fun animateCursor() {
        if (!active) return

        cursorComponent.animate {
            setColorAnimation(Animations.OUT_CIRCULAR, 0.5f, Color.WHITE.asConstraint())
            onComplete {
                if (!active) return@onComplete
                cursorComponent.animate {
                    setColorAnimation(Animations.IN_CIRCULAR, 0.5f, Color(255, 255, 255, 0).asConstraint())
                    onComplete {
                        if (active) animateCursor()
                    }
                }
            }
        }
    }

    protected open fun hasText() = textualLines.size > 1 || textualLines[0].text.isNotEmpty()

    protected open fun drawUnselectedText(text: String, left: Float, row: Int) {
        UniversalGraphicsHandler.drawString(
            text,
            left - horizontalScrollingOffset,
            getTop() + (9 * row) + verticalScrollingOffset,
            getColor().rgb,
            shadow
        )
    }

    protected open fun drawSelectedText(text: String, left: Float, right: Float, row: Int) {
        UIBlock.drawBlock(
            if (active) selectionBackgroundColor else inactiveSelectionBackgroundColor,
            left.toDouble() - horizontalScrollingOffset,
            getTop().toDouble() + (9 * row) + verticalScrollingOffset,
            right.toDouble() - horizontalScrollingOffset,
            getTop().toDouble() + (9 * (row + 1)) + verticalScrollingOffset
        )
        if (text.isNotEmpty()) {
            UniversalGraphicsHandler.drawString(
                text,
                left - horizontalScrollingOffset,
                getTop() + (9 * row) + verticalScrollingOffset,
                if (active) selectionForegroundColor.rgb else inactiveSelectionForegroundColor.rgb,
                false
            )
        }
    }

    override fun animationFrame() {
        super.animationFrame()

        val diff = (targetVerticalScrollingOffset - verticalScrollingOffset) * 0.1f
        if (abs(diff) < .25f)
            verticalScrollingOffset = targetVerticalScrollingOffset
        verticalScrollingOffset += diff

        recalculateDimensions()

        if (cursorNeedsRefocus) {
            scrollIntoView(cursor)
            cursorNeedsRefocus = false
        }
    }

    protected inner class LinePosition(val line: Int, val column: Int, val isVisual: Boolean) :
        Comparable<LinePosition> {
        val isAtLineStart: Boolean get() = column == 0
        val isAtLineEnd: Boolean get() = column == lines[line].length

        val isInFirstLine: Boolean get() = line == 0
        val isInLastLine: Boolean get() = line == lines.lastIndex

        val isAtAbsoluteStart: Boolean get() = isInFirstLine && isAtLineStart
        val isAtAbsoluteEnd: Boolean get() = isInLastLine && isAtLineEnd

        private val lines: List<Line> = if (isVisual) visualLines else textualLines

        fun offsetColumn(amount: Int) = when {
            amount > 0 -> offsetColumnPositive(amount, this)
            amount < 0 -> offsetColumnNegative(-amount, this)
            else -> this
        }

        private tailrec fun offsetColumnNegative(amount: Int, pos: LinePosition): LinePosition {
            if (amount == 0 || pos.isAtAbsoluteStart)
                return pos

            return offsetColumnNegative(amount - 1, complexOffsetColumnNegative(pos))
        }

        private fun complexOffsetColumnNegative(pos: LinePosition): LinePosition {
            if (!pos.isVisual)
                return simpleOffsetColumnNegative(pos)
            if (!pos.isAtLineStart)
                return simpleOffsetColumnNegative(pos)

            val currentLine = visualLines[pos.line]
            val previousLine = visualLines[pos.line - 1]
            if (currentLine.textIndex != previousLine.textIndex)
                return simpleOffsetColumnNegative(pos)
            if (previousLine.text.last() != ' ')
                return simpleOffsetColumnNegative(pos)
            return LinePosition(pos.line - 1, previousLine.length - 1, isVisual = true)
        }

        private fun simpleOffsetColumnNegative(pos: LinePosition) = if (pos.column == 0) {
            LinePosition(pos.line - 1, pos.lines[pos.line - 1].length, pos.isVisual)
        } else {
            pos.withColumn(pos.column - 1)
        }

        private tailrec fun offsetColumnPositive(amount: Int, pos: LinePosition): LinePosition {
            if (amount == 0 || pos.isAtAbsoluteEnd)
                return pos

            return offsetColumnPositive(amount - 1, complexOffsetColumnPositive(pos))
        }

        private fun complexOffsetColumnPositive(pos: LinePosition): LinePosition {
            if (!pos.isVisual)
                return simpleOffsetColumnPositive(pos)

            val currentLine = visualLines[pos.line]
            if (pos.column < currentLine.length - 1)
                return simpleOffsetColumnPositive(pos)
            if (pos.line == visualLines.lastIndex)
                return LinePosition(pos.line, currentLine.length, isVisual = true)
            if (pos.column == currentLine.length - 1 && currentLine.text.last() != ' ')
                return simpleOffsetColumnPositive(pos)

            val nextLine = visualLines[pos.line + 1]
            if (currentLine.textIndex == nextLine.textIndex)
                return LinePosition(pos.line + 1, 0, isVisual = true)
            return simpleOffsetColumnPositive(pos)
        }

        private fun simpleOffsetColumnPositive(pos: LinePosition) = if (pos.column >= pos.lines[pos.line].length) {
            if (pos.line == pos.lines.lastIndex) {
                LinePosition(pos.lines.lastIndex, pos.lines.last().length, pos.isVisual)
            } else {
                LinePosition(pos.line + 1, 0, pos.isVisual)
            }
        } else {
            pos.withColumn(pos.column + 1)
        }

        fun withColumn(newColumn: Int) = LinePosition(line, newColumn, isVisual)

        fun toTextualPos(): LinePosition {
            if (!isVisual)
                return this

            val visualLine = visualLines[line]
            val textualLine = textualLines[visualLine.textIndex]
            var totalVisualLength = 0

            for (i in textualLine.visualIndices.first until line)
                totalVisualLength += visualLines[i].length

            return LinePosition(visualLine.textIndex, totalVisualLength + column, isVisual = false)
        }

        fun toVisualPos(): LinePosition {
            if (isVisual)
                return this

            val textualLine = textualLines[line]
            var lengthRemaining = column

            for (visualLineIndex in textualLine.visualIndices) {
                val visualLine = visualLines[visualLineIndex]
                if (visualLine.length >= lengthRemaining)
                    return LinePosition(visualLineIndex, lengthRemaining, isVisual = true)

                lengthRemaining -= visualLine.length
            }

            println("toTextualPos: Unexpected end of function")
            return LinePosition(0, 0, isVisual = true)
        }

        fun toScreenPos(): Pair<Float, Float> {
            val visualPos = toVisualPos()
            val x = visualLines[visualPos.line].text.substring(0, visualPos.column).width()
                .toFloat() - horizontalScrollingOffset
            val y = (9f * visualPos.line) + verticalScrollingOffset
            return x to y
        }

        override operator fun compareTo(other: LinePosition): Int {
            val thisVisual = toVisualPos()
            val otherVisual = other.toVisualPos()

            return when {
                thisVisual.line < otherVisual.line -> -1
                thisVisual.line > otherVisual.line -> 1
                thisVisual.column < otherVisual.column -> -1
                thisVisual.column > otherVisual.column -> 1
                else -> 0
            }
        }

        override fun equals(other: Any?) =
            other is LinePosition && line == other.line && column == other.column && isVisual == other.isVisual

        override fun hashCode(): Int {
            var result = line
            result = 31 * result + column
            result = 31 * result + isVisual.hashCode()
            return result
        }

        override fun toString() = "LinePosition(line=$line, column=$column, isVisual=$isVisual)"
    }

    protected open inner class Line(var text: String) {
        val length: Int get() = text.length
    }

    protected inner class TextualLine(text: String, var visualIndices: IntRange = 0..0) : Line(text) {
        fun addTextAt(newText: String, column: Int) {
            if (column >= text.length) {
                text += newText
            } else {
                text = text.substring(0, column) + newText + text.substring(column)
            }
        }

        override fun toString() = "TextualLine(text=$text, visualIndices=$visualIndices)"
    }

    protected inner class VisualLine(text: String, val textIndex: Int) : Line(text) {
        override fun toString() = "VisualLine(text=$text, textIndex=$textIndex)"
    }
}