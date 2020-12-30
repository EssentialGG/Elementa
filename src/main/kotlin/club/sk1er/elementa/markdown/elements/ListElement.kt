package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.elementa.markdown.MarkdownState
import club.sk1er.mods.core.universal.UGraphics

class ListElement private constructor(
    private val items: List<ListItem>,
    private val isOrdered: Boolean
) : Element() {
    override fun onClick(mouseX: Float, mouseY: Float) {
        items.forEach { it.text.onClick(mouseX, mouseY) }
    }

    override fun draw(state: MarkdownState) {
        state.gotoNextLine()
        state.y += state.listConfig.elementSpacing + state.listConfig.spaceBeforeList


        items.forEach {
            val indent = it.level * state.listConfig.indentation
            if (indent > state.width) {
                // TODO: Horizontal scrolling?
                return@forEach
            }

            state.x += indent
            val glyph = glyphForLevel(it.level)

            UGraphics.drawString(
                glyphForLevel(it.level),
                state.left + state.x,
                state.top + state.y,
                state.listConfig.fontColor.rgb,
                state.textConfig.shadow
            )

            state.x += glyph.width() + state.listConfig.spaceBeforeText
            if (state.x > state.width) {
                state.gotoNextLine()
                state.y += state.listConfig.elementSpacing
            }

            it.text.draw(state, state.listConfig.fontColor)

            state.gotoNextLine()
            state.y += state.listConfig.elementSpacing
        }

        state.y += state.listConfig.spaceAfterList
    }

    private fun glyphForLevel(level: Int) = when (level) {
        0 -> "●"
        1 -> "○"
        2 -> "■"
        3 -> "□"
        4 -> "▶"
        else -> "▷"
    }

    companion object {
        fun parse(lines: MutableList<String>, config: MarkdownConfig): ListElement? {
            if (!config.listConfig.enabled)
                return null

            var consumed = false
            val items = mutableListOf<ListItem>()

            while (true) {
                if (lines.isEmpty())
                    break

                val line = lines[0]
                if (line.isBlank())
                    break

                if (!matches(line))
                    break

                val index = line.indexOfFirst { it == '*' || it == '-' }
                val level = line.takeWhile { it == ' ' }.length / 4

                lines.removeAt(0)
                consumed = true
                items.add(ListItem(
                    TextElement.parse(if (index + 2 >= line.length) "" else line.substring(index + 2), config),
                    level
                ))
            }

            if (!consumed)
                return null

            return ListElement(items, false)
        }

        // This does not consider a four-space indent to be a code block
        fun matches(line: String): Boolean {
            if (line.isBlank())
                return false

            val firstCharIndex = line.indexOfFirst { it != ' ' }
            assert(firstCharIndex != -1)
            val firstChar = line[firstCharIndex]

            if (firstChar == '*' || firstChar == '-')
                return firstCharIndex + 1 < line.length && line[firstCharIndex + 1] == ' '

            if (!firstChar.isDigit())
                return false

            val dotIndex = line.indexOf('.')
            if (dotIndex == -1)
                return false

            if (line.substring(firstCharIndex, dotIndex).toIntOrNull() == null)
                return false

            return dotIndex + 1 < line.length && line[dotIndex + 1] == ' '
        }
    }

    private data class ListItem(
        val text: TextElement,
        val level: Int
    )
}
