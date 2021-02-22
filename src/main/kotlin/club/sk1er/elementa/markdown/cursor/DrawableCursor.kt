package club.sk1er.elementa.markdown.cursor

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.drawables.Drawable
import club.sk1er.elementa.markdown.drawables.DrawableList
import club.sk1er.elementa.markdown.drawables.ListDrawable
import club.sk1er.elementa.markdown.drawables.ParagraphDrawable

abstract class DrawableCursor {
    abstract val target: Drawable

    abstract fun moveToStart()

    abstract fun moveToEnd()

    abstract fun moveTo(mouseX: Float, mouseY: Float)

    // TODO: Remove this. We'll never actually be rendering cursors normally,
    // this is just to make sure the cursors work before getting selection working
    abstract fun draw(state: DrawState)

    companion object {
        fun forDrawable(drawable: Drawable) = when (drawable) {
            is ParagraphDrawable -> ParagraphCursor(drawable)
            is ListDrawable -> DrawableListCursor(
                DrawableList(drawable.config, drawable.listItems.map { it.drawable })
            )
            is DrawableList -> DrawableListCursor(drawable)
            else -> TODO()
        }
    }
}
