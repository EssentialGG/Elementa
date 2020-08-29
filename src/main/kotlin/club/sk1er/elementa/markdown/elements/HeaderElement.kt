package club.sk1er.elementa.markdown.elements

class HeaderElement private constructor(
    private val textElement: TextElement,
    private val level: Int
) : Element() {
    companion object {
        fun parse(lines: MutableList<String>): HeaderElement? {
            if (lines.isEmpty())
                return null

            val line = lines.first()
            if (!matches(line))
                return null

            val level = line.takeWhile { it == '#' }.length

            lines.removeAt(0)
            val text = if (level + 1 >= line.length) "" else line.substring(level + 1)
            return HeaderElement(TextElement.parse(text), level)
        }

        fun matches(line: String): Boolean {
            if (line.isEmpty())
                return false

            if (line.first() != '#')
                return false

            val level = line.takeWhile { it == '#' }.length
            if (level > 6)
                return false

            if (level >= line.length || line[level] != ' ')
                return false

            return true
        }
    }
}