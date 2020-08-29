package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.elements.*

class Document private constructor(private val elements: List<Element>) : Element() {
    override fun draw(state: MarkdownState) {
        elements.forEach {
            it.draw(state)
            state.previousElementType = it::class
        }
    }

    companion object {
        fun fromString(text: String): Document? {
            val elements = mutableListOf<Element>()
            val lines = text.lines().toMutableList()

            // Trim leading empty lines
            while (lines.isNotEmpty() && lines[0].isBlank())
                lines.removeAt(0)

            while (true) {
                if (lines.isEmpty())
                    return Document(elements)

                if (lines[0].isEmpty()) {
                    lines.removeAt(0)
                    continue
                }

                val blockquote = BlockquoteElement.parse(lines)
                if (blockquote != null) {
                    elements.add(blockquote)
                    continue
                }

                val header = HeaderElement.parse(lines)
                if (header != null) {
                    elements.add(header)
                    continue
                }

                val list = ListElement.parse(lines)
                if (list != null) {
                    elements.add(list)
                    continue
                }

                val paragraph = ParagraphElement.parse(lines)
                if (paragraph != null) {
                    elements.add(paragraph)
                    continue
                }

                return null
            }
        }
    }
}