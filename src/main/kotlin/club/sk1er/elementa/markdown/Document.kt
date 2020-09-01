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
        fun elementFromLines(lines: MutableList<String>): Element? {
            val blockquote = BlockquoteElement.parse(lines)
            if (blockquote != null)
                return blockquote

            val header = HeaderElement.parse(lines)
            if (header != null)
                return header

            val list = ListElement.parse(lines)
            if (list != null)
                return list

            val paragraph = ParagraphElement.parse(lines)
            if (paragraph != null)
                return paragraph

            val codeblock = CodeblockElement.parse(lines)
            if (codeblock != null)
                return codeblock

            return null
        }

        fun fromString(text: String): Document? = fromLines(text.lines().toMutableList())

        fun fromLines(lines: MutableList<String>): Document? {
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

                val element = elementFromLines(lines) ?: return null

                if (addBreak && element is ParagraphElement) {
                    elements.add(BreakElement())
                    addBreak = false
                }

                elements.add(element)

            }
        }
    }
}