package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.Document
import club.sk1er.elementa.markdown.MarkdownState

class BlockquoteElement private constructor(private val lines: List<Element>) : Element() {
    override fun onClick(mouseX: Float, mouseY: Float) {
        lines.forEach { it.onClick(mouseX, mouseY) }
    }

    override fun draw(state: MarkdownState) {
        val config = state.blockquoteConfig

        val prevNewlineX = state.newlineX
        state.newlineX += config.spaceBeforeDivider + config.dividerWidth + config.spaceAfterDivider

        state.x = state.newlineX
        state.y += config.spaceBeforeBlockquote

        val dividerYStart = state.y

        lines.forEach {
            it.draw(state)
            if (state.x != state.newlineX)
                state.gotoNextLine()
        }

        UIBlock.drawBlock(
            config.dividerColor,
            state.left.toDouble() + prevNewlineX + config.spaceBeforeDivider,
            state.top.toDouble() + dividerYStart,
            state.left.toDouble() + prevNewlineX + config.spaceBeforeDivider + config.dividerWidth,
            state.top.toDouble() + state.y - (if (lines.last() is BlockquoteElement) config.spaceAfterBlockquote else 0f)
        )

        state.newlineX = prevNewlineX
        state.x = state.newlineX
        state.y += config.spaceAfterBlockquote
    }

    companion object {
        // TODO: Headers must start with a '>' character to be included in the block quote
        fun parse(lines: MutableList<String>): BlockquoteElement? {
            if (lines.isEmpty())
                return null

            if (!matches(lines.first()))
                return null

            val consumedLines = mutableListOf<String>()

            while (true) {
                if (lines.isEmpty())
                    break

                val line = lines.first()
                if (line.isBlank())
                    break

                val index = line.indexOfFirst { it == '>' }
                lines.removeAt(0)
                when {
                    index == -1 -> line
                    index + 1 >= line.length -> ""
                    else -> line.substring(index + 1)
                }.let(consumedLines::add)
            }

            assert(consumedLines.isNotEmpty())

            return BlockquoteElement(Document.fromLines(consumedLines)?.elements ?: return null)
        }

        fun matches(line: String): Boolean {
            return line.startsWith(">") || line.startsWith(" >") || line.startsWith("  >") || line.startsWith("   >")
        }
    }
}