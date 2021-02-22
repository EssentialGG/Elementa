package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.MarkdownConfig

class DrawableList(
    config: MarkdownConfig,
    val drawables: List<Drawable>
) : Drawable(config), List<Drawable> by drawables {
    init {
        trim(drawables)

        drawables.forEachIndexed { index, drawable ->
            if (index > 0)
                drawable.previous = drawables[index - 1]
            if (index != drawables.lastIndex)
                drawable.next = drawables[index + 1]
        }
    }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        var currY = y
        drawables.forEach {
            currY += it.layout(x, currY, width).height
        }
        val height = currY - y
        return Layout(x, currY, width, height)
    }

    override fun draw() {
        drawables.forEach(Drawable::draw)
    }
}
