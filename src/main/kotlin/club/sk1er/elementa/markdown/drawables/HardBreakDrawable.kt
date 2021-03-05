package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.elementa.markdown.selection.TextCursor

/**
 * A hard break is two or more line breaks between lines of
 * markdown text.
 */
class HardBreakDrawable(config: MarkdownConfig) : Drawable(config) {
    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        TODO("Not yet implemented")
    }

    override fun draw(state: DrawState) {
        TODO("Not yet implemented")
    }

    override fun cursorAt(mouseX: Float, mouseY: Float): TextCursor {
        TODO("Not yet implemented")
    }

    override fun cursorAtStart(): TextCursor {
        TODO("Not yet implemented")
    }

    override fun cursorAtEnd(): TextCursor {
        TODO("Not yet implemented")
    }

    override fun selectedText(asMarkdown: Boolean): String {
        TODO("Not yet implemented")
    }
}
