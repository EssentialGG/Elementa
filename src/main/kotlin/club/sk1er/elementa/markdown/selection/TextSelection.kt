package club.sk1er.elementa.markdown.selection

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.drawables.Drawable
import club.sk1er.elementa.markdown.drawables.TextDrawable

class TextSelection(val start: TextCursor, val end: TextCursor) {
    val textDrawables = mutableListOf<TextDrawable>()

    fun draw(state: DrawState) {
        start.draw(state)
        end.draw(state)
    }

    fun remove() {
        textDrawables.forEach {
            it.selectionStart = -1
            it.selectionEnd = -1
        }
    }
}
