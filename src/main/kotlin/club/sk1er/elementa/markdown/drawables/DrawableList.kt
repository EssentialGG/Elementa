package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownConfig
import java.util.*

class DrawableList(
    config: MarkdownConfig,
    private var drawables: List<Drawable>
) : Drawable(config), List<Drawable> {
    fun setDrawables(newDrawables: List<Drawable>) {
        drawables = newDrawables
        trim(this)

        forEachIndexed { index, drawable ->
            if (index > 0)
                drawable.previous = this[index - 1]
            if (index != lastIndex)
                drawable.next = this[index + 1]
        }
    }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        var currY = y
        forEach {
            currY += it.layout(x, currY, width).height
        }
        val height = currY - y
        return Layout(x, y, width, height)
    }

    override fun draw(state: DrawState) {
        forEach { it.draw(state) }
    }

    override val size get() = drawables.size
    override fun contains(element: Drawable) = element in drawables
    override fun containsAll(elements: Collection<Drawable>) = drawables.containsAll(elements)
    override fun get(index: Int) = drawables[index]
    override fun indexOf(element: Drawable) = drawables.indexOf(element)
    override fun isEmpty() = drawables.isEmpty()
    override fun iterator() = drawables.iterator()
    override fun lastIndexOf(element: Drawable) = drawables.lastIndexOf(element)
    override fun listIterator() = drawables.listIterator()
    override fun listIterator(index: Int) = drawables.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = drawables.subList(fromIndex, toIndex)
}
