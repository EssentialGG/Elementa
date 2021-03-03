package club.sk1er.elementa.markdown.selection

import club.sk1er.elementa.markdown.drawables.TextDrawable

class TextSelection(val start: TextCursor, val end: TextCursor) {
    val textDrawables = mutableListOf<TextDrawable>()

    fun remove() {
        textDrawables.forEach {
            it.selectionStart = -1
            it.selectionEnd = -1
        }
    }
}
