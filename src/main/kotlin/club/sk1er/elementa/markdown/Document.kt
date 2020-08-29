package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.elements.*

class Document private constructor(internal val elements: List<Element>) : Element() {
    override fun draw(state: MarkdownState) {
        elements.forEach {
            it.draw(state)
            state.previousElementType = it::class
        }
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

            return null
        }

        fun fromString(text: String): Document? = fromLines(text.lines().toMutableList())

        fun fromLines(lines: MutableList<String>): Document? {
            val elements = mutableListOf<Element>()

            // Trim leading empty lines
            while (lines.isNotEmpty() && lines[0].isBlank())
                lines.removeAt(0)

            while (true) {
                if (lines.isEmpty())
                    return Document(elements)

                // TODO: Multiple blank lines should result in one visually blank line
                if (lines[0].isEmpty()) {
                    lines.removeAt(0)
                    continue
                }

                elements.add(elementFromLines(lines) ?: return null)
            }
        }
    }
}