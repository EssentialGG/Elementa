package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.MarkdownState
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import java.awt.Color

class TextElement private constructor(internal val spans: List<Span>) : Element() {
    override fun draw(state: MarkdownState) {
        draw(state, state.textConfig.color)
    }

    fun draw(state: MarkdownState, textColor: Color) {
        spans.forEach { span ->
            var text = span.styledText

            val scale = state.textScaleModifier.toDouble()
            val scaleFloat = scale.toFloat()
            UniversalGraphicsHandler.scale(scale, scale, 1.0)

            val width = text.width(state.textScaleModifier)
            if (state.x + width <= state.width) {
                UniversalGraphicsHandler.drawString(
                    text,
                    (state.left + state.x) / scaleFloat,
                    (state.top + state.y) / scaleFloat,
                    textColor.rgb,
                    state.textConfig.shadow
                )

                if (span.endsInNewline) {
                    state.x = 0f
                    state.y += (9f + state.textConfig.spaceBetweenLines) * state.textScaleModifier
                } else {
                    state.x += width
                }
            } else {
                var textIndex = 0

                while (text.isNotEmpty()) {
                    while (textIndex < text.length) {
                        if (text.substring(0, textIndex).width(state.textScaleModifier) + state.x > state.width) {
                            textIndex--
                            break
                        }
                        textIndex++
                    }

                    val textToDraw = span.style.applyStyle(text.substring(0, textIndex))

                    UniversalGraphicsHandler.drawString(
                        textToDraw,
                        (state.left + state.x) / scaleFloat,
                        (state.top + state.y) / scaleFloat,
                        textColor.rgb,
                        state.textConfig.shadow
                    )

                    text = text.substring(textIndex)

                    if (text.isNotEmpty()) {
                        state.x = 0f
                        state.y += (9f + state.textConfig.spaceBetweenLines) * state.textScaleModifier
                    } else {
                        state.x += width
                    }

                    textIndex = 0
                }

                if (span.endsInNewline) {
                    state.x = 0f
                    state.y += (9f + state.textConfig.spaceBetweenLines) * state.textScaleModifier
                }
            }

            UniversalGraphicsHandler.scale(1f / scale, 1f / scale, 1.0)
        }
    }

    companion object {
        fun parse(text: String): TextElement {
            val style = Style()
            var spanStart = 0
            val spans = mutableListOf<Span>()

            fun addSpan(index: Int, hasNewline: Boolean = false) {
                if (index == spanStart)
                    return
                spans.add(Span(text.substring(spanStart, index), style.copy(), hasNewline))
            }

            var index = 0
            while (index < text.length) {
                val ch = text[index]

                if (ch == '\\' && index != text.lastIndex) {
                    index++
                    continue
                }

                if (ch == '\n') {
                    addSpan(index, true)
                    index++
                    spanStart = index
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

    internal data class Style(
        var italic: Boolean = false,
        var bold: Boolean = false
    ) {
        fun applyStyle(text: String) = (if (bold) "§l" else "") + (if (italic) "§o" else "") + text
    }

    internal data class Span(
        val text: String,
        val style: Style,
        var endsInNewline: Boolean = false
    ) {
        val styledText: String
            get() = style.applyStyle(text)
    }
}