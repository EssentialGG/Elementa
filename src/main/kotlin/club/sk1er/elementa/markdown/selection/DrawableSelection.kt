package club.sk1er.elementa.markdown.selection

import club.sk1er.elementa.markdown.cursor.DrawableCursor

abstract class DrawableSelection(
    val start: DrawableCursor,
    val end: DrawableCursor
) {
    init {
        if (start.target != end.target)
            throw IllegalStateException()
    }

    abstract fun getText(): String

    abstract fun draw()
}
