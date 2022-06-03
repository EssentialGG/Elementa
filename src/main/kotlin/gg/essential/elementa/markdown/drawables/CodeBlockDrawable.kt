package gg.essential.elementa.markdown.drawables

import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.dsl.width
import gg.essential.elementa.font.DefaultFonts
import gg.essential.elementa.markdown.DrawState
import gg.essential.elementa.markdown.MarkdownComponent
import gg.essential.elementa.markdown.selection.CodeTextCursor
import gg.essential.elementa.markdown.selection.Cursor
import gg.essential.universal.UMatrixStack
import kotlin.math.abs

class CodeBlockDrawable(md: MarkdownComponent, internal val text: String) : Drawable(md) {
    internal val lines = text.split('\n').dropLastWhile { it.isEmpty() }
    internal var selection: IntRange = 0..0

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        return Layout(
                x,
                y,
                width,
                DefaultFonts.VANILLA_FONT_RENDERER.getStringHeight("", 10f) * lines.size + (config.codeBlockConfig.let { it.topPadding + it.bottomPadding + it.outlineWidth + it.topMargin }),
                Margin(top = config.codeBlockConfig.topMargin, bottom = config.codeBlockConfig.bottomMargin)
        )
    }

    override fun draw(matrixStack: UMatrixStack, state: DrawState) {
        val x1 = x + state.xShift
        val y1 = y + state.yShift - 1f
        val x2 = x1 + width
        val y2 = y1 + height
        val outlineWidth = config.codeBlockConfig.outlineWidth

        UIRoundedRectangle.drawRoundedRectangle(
                matrixStack,
                x1,
                y1,
                x2,
                y2,
                config.codeBlockConfig.cornerRadius,
                config.codeBlockConfig.outlineColor
        )

        UIRoundedRectangle.drawRoundedRectangle(
                matrixStack,
                x1 + outlineWidth,
                y1 + outlineWidth,
                x2 - outlineWidth,
                y2 - outlineWidth,
                config.codeBlockConfig.cornerRadius,
                config.codeBlockConfig.backgroundColor
        )

        lines.forEachIndexed { index, line ->
            DefaultFonts.VANILLA_FONT_RENDERER.drawString(
                matrixStack,
                line,
                config.codeBlockConfig.fontColor,
                x1 + config.codeBlockConfig.leftPadding,
                y1 + config.codeBlockConfig.topMargin + DefaultFonts.VANILLA_FONT_RENDERER.getStringHeight("", 10f) * index,
                10f,
                1f,
                false
            )
        }
    }

    override fun cursorAt(mouseX: Float, mouseY: Float, dragged: Boolean, mouseButton: Int): Cursor<*> {
        fun String.textWidth(offset: Int) = this.substring(0, offset).width(1f)

        val index = ((mouseY - y - config.codeBlockConfig.let { it.topMargin + it.topPadding }) / DefaultFonts.VANILLA_FONT_RENDERER.getStringHeight("", 10f) + 0.5).toInt()
        val line = lines[index]

        var offset = 0
        var cachedWidth = 0f

        // Iterate from left to right in the text component until we find a good
        // offset based on the text width
        while (offset < line.length) {
            offset++
            val newWidth = line.textWidth(offset)

            if (x + config.codeBlockConfig.leftPadding + newWidth > mouseX) {
                // We've passed mouseX, but now we have to consider which offset
                // is closer to mouseX: `offset` or `offset - 1`. We check that
                // here and use the closest offset

                val oldDist = abs(mouseX - x - config.codeBlockConfig.leftPadding - cachedWidth)
                val newDist = abs(newWidth - (mouseX - x - config.codeBlockConfig.leftPadding))

                if (oldDist < newDist) {
                    // The old offset was better
                    offset--
                }

                return CodeTextCursor(this, index, offset, lines.subList(0, index - 1).sumOf { it.length } + line.length)
            }

            cachedWidth = newWidth
        }

        return CodeTextCursor(this, index, line.length, lines.subList(0, index).sumOf { it.length })
    }

    override fun cursorAtStart(): Cursor<*> = CodeTextCursor(this, 0, 0, 0)
    override fun cursorAtEnd(): Cursor<*> = CodeTextCursor(this, lines.size, lines.last().length, text.length)

    override fun selectedText(asMarkdown: Boolean): String = text.substring(selection)
}
