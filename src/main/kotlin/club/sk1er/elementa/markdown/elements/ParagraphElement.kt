package club.sk1er.elementa.markdown.elements

class ParagraphElement private constructor(private val textElement: TextElement) : Element() {
    companion object {
        fun parse(lines: MutableList<String>): ParagraphElement? {
            var consumed = false
            val builder = StringBuilder()

            while (true) {
                if (lines.isEmpty())
                    break

                val line = lines[0]
                if (line.isBlank())
                    break

                if (HeaderElement.matches(line))
                    break

                if (ListElement.matches(line))
                    break

                if (line.length >= 3 && line.take(3).all { it == '`' })
                    throw IllegalArgumentException("Code blocks are not yet supported")

                val first = line.take(4)
                if (first.length == 4 && first.all { it == ' ' })
                    throw IllegalArgumentException("Code blocks are not yet supported")

                var isBlockQuote = false
                if (first.isNotEmpty())
                    isBlockQuote = first.contentEquals(">")
                if (!isBlockQuote && first.length > 1)
                    isBlockQuote = first.contentEquals(" >")
                if (!isBlockQuote && first.length > 2)
                    isBlockQuote = first.contentEquals("  >")
                if (!isBlockQuote && first.length > 3)
                    isBlockQuote = first.contentEquals("   >")

                if (isBlockQuote)
                    throw IllegalArgumentException("Blockquotes are not yet supported")

                if (consumed)
                    builder.append('\n')
                builder.append(line)
                lines.removeAt(0)
                consumed = true
            }

            if (!consumed)
                return null

            return ParagraphElement(TextElement.parse(builder.toString()))
        }
    }
}