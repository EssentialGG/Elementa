package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.font.FontRenderer
import club.sk1er.elementa.markdown.MarkdownState
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import java.awt.Color

class TextElement private constructor(internal val spans: List<Span>) : Element() {
    override fun draw(state: MarkdownState) {
        draw(state, state.textConfig.color)
    }

    fun draw(state: MarkdownState, textColor: Color) {
        spans.forEach { span ->
            fun drawString(text: String, x: Float, y: Float, color: Color, shadow: Boolean) {
                if (span.style.code) {
                    codeFontRenderer.drawString(text, x, y - 1.5f, color.rgb, shadow)
                } else {
                    UniversalGraphicsHandler.drawString(text, x, y, color.rgb, shadow)
                }
            }

            fun drawBackground(x1: Float, y1: Float, x2: Float, y2: Float, cutOffBeginning: Boolean = false, cutOffEnd: Boolean = false) {
                if (!span.style.code)
                    return

                state.config.inlineCodeConfig.run {
                    UIRoundedRectangle.drawRoundedRectangle(
                        if (cutOffBeginning) state.left - 10.0 else x1.toDouble() - leftPadding,
                        y1.toDouble() - topPadding,
                        if (cutOffEnd) state.left + state.width + 10.0 else x2.toDouble() + rightPadding,
                        y2.toDouble() + bottomPadding,
                        radius,
                        steps,
                        outlineColor
                    )

                    UIRoundedRectangle.drawRoundedRectangle(
                        if (cutOffBeginning) state.left - 10.0 else x1.toDouble() - leftPadding + outlineWidth,
                        y1.toDouble() - topPadding + outlineWidth,
                        if (cutOffEnd) state.left + state.width + 10.0 else x2.toDouble() + rightPadding - outlineWidth,
                        y2.toDouble() + bottomPadding - outlineWidth,
                        radius,
                        steps,
                        backgroundColor
                    )
                }
            }

            fun textWidth(text: String) = if (span.style.code) {
                codeFontRenderer.getWidth(text) * state.textScaleModifier
            } else text.width(state.textScaleModifier)

            fun textHeight(text: String) = if (span.style.code) {
                codeFontRenderer.getHeight(text) * state.textScaleModifier
            } else 9f * state.textScaleModifier

            var text = span.styledText

            val scale = state.textScaleModifier.toDouble()
            val scaleFloat = scale.toFloat()
            UniversalGraphicsHandler.scale(scale, scale, 1.0)

            val width = textWidth(text)

            if (state.x + width <= state.width) {
                drawBackground(
                    (state.left + state.x) / scaleFloat,
                    (state.top + state.y) / scaleFloat,
                    (state.left + state.x + width) / scaleFloat,
                    (state.top + state.y + textHeight(text)) / scaleFloat
                )

                drawString(
                    text,
                    (state.left + state.x) / scaleFloat,
                    (state.top + state.y) / scaleFloat,
                    textColor,
                    state.textConfig.shadow
                )

                if (span.endsInNewline) {
                    state.gotoNextLine()
                } else {
                    state.x += width
                }
            } else {
                var textIndex = 0
                var first = true

                while (text.isNotEmpty()) {
                    while (textIndex < text.length) {
                        if (textWidth(text.substring(0, textIndex)) + state.x > state.width) {
                            textIndex--
                            break
                        }
                        textIndex++
                    }

                    val textToDraw = text.substring(0, textIndex)
                    val textToDrawWidth = textWidth(textToDraw)
                    val styledText = span.style.applyStyle(textToDraw)
                    text = text.substring(textIndex)

                    drawBackground(
                        (state.left + state.x) / scaleFloat,
                        (state.top + state.y) / scaleFloat,
                        (state.left + state.x + textToDrawWidth) / scaleFloat,
                        (state.top + state.y + textHeight(textToDraw)) / scaleFloat,
                        !first,
                        text.isNotEmpty()
                    )

                    drawString(
                        styledText,
                        (state.left + state.x) / scaleFloat,
                        (state.top + state.y) / scaleFloat,
                        textColor,
                        state.textConfig.shadow
                    )

                    if (text.isNotEmpty()) {
                        state.gotoNextLine()
                    } else {
                        state.x += textToDrawWidth
                    }

                    textIndex = 0

                    first = false
                }

                if (span.endsInNewline)
                    state.gotoNextLine()
            }

            UniversalGraphicsHandler.scale(1f / scale, 1f / scale, 1.0)
        }
    }

    companion object {
        private val codeFontRenderer = FontRenderer(FontRenderer.SupportedFont.Menlo, 18f)

        fun parse(text: String): TextElement {
            val style = Style()
            var spanStart = 0
            val spans = mutableListOf<Span>()

            fun addSpan(index: Int, hasNewline: Boolean = false) {
                if (index == spanStart && !hasNewline)
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
                    if (!style.code) {
                        addSpan(index, true)
                        spanStart = index + 1
                    }

                    index++
                    continue
                }

                if (!isSpecialChar(ch) || (style.code && ch != '`')) {
                    index++
                    continue
                }

                addSpan(index)

                when (ch) {
                    '*', '_' -> if (index + 1 <= text.lastIndex && ch == text[index + 1]) {
                        index++
                        style.bold = !style.bold
                    } else {
                        style.italic = !style.italic
                    }
                    '`' -> {
                        if (style.code) {
                            style.code = false
                        } else if (index + 1 < text.length) {
                            val remainingLines = text.substring(index + 1).split('\n')
                            var lineIndex = 0

                            while (lineIndex < remainingLines.size) {
                                val line = remainingLines[lineIndex]
                                if (line.isBlank())
                                    break
                                lineIndex++
                            }

                            style.code = remainingLines.subList(0, lineIndex).any { '`' in it }
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

        private fun isSpecialChar(ch: Char) = ch == '*' || ch == '_' || ch == '`'
    }

    internal data class Style(
        var italic: Boolean = false,
        var bold: Boolean = false,
        var code: Boolean = false
    ) {
        fun applyStyle(text: String) = (if (bold) "§l" else "") + (if (italic) "§o" else "") + text
    }

    internal class Span(
        text: String,
        val style: Style,
        var endsInNewline: Boolean = false
    ) {
        val styledText: String
            get() = style.applyStyle(text)

        var text: String = text
            private set

        init {
            if (style.code)
                this.text = text.replace("\n", "")
        }
    }
}