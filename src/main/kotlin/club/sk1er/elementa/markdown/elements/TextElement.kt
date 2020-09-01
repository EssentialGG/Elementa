package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.MarkdownState
import club.sk1er.mods.core.universal.UniversalDesktop
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalMouse
import club.sk1er.mods.core.universal.UniversalResolutionUtil
import java.awt.Color
import java.net.URI

class TextElement internal constructor(internal val spans: List<Span>) : Element() {
    internal var partialTexts: List<Pair<Span, List<PartialText>>>? = null

    override fun draw(state: MarkdownState) {
        draw(state, state.textConfig.color)
    }

    internal data class Rectangle(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    )

    internal data class PartialText(
        val text: String,
        val bounds: Rectangle,
        val cutOffBeginning: Boolean,
        val cutOffEnd: Boolean
    )

    override fun onClick(mouseX: Float, mouseY: Float) {
        val clicked = partialTexts!!
            .filter { it.first.style.isURL }
            .firstOrNull { (_, partialTexts) ->
                partialTexts.any { (_, bounds) ->
                    mouseX > bounds.x1 && mouseX < bounds.x2 && mouseY > bounds.y1 && mouseY < bounds.y2
                }
            }

        if (clicked != null)
            UniversalDesktop.browse(URI(clicked.first.style.url!!))
    }

    internal fun calculatePartialTexts(state: MarkdownState) {
        partialTexts = spans.map { span ->
            fun textWidth(text: String) = if (span.style.code) {
                CodeblockElement.codeFontRenderer.getWidth(text) * state.textScaleModifier
            } else text.width(state.textScaleModifier)

            fun textHeight(text: String) = if (span.style.code) {
                CodeblockElement.codeFontRenderer.getHeight(text) * state.textScaleModifier
            } else 9f * state.textScaleModifier

            var text = span.styledText

            val scale = state.textScaleModifier

            val width = textWidth(text)
            val partialTexts = mutableListOf<PartialText>()

            if (state.x + width <= state.width) {
                partialTexts.add(PartialText(
                    text,
                    Rectangle(
                        (state.left + state.x) / scale,
                        (state.top + state.y) / scale,
                        (state.left + state.x + width) / scale,
                        (state.top + state.y + textHeight(text)) / scale
                    ),
                    cutOffBeginning = false,
                    cutOffEnd = false
                ))

                if (span.endsInNewline) {
                    state.gotoNextLine()
                } else {
                    state.x += width
                }
            } else {
                var textIndex = text.length
                var first = true

                while (text.isNotEmpty()) {
                    while (textIndex > 0) {
                        if (textWidth(text.substring(0, textIndex)) + state.x <= state.width)
                            break
                        textIndex--
                    }

                    val textToDraw = text.substring(0, textIndex)
                    val textToDrawWidth = textWidth(textToDraw)
                    text = text.substring(textIndex).trimStart()

                    partialTexts.add(PartialText(
                        textToDraw,
                        Rectangle(
                            (state.left + state.x) / scale,
                            (state.top + state.y) / scale,
                            (state.left + state.x + textToDrawWidth) / scale,
                            (state.top + state.y + textHeight(textToDraw)) / scale
                        ),
                        cutOffBeginning = !first,
                        cutOffEnd = text.isNotEmpty()
                    ))

                    if (text.isNotEmpty()) {
                        state.gotoNextLine()
                    } else {
                        state.x += textToDrawWidth
                    }

                    textIndex = text.length

                    first = false
                }

                if (span.endsInNewline)
                    state.gotoNextLine()
            }

            span to partialTexts
        }
    }

    fun draw(state: MarkdownState, textColor: Color, isMultilineCode: Boolean = false) {
        if (!isMultilineCode)
            calculatePartialTexts(state)

        partialTexts!!.forEach { (span, partialTexts) ->
            fun drawString(text: String, x: Float, y: Float, color: Color, shadow: Boolean) {
                val actualColor = if (span.style.isURL) {
                    state.config.urlConfig.fontColor
                } else color

                if (span.style.code) {
                    CodeblockElement.codeFontRenderer.drawString(text, x, y - 1.5f, actualColor.rgb, shadow)
                } else {
                    UniversalGraphicsHandler.drawString(text, x, y, actualColor.rgb, shadow)
                }
            }

            fun drawBackground(x1: Float, y1: Float, x2: Float, y2: Float, cutOffBeginning: Boolean = false, cutOffEnd: Boolean = false) {
                if (!span.style.code || isMultilineCode)
                    return

                state.config.inlineCodeConfig.run {
                    UIRoundedRectangle.drawRoundedRectangle(
                        if (cutOffBeginning) state.left - 10.0 else x1.toDouble() - leftPadding,
                        y1.toDouble() - topPadding,
                        if (cutOffEnd) state.left + state.width + 10.0 else x2.toDouble() + rightPadding,
                        y2.toDouble() + bottomPadding,
                        cornerRadius,
                        steps,
                        outlineColor
                    )

                    UIRoundedRectangle.drawRoundedRectangle(
                        if (cutOffBeginning) state.left - 10.0 else x1.toDouble() - leftPadding + outlineWidth,
                        y1.toDouble() - topPadding + outlineWidth,
                        if (cutOffEnd) state.left + state.width + 10.0 else x2.toDouble() + rightPadding - outlineWidth,
                        y2.toDouble() + bottomPadding - outlineWidth,
                        cornerRadius,
                        steps,
                        backgroundColor
                    )
                }
            }

            val scale = state.textScaleModifier.toDouble()
            UniversalGraphicsHandler.scale(scale, scale, 1.0)

            val mouseX = UniversalMouse.getScaledX()
            val mouseY = UniversalResolutionUtil.getInstance().scaledHeight - UniversalMouse.getScaledY()

            val isHovered = span.style.isURL && partialTexts.any { (_, bounds) ->
                mouseX > bounds.x1 && mouseX < bounds.x2 && mouseY > bounds.y1 && mouseY < bounds.y2
            }

            partialTexts.forEach { (text, bounds, cutOffBeginning, cutOffEnd) ->
                drawBackground(
                    bounds.x1,
                    bounds.y1,
                    bounds.x2,
                    bounds.y2,
                    cutOffBeginning,
                    cutOffEnd
                )

                drawString(
                    span.style.applyStyle(text),
                    bounds.x1,
                    bounds.y1,
                    textColor,
                    state.textConfig.shadow
                )

                if (isHovered && state.config.urlConfig.showBarOnHover) {
                    UIBlock.drawBlock(
                        state.config.urlConfig.barColor,
                        bounds.x1.toDouble(),
                        bounds.y2.toDouble() + state.config.urlConfig.spaceBeforeBar,
                        bounds.x2.toDouble(),
                        bounds.y2.toDouble() + state.config.urlConfig.spaceBeforeBar + state.config.urlConfig.barWidth
                    )
                }
            }

            UniversalGraphicsHandler.scale(1f / scale, 1f / scale, 1.0)
        }
    }

    internal data class Style(
        var italic: Boolean = false,
        var bold: Boolean = false,
        var code: Boolean = false,
        var url: String? = null
    ) {
        val isURL: Boolean
            get() = url != null

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

    companion object {
        private val specialChars = listOf('*', '_', '`', '[', ']', ')')

        fun parse(text: String): TextElement {
            val style = Style()
            var spanStart = 0
            val spans = mutableListOf<Span>()
            var inURL = false
            var inURLText = false
            val replacedText = text.replace("\n", " ")

            fun addSpan(index: Int, hasNewline: Boolean = false) {
                if (index == spanStart && !hasNewline)
                    return
                spans.add(Span(replacedText.substring(spanStart, index), style.copy(), hasNewline))
            }

            var index = 0
            loop@ while (index < replacedText.length) {
                val ch = replacedText[index]

                if (ch == '\\' && index != replacedText.lastIndex) {
                    index++
                    continue
                }

                if (!isSpecialChar(ch) || (inURL && ch != ')') || (style.code && ch != '`')) {
                    index++
                    continue
                }

                when (ch) {
                    '*', '_' -> {
                        addSpan(index)
                        if (index + 1 <= replacedText.lastIndex && ch == replacedText[index + 1]) {
                            index++
                            style.bold = !style.bold
                        } else {
                            style.italic = !style.italic
                        }
                    }
                    '`' -> {
                        addSpan(index)
                        if (style.code) {
                            style.code = false
                        } else if (index + 1 < replacedText.length) {
                            val remainingLines = replacedText.substring(index + 1).split('\n')
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
                    '[' -> {
                        addSpan(index)
                        inURLText = true
                    }
                    ']' -> {
                        addSpan(index)

                        if (inURLText && index + 2 < replacedText.length && replacedText[index + 1] == '(') {
                            inURLText = false
                            val remainingText = replacedText.substring(index + 2)
                            val closeParenIndex = remainingText.indexOf(')')

                            if (closeParenIndex != -1 && '\n' !in remainingText.substring(0, closeParenIndex)) {
                                index++
                                spanStart = index + 1
                            }

                            inURL = true
                        }
                    }
                    ')' -> {
                        if (inURL) {
                            inURL = false
                            spans.last().style.url = replacedText.substring(spanStart, index)
                        } else {
                            index++
                            continue@loop
                        }
                    }
                    else -> throw IllegalStateException()
                }

                index++
                spanStart = index
            }

            addSpan(replacedText.length)

            return TextElement(spans)
        }

        private fun isSpecialChar(ch: Char) = ch in specialChars
    }
}