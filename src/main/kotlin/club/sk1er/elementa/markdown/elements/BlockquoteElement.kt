package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.MarkdownState

// TODO: This can contain pretty much any other element (code blocks, headers, etc), not just TextElements
class BlockquoteElement private constructor(private val lines: List<TextElement>) : Element() {
    override fun draw(state: MarkdownState) {
        val config = state.blockquoteConfig

        val prevNewlineX = state.newlineX
        state.newlineX += config.spaceBeforeDivider + config.dividerWidth + config.spaceAfterDivider

        state.x = state.newlineX
        state.y += config.spaceBeforeBlockquote

        UIBlock.drawBlock(
            config.dividerColor,
            state.left.toDouble() + config.spaceBeforeDivider,
            state.top.toDouble() + state.y,
            state.left.toDouble() + config.spaceBeforeDivider + config.dividerWidth,
            state.top.toDouble() + state.y + lines.size * 9f + (lines.size - 1) * config.spaceBetweenLines
        )

        lines.forEach {
            it.draw(state, config.fontColor)
            state.gotoNextLine()
        }

        state.y += config.spaceAfterBlockquote
        state.newlineX = prevNewlineX
    }

    companion object {
        fun parse(lines: MutableList<String>): BlockquoteElement? {
            if (lines.isEmpty())
                return null

            if (!matches(lines.first()))
                return null

            val textElements = mutableListOf<TextElement>()

            while (true) {
                val line = lines.first()
                if (line.isBlank())
                    break

                val index = line.indexOfFirst { it == '>' }
                lines.removeAt(0)
                val text = when {
                    index == -1 -> line
                    index + 1 >= line.length -> ""
                    else -> line.substring(index + 1)
                }
                textElements.add(TextElement.parse(text.trimStart()))
            }

            if (textElements.isEmpty())
                return null

            return BlockquoteElement(textElements)
        }

        private fun matches(line: String): Boolean {
            return line.startsWith(">") || line.startsWith(" >") || line.startsWith("  >") || line.startsWith("   >")
        }
    }
}