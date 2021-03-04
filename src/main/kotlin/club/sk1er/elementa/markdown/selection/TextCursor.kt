package club.sk1er.elementa.markdown.selection

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.drawables.Drawable
import club.sk1er.elementa.markdown.drawables.TextDrawable
import java.awt.Color

/**
 * A simple class which points to a position in a TextDrawable.
 */
data class TextCursor(private val target: TextDrawable, private val offset: Int) {
    private val xBase = target.x + target.formattedText.substring(0, offset + target.styleChars()).width(target.scaleModifier)
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

    fun selectionTo(other: TextCursor): TextSelection {
        // Ensure correct cursor ordering
        if (this > other)
            return other.selectionTo(this)

        val selection = TextSelection(this, other)

        if (target == other.target) {
            selection.textDrawables.add(target)
            target.selectionStart = offset
            target.selectionEnd = other.offset
            return selection
        }

        // Configure selection area for the starting target
        target.selectionStart = offset
        target.selectionEnd = target.plainText().length

        // We now have to iterate the entire markdown tree structure.
        var currentTarget: TextDrawable? = target

        loop@while (currentTarget != null) {
            selection.textDrawables.add(currentTarget)
            currentTarget = nextText(currentTarget)

            when (currentTarget) {
                null -> throw IllegalStateException()
                other.target -> {
                    selection.textDrawables.add(currentTarget)
                    currentTarget.selectionStart = 0
                    currentTarget.selectionEnd = other.offset
                    break@loop
                }
                else -> {
                    currentTarget.selectionStart = 0
                    currentTarget.selectionEnd = currentTarget.plainText().length
                }
            }
        }

        return selection
    }

    private fun nextText(drawable: TextDrawable): TextDrawable? {
        var nextText: Drawable? = drawable.next
        while (nextText != null && nextText !is TextDrawable)
            nextText = nextText.next

        if (nextText is TextDrawable)
            return nextText

        var nextContainer: Drawable = drawable.parent ?: return null
        while (nextContainer.next == null) {
            if (nextContainer.parent == null)
                return null
            nextContainer = nextContainer.parent!!
        }

        return firstTextChild(nextContainer.next!!)
    }

    private fun firstTextChild(drawable: Drawable): TextDrawable? {
        if (drawable is TextDrawable)
            return drawable

        if (drawable.children.isEmpty())
            return null

        return firstTextChild(drawable.children.first())
    }

    operator fun compareTo(other: TextCursor): Int {
        if (target == other.target)
            return offset.compareTo(other.offset)

        if (target.y == other.target.y)
            return target.x.compareTo(other.target.x)

        return target.y.compareTo(other.target.y)
    }
}
