package club.sk1er.elementa.markdown.selection

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.drawables.Drawable
import club.sk1er.elementa.markdown.drawables.TextDrawable

class TextSelection private constructor(val start: TextCursor, val end: TextCursor) {
    val textDrawables = mutableListOf<TextDrawable>()

    init {
        if (start.target == end.target) {
            textDrawables.add(start.target)
            start.target.selectionStart = start.offset
            start.target.selectionEnd = end.offset
        } else {
            // Configure selection area for the starting target
            start.target.selectionStart = start.offset
            start.target.selectionEnd = start.target.plainText().length

            // We now have to iterate the entire markdown tree structure.
            var currentTarget: TextDrawable? = start.target

            loop@ while (currentTarget != null) {
                textDrawables.add(currentTarget)
                currentTarget = nextText(currentTarget)

                when (currentTarget) {
                    null -> throw IllegalStateException()
                    end.target -> {
                        textDrawables.add(currentTarget)
                        currentTarget.selectionStart = 0
                        currentTarget.selectionEnd = end.offset
                        break@loop
                    }
                    else -> {
                        currentTarget.selectionStart = 0
                        currentTarget.selectionEnd = currentTarget.plainText().length
                    }
                }
            }
        }
    }

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

    companion object {
        fun fromCursors(first: TextCursor, second: TextCursor): TextSelection {
            return if (first <= second) TextSelection(first, second) else TextSelection(second, first)
        }
    }
}
