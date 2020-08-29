package club.sk1er.elementa.markdown.elements

class TextElement private constructor(private val spans: List<Span>) {
    companion object {
        fun parse(text: String): TextElement {
            val style = Style()
            var spanStart = 0
            val spans = mutableListOf<Span>()

            fun addSpan(index: Int) {
                if (index == spanStart)
                    return
                spans.add(Span(text.substring(spanStart, index), style.copy()))
            }

            var index = 0
            while (index < text.length) {
                val ch = text[index]

                if (ch == '\\' && index != text.lastIndex) {
                    index++
                    continue
                }

                if (!isSpecialChar(ch)) {
                    index++
                    continue
                }

                addSpan(index)

                when (ch) {
                    '*', '_' -> {
                        if (index + 1 <= text.lastIndex && ch == text[index + 1]) {
                            index++
                            style.bold = !style.bold
                        } else {
                            style.italic = !style.italic
                        }
                    }
                    else -> throw IllegalStateException()
                }

                index++
                spanStart = index
            }

            addSpan(text.length)

            return TextElement(spans)
        }

        private fun isSpecialChar(ch: Char) = ch == '*' || ch == '_'
    }

    private data class Style(
        var italic: Boolean = false,
        var bold: Boolean = false
    )

    private data class Span(
        val text: String,
        val style: Style
    )
}