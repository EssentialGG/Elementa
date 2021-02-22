package club.sk1er.elementa.markdown.cursor

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.drawables.ParagraphDrawable
import club.sk1er.elementa.markdown.drawables.TextDrawable
import java.awt.Color
import kotlin.math.abs

class ParagraphCursor(override val target: ParagraphDrawable) : DrawableCursor() {
    private var currentText = target.textDrawables.first()
    private var stringOffset = 0

    private var cursorX = 0f
    private var cursorY = 0f
    private var cursorWidth = 0f
    private var cursorHeight = 0f

    override fun moveToStart() {
        currentText = target.textDrawables.first()
        stringOffset = currentText.styleChars()
        cursorX = currentText.x
        cursorY = currentText.y
        cursorHeight = currentText.height
        cursorWidth = cursorHeight / 9f
    }

    override fun moveToEnd() {
        currentText = target.textDrawables.last()
        stringOffset = currentText.formattedText.length
        cursorX = currentText.x
        cursorY = currentText.y
        cursorHeight = currentText.height
        cursorWidth = cursorHeight / 9f
    }

    override fun moveTo(mouseX: Float, mouseY: Float) {
        // Account for padding between lines
        // TODO: Don't account for this padding for the first and last lines?
        val linePadding = target.config.paragraphConfig.spaceBetweenLines / 2f

        // Step 1: Get to the correct row

        var currentHeightRange = (currentText.y - linePadding)..(currentText.y + currentText.height + linePadding)

        if (mouseY !in currentHeightRange) {
            if (mouseY < currentHeightRange.start) {
                while (currentText.previous != null && mouseY < currentText.y - linePadding)
                    selectPreviousText()

                if (mouseY < currentText.y - linePadding) {
                    // The mouse in in between text components, in which case this cursor
                    // can't properly position
                    return
                }
            } else {
                while (currentText.next != null && mouseY > currentText.y + currentText.height + linePadding)
                    selectNextText()

                if (mouseY > currentText.y + currentText.height + linePadding) {
                    // The mouse in in between text components, in which case this cursor
                    // can't properly position
                    return
                }
            }

            currentHeightRange = (currentText.y - linePadding)..(currentText.y + currentText.height + linePadding)
        }

        // Step 2: Get to the correct text drawable
        var positionInTextComponent = true

        if (mouseX < currentText.x) {
            while (currentText.previous != null && mouseX < currentText.x) {
                // If the previous text component is at the end of the previous line, then
                // we just select the beginning of the current component
                if (currentText.previous!!.y !in currentHeightRange) {
                    positionInTextComponent = false
                    stringOffset = currentText.styleChars()
                } else {
                    selectPreviousText()
                }
            }
        } else if (mouseX > currentText.x + currentText.width()) {
            while (currentText.next != null && mouseX > currentText.x + currentText.width()) {
                // If the previous text component is at the beginning of the the next line,
                // then we just select the end of the current component
                if (currentText.next!!.y !in currentHeightRange) {
                    positionInTextComponent = false
                    stringOffset = currentText.formattedText.length
                } else {
                    selectNextText()
                }
            }
        }

        // Step 3: Get the string offset position in the current text

        if (positionInTextComponent) {
            stringOffset = currentText.styleChars()
            var cachedWidth = 0f

            while (stringOffset < currentText.formattedText.lastIndex) {
                stringOffset++
                val newWidth = currentText.formattedText.substring(0, stringOffset).width(currentText.scaleModifier)
                if (currentText.x + newWidth > mouseX) {
                    // Chose the closer side
                    val newDiff = abs(currentText.x + newWidth - mouseX)
                    val oldDiff = abs(currentText.x + cachedWidth - mouseX)

                    if (newDiff < oldDiff) {
                        cachedWidth = newWidth
                    } else {
                        stringOffset--
                    }
                    break
                }

                cachedWidth = newWidth
            }

            cursorX = currentText.x + cachedWidth
        } else {
            cursorX = currentText.x + currentText.formattedText.substring(0, stringOffset).width(currentText.scaleModifier)
        }

        cursorY = currentText.y
        cursorHeight = currentText.height
        cursorWidth = cursorHeight / 9f
    }

    private fun selectPreviousText() {
        if (currentText.previous == null)
            TODO()

        currentText = currentText.previous as TextDrawable
        stringOffset = currentText.formattedText.length
        cursorX = currentText.x + currentText.width()
        cursorY = currentText.y
    }

    private fun selectNextText() {
        if (currentText.next == null)
            TODO()

        currentText = currentText.next as TextDrawable
        stringOffset = currentText.styleChars()
        cursorX = currentText.x
        cursorY = currentText.y
    }

    override fun draw(state: DrawState) {
        UIBlock.drawBlockSized(
            Color.RED,
            (cursorX + state.xShift).toDouble(),
            (cursorY + state.yShift).toDouble(),
            cursorWidth.toDouble(),
            cursorHeight.toDouble()
        )
    }
}
