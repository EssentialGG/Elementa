package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.MarkdownConfig

class ListDrawable(
    config: MarkdownConfig,
    private val drawables: DrawableList,
    private val isOrdered: Boolean,
    isLoose: Boolean
) : Drawable(config) {
    private val listItems = mutableListOf<ListEntry>()

    private var isLoose: Boolean = isLoose
        set(value) {
            field = value
            elementSpacing = if (isLoose) {
                config.listConfig.elementSpacingLoose
            } else config.listConfig.elementSpacingTight
        }

    private var elementSpacing = if (isLoose) {
        config.listConfig.elementSpacingLoose
    } else config.listConfig.elementSpacingTight

    private var indentLevel = 0

    init {
        trim(drawables)
    }

    override fun layoutImpl(): Height {
        listItems.clear()
        var y = this.y + if (insertSpaceBefore) config.listConfig.spaceBeforeList else 0f
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
            y += item.layout(x + indentation, y, width - indentation)
            y += elementSpacing
        }

        for (drawable_ in drawables) {
            var drawable = drawable_
            if (drawable is ListDrawable)
                drawable.indentLevel = indentLevel + 1
            if (drawable is DrawableList) {
                // Pull out any trailing ListDrawables into their own ListElement
                val last = drawable.drawables.last()
                if (last is ListDrawable) {
                    drawable = DrawableList(config, drawable.drawables.dropLast(1))
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

        y -= elementSpacing
        if (insertSpaceAfter)
            y += config.listConfig.spaceAfterList

        return y - this.y
    }

    override fun draw() {
        listItems.forEach(Drawable::draw)
    }

    // A mostly organized and ready-to-render list item
    private class ListEntry(
        config: MarkdownConfig,
        val symbol: String,
        val symbolWidth: Float,
        val symbolPaddingRight: Float,
        val drawable: Drawable
    ) : Drawable(config) {
        private val actualSymbolWidth = symbol.width()

        init {
            trim(drawable)

            // trim any space around list elements
            if (drawable is DrawableList) {
                for ((index, item) in drawable.drawables.withIndex()) {
                    if (item is ListDrawable) {
                        if (index != 0)
                            trim(drawable.drawables[index - 1])
                        if (index != drawable.drawables.lastIndex)
                            trim(drawable.drawables[index + 1])
                    }
                }
            }
        }

        override fun layoutImpl(): Height {
            val nonDrawableSpace = symbolWidth + symbolPaddingRight
            return drawable.layout(x + nonDrawableSpace, y, width - nonDrawableSpace)
        }

        override fun draw() {
            val x = this.x + symbolWidth - actualSymbolWidth
            if (drawable !is ListDrawable)
                TextDrawable.drawString(config, symbol, x, y)
            drawable.draw()
        }
    }
}
