package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownConfig

class ListDrawable(
    config: MarkdownConfig,
    private val drawables: DrawableList,
    private val isOrdered: Boolean,
    /**
     * A "loose" list is a list in which any of its list items are
     * separated by blank lines, or if any item contains two block
     * elements with a blank line between them. A loose list has more
     * separation between list elements than a tight list does.
     *
     * Reference: https://spec.commonmark.org/0.28/#tight
     */
    private var isLoose: Boolean
) : Drawable(config) {
    private val listItems = mutableListOf<ListEntry>()

    private val elementSpacing: Float get() = if (isLoose) {
        config.listConfig.elementSpacingLoose
    } else config.listConfig.elementSpacingTight

    /**
     * The indentation of this list in any parent lists. This is set
     * below in the layoutImpl method
     */
    private var indentLevel = 0

    init {
        trim(drawables)
        drawables.parent = this
    }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        listItems.clear()

        val marginTop = if (insertSpaceBefore) config.listConfig.spaceBeforeList else 0f
        val marginBottom = if (insertSpaceAfter) config.listConfig.spaceAfterList else 0f
        var currY = y + marginTop
        val spaceAfterSymbol = config.listConfig.spaceBeforeText
        val indentation = config.listConfig.indentation

        var orderedListShift = 0

        fun symbol(index: Int): String {
            return if (isOrdered) {
                "${index + 1 - orderedListShift}."
            } else {
                val symbols = config.listConfig.unorderedSymbols
                symbols[indentLevel % symbols.length].toString()
            }
        }

        // Get the maximum width of all list item symbols to align
        // them with each other. Unordered lists have the same symbol
        // at each level, however ordered lists are variable width.
        val symbolWidth = if (isOrdered) {
            val dotWidth = '.'.width()
            drawables.indices.filter {
                drawables[it] !is ListDrawable
            }.map {
                it.toString().width() + dotWidth
            }.max()!!
        } else {
            val symbols = config.listConfig.unorderedSymbols
            symbols[indentLevel % symbols.length].width()
        }

        var index = 0

        fun addItem(drawable: Drawable) {
            val item = ListEntry(
                config,
                symbol(index),
                symbolWidth,
                spaceAfterSymbol,
                drawable
            )
            listItems.add(item)
            currY += item.layout(x + indentation, currY, width - indentation).height
            currY += elementSpacing
        }

        for (drawable_ in drawables) {
            var drawable = drawable_
            if (drawable is ListDrawable)
                drawable.indentLevel = indentLevel + 1
            if (drawable is DrawableList) {
                // Pull out any trailing ListDrawables into their own ListElement
                val last = drawable.last()
                if (last is ListDrawable) {
                    drawable = DrawableList(config, drawable.dropLast(1))
                    addItem(drawable)
                    index++
                    orderedListShift++
                    trim(last)
                    last.isLoose = isLoose
                    drawable = last
                }
            }
            addItem(drawable)
            index++
        }

        currY -= elementSpacing
        currY += marginBottom

        val height = currY - y

        return Layout(
            x,
            y,
            width,
            height,
            Margin(0f, marginTop, 0f, marginBottom)
        )
    }

    override fun draw(state: DrawState) {
        listItems.forEach { it.draw(state) }
    }

    override fun cursorAt(mouseX: Float, mouseY: Float) = drawables.cursorAt(mouseX, mouseY)
    override fun cursorAtStart() = drawables.cursorAtStart()
    override fun cursorAtEnd() = drawables.cursorAtEnd()

    // A mostly organized and ready-to-render list item
    class ListEntry(
        config: MarkdownConfig,
        private val symbol: String,
        private val symbolWidth: Float,
        private val symbolPaddingRight: Float,
        val drawable: Drawable
    ) : Drawable(config) {
        private val actualSymbolWidth = symbol.width()

        init {
            trim(drawable)

            // trim any space around list elements
            if (drawable is DrawableList) {
                for ((index, item) in drawable.withIndex()) {
                    if (item is ListDrawable) {
                        if (index != 0)
                            trim(drawable[index - 1])
                        if (index != drawable.lastIndex)
                            trim(drawable[index + 1])
                    }
                }
            }
        }

        override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
            val nonDrawableSpace = symbolWidth + symbolPaddingRight
            drawable.layout(x + nonDrawableSpace, y, width - nonDrawableSpace)
            return Layout(x, y, width, drawable.height)
        }

        override fun draw(state: DrawState) {
            val newX = x + symbolWidth - actualSymbolWidth
            if (drawable !is ListDrawable)
                TextDrawable.drawString(config, symbol, newX + state.xShift, y + state.yShift)
            drawable.draw(state)
        }

        override fun cursorAt(mouseX: Float, mouseY: Float) = drawable.cursorAt(mouseX, mouseY)
        override fun cursorAtStart() = drawable.cursorAtStart()
        override fun cursorAtEnd() = drawable.cursorAtEnd()
    }
}
