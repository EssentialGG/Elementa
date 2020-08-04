package club.sk1er.elementa.components.input

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.HeightConstraint
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

class UIMultilineTextInput @JvmOverloads constructor(
    private val placeholder: String = "",
    var shadow: Boolean = true,
    private val selectionBackgroundColor: Color = Color.WHITE,
    private val selectionForegroundColor: Color = Color(64, 139, 229),
    private val allowInactiveSelection: Boolean = false,
    private val inactiveSelectionBackgroundColor: Color = Color(176, 176, 176),
    private val inactiveSelectionForegroundColor: Color = Color.WHITE
) : UIComponent() {
    private var maxHeight: HeightConstraint? = null

    private var updateAction: (text: String) -> Unit = {}
    private var activateAction: (text: String) -> Unit = {}

    private val textualLines = mutableListOf(TextualLine("", 0..0))
    private val visualLines = mutableListOf(VisualLine("", 0))
    private var active = false

    private var cursorComponent: UIComponent = UIBlock(
        Color(
            255,
            255,
            255,
            0
        )
    ).constrain {
        y = CenterConstraint() - 0.5f.pixels()
        width = 1.pixels()
        height = 9f.pixels()
    } childOf this

    private var cursor = LinePosition(0, 0, isVisual = true)
        set(value) {
            field = value.toVisualPos()
        }

    private var otherSelectionEnd = LinePosition(0, 0, isVisual = true)
        set(value) {
            field = value.toVisualPos()
        }

    private var selectionMode = SelectionMode.None
    private var initiallySelectedLine = -1
    private var initiallySelectedWord = LinePosition(0, 0, true) to LinePosition(0, 0, true)

    private var scrollingOffset = 0f
    private var targetScrollingOffset = 0f
    private var cursorNeedsRefocus = false
    private var lastSelectionMoveTimestamp = System.currentTimeMillis()

    private val spaceWidth = ' '.width()

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
                    holdingCtrl -> getNearestWordBoundary(cursor,
                        Direction.Left
                    )
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
                    holdingCtrl -> getNearestWordBoundary(cursor,
                        Direction.Right
                    )
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
                if (UniversalKeyboard.isShiftKeyDown()) {
                    val textualPos = cursor.toTextualPos()
                    val textualLine = textualLines[textualPos.line]
                    if (textualPos.column == textualLine.length) {
                        textualLines.add(textualPos.line + 1, TextualLine(""))
                    } else {
                        val newLineText = textualLine.text.substring(textualPos.column)
                        textualLines.add(textualPos.line + 1, TextualLine(newLineText))
                        textualLine.text = textualLine.text.substring(0, textualPos.column)
                    }

                    setCursorPosition(LinePosition(cursor.line + 1, 0, isVisual = true))
                    recalculateAllVisualLines()
                    updateAction(getText())
                } else {
                    activateAction(getText())
                }
            }
        }

        onMouseScroll { delta ->
            val heightDifference = getHeight() - visualLines.size * 9f
            if (heightDifference > 0)
                return@onMouseScroll
            targetScrollingOffset = (targetScrollingOffset + delta * 9f).coerceIn(heightDifference, 0f)
        }

        onMouseClick { event ->
            if (!active || event.mouseButton != 0)
                return@onMouseClick

            val clickedVisualPos = screenPosToVisualPos(event.relativeX, event.relativeY)

            when (event.clickCount % 3) {
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
                    cursor = getNearestWordBoundary(clickedVisualPos,
                        Direction.Left
                    )
                    cursorNeedsRefocus = true
                    otherSelectionEnd = getNearestWordBoundary(clickedVisualPos,
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
                    otherSelectionEnd = LinePosition(initiallySelectedLine, visualLines[initiallySelectedLine].length, isVisual = true)
                }
                SelectionMode.Word -> when {
                    draggedVisualPos < initiallySelectedWord.first -> {
                        cursor = getNearestWordBoundary(draggedVisualPos,
                            Direction.Left
                        )
                        otherSelectionEnd = initiallySelectedWord.second
                    }
                    draggedVisualPos > initiallySelectedWord.second -> {
                        cursor = initiallySelectedWord.first
                        otherSelectionEnd = getNearestWordBoundary(draggedVisualPos,
                            Direction.Right
                        )
                    }
                    else -> {
                        cursor = initiallySelectedWord.first
                        otherSelectionEnd = initiallySelectedWord.second
                    }
                }
                SelectionMode.None -> {}
            }

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSelectionMoveTimestamp > 50) {
                if (mouseY < 0) {
                    targetScrollingOffset = (targetScrollingOffset + 9).coerceAtMost(0f)
                    lastSelectionMoveTimestamp = currentTime
                } else if (mouseY > getHeight()) {
                    val heightDifference = getHeight() - visualLines.size * 9f
                    targetScrollingOffset = (targetScrollingOffset - 9).coerceIn(heightDifference, 0f)
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

    fun getText() = textualLines.joinToString("\n") { it.text }

    fun setMaxHeight(maxHeight: HeightConstraint) = apply {
        this.maxHeight = maxHeight
    }

    fun setMaxLines(maxLines: Int) = apply {
        this.maxHeight = (9 * maxLines).pixels()
    }

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

    private fun addText(newText: String) {
        if (hasSelection()) {
            deleteSelection()
            addText(newText)
            return
        }

        val textPos = cursor.toTextualPos()
        val textualLine = textualLines[textPos.line]
        if (newText.contains('\n')) {
            val lines = newText.split('\n')
            textualLine.addTextAt(lines[0], textPos.column)
            textualLines.addAll(textPos.line + 1, lines.drop(1).map { TextualLine(it) })
        } else {
            textualLine.addTextAt(newText, textPos.column)
        }
        recalculateAllVisualLines()
        setCursorPosition(textPos.offsetColumn(newText.length).toVisualPos())

        updateAction(getText())
    }

    private fun recalculateVisualLinesFor(textualLineIndex: Int) {
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
    private fun recalculateAllVisualLines() {
        visualLines.clear()

        for ((index, textualLine) in textualLines.withIndex()) {
            val splitLines = splitTextForWrapping(textualLine.text, getWidth())
            textualLine.visualIndices = visualLines.size..visualLines.size + splitLines.size
            visualLines.addAll(splitLines.map { VisualLine(it, index) })
        }
    }

    // TODO: Look into optimization of this algorithm
    private fun splitTextForWrapping(text: String, maxLineWidth: Float): List<String> {
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

        startTextualLine.text = startTextualLine.text.substring(0, textualStartPos.column) + endTextualLine.text.substring(textualEndPos.column)

        val firstItemToDelete = textualStartPos.line + 1
        repeat(textualEndPos.line - firstItemToDelete + 1) {
            textualLines.removeAt(firstItemToDelete)
        }

        recalculateAllVisualLines()

        updateAction(getText())
    }

    private fun setCursorPosition(newPosition: LinePosition) {
        newPosition.toVisualPos().run {
            cursor = this
            otherSelectionEnd = this
            cursorNeedsRefocus = true
        }
    }

    private fun selectAll() {
        cursor = LinePosition(0, 0, isVisual = true)
        otherSelectionEnd = LinePosition(visualLines.size - 1, visualLines.last().length, isVisual = true)
    }

    private fun hasSelection() = cursor != otherSelectionEnd
    private fun selectionStart() = minOf(cursor, otherSelectionEnd)
    private fun selectionEnd() = maxOf(cursor, otherSelectionEnd)
    private fun getSelection() = selectionStart() to selectionEnd()

    private fun deleteSelection() {
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

    private fun copySelection() {
        val (selectionStart, selectionEnd) = getSelection()

        val text = if (selectionStart == selectionEnd) {
            visualLines[selectionStart.line].text.substring(selectionStart.column, selectionEnd.column)
        } else {
            val lines = mutableListOf<String>()

            lines.add(visualLines[selectionStart.line].text.substring(selectionStart.column))

            for (i in selectionStart.line + 1 until selectionEnd.line)
                lines.add(visualLines[i].text)

            lines.add(visualLines[selectionEnd.line].text.substring(0, selectionEnd.column))
            lines.joinToString("\n")
        }

        val string = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(string, string)
    }

    private fun charBefore(pos: LinePosition) = pos.toTextualPos().let {
        when {
            it.isAtAbsoluteStart -> null
            it.isAtLineStart -> '\n'
            else -> textualLines[it.line].text[it.column - 1]
        }
    }

    private fun charAfter(pos: LinePosition) = pos.toTextualPos().let {
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

    private fun isBreakingCharacter(ch: Char): Boolean {
        return !ch.isLetterOrDigit() && ch != '_'
    }

    private fun getNearestWordBoundary(pos: LinePosition, direction: Direction): LinePosition {
        /*
         * Algorithm:
         *   1. If at beginning, return pos
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
        if (pos.isAtAbsoluteStart)
            return pos

        var textualPos = pos.toTextualPos()
        val columnOffset = if (direction == Direction.Left) -1 else 1
        val nextChar = if (direction == Direction.Left) ::charBefore else ::charAfter

        var ch = nextChar(textualPos)
        if (ch == '\n')
            return textualPos.offsetColumn(columnOffset)

        // Step 2
        while (!textualPos.isAtAbsoluteStart && ch?.let(::isBreakingCharacter) == true) {
            textualPos = textualPos.offsetColumn(columnOffset)
            ch = nextChar(textualPos)
            if (ch == '\n')
                return textualPos
        }

        // Step 3
        while (!textualPos.isAtAbsoluteStart && ch?.let(::isBreakingCharacter) == false) {
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

    private fun scrollIntoView(pos: LinePosition) {
        val visualPos = pos.toVisualPos()

        val visualLineOffset = visualPos.line * -9f

        if (targetScrollingOffset < visualLineOffset) {
            targetScrollingOffset = visualLineOffset
        } else if (visualLineOffset - 9f < targetScrollingOffset - getHeight()) {
            targetScrollingOffset += visualLineOffset - 9f - (targetScrollingOffset - getHeight())
        }
    }

    private fun recalculateHeight() {
        if (maxHeight == null)
            return

        setHeight((9 * visualLines.size).pixels().max(maxHeight!!))
    }

    private fun animateCursor() {
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

    private fun hasText() = textualLines.size > 1 || textualLines[0].text.isNotEmpty()

    private fun drawUnselectedText(text: String, left: Float, row: Int) {
        UniversalGraphicsHandler.drawString(
            text,
            left,
            getTop() + (9 * row) + scrollingOffset,
            getColor().rgb,
            shadow
        )
    }

    private fun drawSelectedText(text: String, left: Float, right: Float, row: Int) {
        UIBlock.drawBlock(
            if (active) selectionBackgroundColor else inactiveSelectionBackgroundColor,
            left.toDouble(),
            getTop().toDouble() + (9 * row) + scrollingOffset,
            right.toDouble(),
            getTop().toDouble() + (9 * (row + 1)) + scrollingOffset
        )
        if (text.isNotEmpty()) {
            UniversalGraphicsHandler.drawString(
                text,
                left,
                getTop() + (9 * row) + scrollingOffset,
                if (active) selectionForegroundColor.rgb else inactiveSelectionForegroundColor.rgb,
                false
            )
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
            val topOffset = (9 * i) + scrollingOffset
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
                    selectionStart.line == selectionEnd.line -> visualLine.text.substring(selectionStart.column, selectionEnd.column)
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

    override fun animationFrame() {
        super.animationFrame()

        val diff = (targetScrollingOffset - scrollingOffset) * 0.1f
        if (abs(diff) < .25f)
            scrollingOffset = targetScrollingOffset
        scrollingOffset += diff

        recalculateHeight()

        if (cursorNeedsRefocus) {
            scrollIntoView(cursor)
            cursorNeedsRefocus = false
        }
    }

    private fun screenPosToVisualPos(x: Float, yTemp: Float): LinePosition {
        val y = yTemp - scrollingOffset

        if (y <= 0)
            return LinePosition(0, 0, isVisual = true)

        val line = (y / 9).toInt()
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

    private inner class LinePosition(val line: Int, val column: Int, val isVisual: Boolean) : Comparable<LinePosition> {
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

            val nextPosition = if (pos.column == 0) {
                LinePosition(pos.line - 1, pos.lines[pos.line - 1].length - 1, pos.isVisual)
            } else {
                pos.withColumn(pos.column - 1)
            }

            return offsetColumnNegative(amount - 1, nextPosition)
        }

        private tailrec fun offsetColumnPositive(amount: Int, pos: LinePosition): LinePosition {
            if (amount == 0 || pos.isAtAbsoluteEnd)
                return pos

            // There are two states we want to handle here:
            //   - The more common case where the user is one position before the end of the
            //     line and pressed the right arrow key. In this case, we want to skip the
            //     ending character by going to the next line
            //   - The less common case where the user is at the absolute end of the line and
            //     pressed the right arrow key. This is only achievable by pressing the end key.
            //     We would want to return the position on the next line with a column of 1
            val nextPosition = if (pos.column >= pos.lines[pos.line].length - 1) {
                if (pos.line == pos.lines.lastIndex) {
                    return LinePosition(pos.lines.lastIndex, pos.lines.last().length, pos.isVisual)
                } else {
                    LinePosition(pos.line + 1, if (pos.column == pos.lines[pos.line].length) 1 else 0, pos.isVisual)
                }
            } else {
                pos.withColumn(pos.column + 1)
            }

            return offsetColumnPositive(amount - 1, nextPosition)
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
            val x = visualLines[visualPos.line].text.substring(0, visualPos.column).width().toFloat()
            val y = (9f * visualPos.line) + scrollingOffset
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

        override fun equals(other: Any?) = other is LinePosition && line == other.line && column == other.column && isVisual == other.isVisual

        override fun hashCode(): Int {
            var result = line
            result = 31 * result + column
            result = 31 * result + isVisual.hashCode()
            return result
        }

        override fun toString() = "LinePosition(line=$line, column=$column, isVisual=$isVisual)"
    }

    private open inner class Line(var text: String) {
        val length: Int get() = text.length
    }

    private inner class TextualLine(text: String, var visualIndices: IntRange = 0..0) : Line(text) {
        fun addTextAt(newText: String, column: Int) {
            if (column >= text.length) {
                text += newText
            } else {
                text = text.substring(0, column) + newText + text.substring(column)
            }
        }

        override fun toString() = "TextualLine(text=$text, visualIndices=$visualIndices)"
    }

    private inner class VisualLine(text: String, val textIndex: Int) : Line(text) {
        override fun toString() = "VisualLine(text=$text, textIndex=$textIndex)"
    }
}