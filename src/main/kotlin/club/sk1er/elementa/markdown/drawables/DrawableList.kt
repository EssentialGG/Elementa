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

    override fun layoutImpl(): Height {
        var y = this.y
        drawables.forEach {
            y += it.layout(x, y, width)
        }
        return y - this.y
    }

    override fun draw() {
        drawables.forEach(Drawable::draw)
    }
}
