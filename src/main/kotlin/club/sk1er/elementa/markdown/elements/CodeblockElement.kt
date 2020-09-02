package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.font.FontRenderer
import club.sk1er.elementa.markdown.MarkdownState

class CodeblockElement private constructor(val text: TextElement) : Element() {
    override fun draw(state: MarkdownState) {
        if (state.x != state.newlineX)
            state.gotoNextLine()

        state.y += state.codeblockConfig.topMargin + state.codeblockConfig.topPadding

        state.newlineX += state.codeblockConfig.leftPadding
        state.x = state.newlineX
        state.width -= state.codeblockConfig.rightPadding

        text.calculateRenderables(state)

        state.y += state.codeblockConfig.bottomPadding
        state.width += state.codeblockConfig.rightPadding

        val firstPartialText = text.renderables!!.first().second.first()
        val lastPartialText = text.renderables!!.last().second.last()

        UIRoundedRectangle.drawRoundedRectangle(
            state.left.toDouble(),
            firstPartialText.bounds.y1.toDouble() - state.codeblockConfig.topPadding,
            state.left.toDouble() + state.width - 2,
            lastPartialText.bounds.y2.toDouble() + state.codeblockConfig.bottomPadding,
            state.codeblockConfig.cornerRadius,
            state.codeblockConfig.steps,
            state.codeblockConfig.outlineColor
        )

        val outlineWidth = state.codeblockConfig.outlineWidth

        UIRoundedRectangle.drawRoundedRectangle(
            state.left.toDouble() + outlineWidth,
            firstPartialText.bounds.y1.toDouble() - state.codeblockConfig.topPadding + outlineWidth,
            state.left.toDouble() + state.width  - outlineWidth - 2,
            lastPartialText.bounds.y2.toDouble() + state.codeblockConfig.bottomPadding - outlineWidth,
            state.codeblockConfig.cornerRadius,
            state.codeblockConfig.steps,
            state.codeblockConfig.backgroundColor
        )

        text.draw(state, state.codeblockConfig.fontColor, avoidRenderableCalculation = true)

        state.newlineX -= state.codeblockConfig.leftPadding
        state.x = state.newlineX
        state.y += state.codeblockConfig.bottomMargin
    }

    companion object {
        val codeFontRenderer = FontRenderer(FontRenderer.SupportedFont.Menlo, 18f)

        fun parse(lines: MutableList<String>): CodeblockElement? {
            if (!matches(lines))
                return null

            lines.removeAt(0)

            val spans = mutableListOf<String>().apply {
                while (!matchesClosing(lines[0])) {
                    add(lines[0])
                    lines.removeAt(0)
                }
                lines.removeAt(0)
            }.map {
                TextElement.Span(it, TextElement.Style(code = true), endsInNewline = true)
            }

            return CodeblockElement(TextElement(spans))
        }

        fun matches(lines: MutableList<String>) =
            lines.size >= 2 && matchesOpening(lines.first()) && lines.drop(1).any(::matchesClosing)

        private fun matchesOpening(line: String) = line.startsWith("```")

        private fun matchesClosing(line: String) = line.trim() == "```"
    }
}