package club.sk1er.elementa.markdown

import club.sk1er.elementa.markdown.elements.Element
import club.sk1er.elementa.markdown.elements.HeaderElement
import club.sk1er.elementa.markdown.elements.ListElement
import club.sk1er.elementa.markdown.elements.ParagraphElement

class Document private constructor(private val elements: List<Element>) {
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