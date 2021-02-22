package club.sk1er.elementa.markdown.cursor

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.drawables.Drawable

abstract class DrawableCursor {
    abstract val target: Drawable
    protected var isSelecting = false

    abstract fun moveToStart()

    abstract fun moveToEnd()

    abstract fun moveTo(mouseX: Float, mouseY: Float)

    open fun toggleSelecting() {
        isSelecting = !isSelecting
    }

    // TODO: Remove this. We'll never actually be rendering cursors normally,
    // this is just to make sure the cursors work before getting selection working
    abstract fun draw(state: DrawState)
}
