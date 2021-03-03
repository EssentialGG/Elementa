package club.sk1er.elementa.markdown.cursor

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.drawables.TextDrawable
import java.awt.Color

/**
 * A simple class which points to a position in a TextDrawable.
 */
class TextCursor(target: TextDrawable, offset: Int) {
    private val xBase = target.x + target.formattedText.substring(0, offset).width(target.scaleModifier)
    private val yBase = target.y
    private val height = target.height.toDouble()
    private val width = height / 9.0

    fun draw(state: DrawState) {
        UIBlock.drawBlockSized(
            Color.RED,
            (xBase + state.xShift).toDouble(),
            (yBase + state.yShift).toDouble(),
            width,
            height
        )
    }
}
