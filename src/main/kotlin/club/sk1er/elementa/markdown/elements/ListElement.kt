package club.sk1er.elementa.markdown.elements

class ListElement private constructor(
    private val items: List<ListItem>,
    private val isOrdered: Boolean
) : Element() {
    companion object {
        fun parse(lines: MutableList<String>): ListElement? {
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
                val level = line.takeWhile { it == ' ' }.length / 2

                lines.removeAt(0)
                consumed = true
                items.add(ListItem(
                    if (index + 2 >= line.length) "" else line.substring(index + 2),
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
        val text: String,
        val level: Int
    )
}
