package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.elements.*

class Document private constructor(internal val elements: List<Element>) : Element() {
    override fun draw(state: MarkdownState) {
        elements.forEach {
            it.draw(state)
            state.previousElementType = it::class
        }
    }

    override fun onClick(mouseX: Float, mouseY: Float) {
        elements.forEach { it.onClick(mouseX, mouseY) }
    }

    companion object {
        private fun elementFromLines(lines: MutableList<String>, config: MarkdownConfig): Element? {
            val blockquote = BlockquoteElement.parse(lines, config)
            if (blockquote != null)
                return blockquote

            val header = HeaderElement.parse(lines, config)
            if (header != null)
                return header

            val list = ListElement.parse(lines, config)
            if (list != null)
                return list

            val paragraph = ParagraphElement.parse(lines, config)
            if (paragraph != null)
                return paragraph

            val codeblock = CodeblockElement.parse(lines, config)
            if (codeblock != null)
                return codeblock

            return null
        }

        fun fromString(text: String, config: MarkdownConfig): Document? = fromLines(text.lines().toMutableList(), config)

        fun fromLines(lines: MutableList<String>, config: MarkdownConfig): Document? {
            val elements = mutableListOf<Element>()
            var addBreak = false

            // Trim leading empty lines
            while (lines.isNotEmpty() && lines[0].isBlank())
                lines.removeAt(0)

            while (true) {
                if (lines.isEmpty()) {
                    if (elements.last() is BreakElement)
                        elements.removeAt(elements.lastIndex)
                    return Document(elements)
                }

                if (lines[0].isEmpty()) {
                    while (lines.isNotEmpty() && lines[0].isBlank())
                        lines.removeAt(0)
                    if (elements.lastOrNull() is ParagraphElement)
                        addBreak = true
                    continue
                }

                val element = elementFromLines(lines, config) ?: return null

                if (addBreak && element is ParagraphElement)
                    elements.add(BreakElement())
                addBreak = false

                elements.add(element)

            }
        }
    }
}
