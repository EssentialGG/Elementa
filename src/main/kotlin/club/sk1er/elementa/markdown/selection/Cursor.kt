package club.sk1er.elementa.markdown.selection

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownComponent
import club.sk1er.elementa.markdown.drawables.Drawable
import java.awt.Color

abstract class Cursor<T : Drawable>(val target: T) {
    protected open val xBase = target.x
    protected open val yBase = target.y
    protected val height = target.height.toDouble()
    protected val width = height / 9.0

    fun draw(state: DrawState) {
        if (!MarkdownComponent.DEBUG)
            return

        UIBlock.drawBlockSized(
            Color.RED,
            (xBase + state.xShift).toDouble(),
            (yBase + state.yShift).toDouble(),
            width,
            height
        )
    }

    abstract operator fun compareTo(other: Cursor<*>): Int
}
