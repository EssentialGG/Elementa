package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.MarkdownConfig

class DrawableList(config: MarkdownConfig, val drawables: List<Drawable>) : Drawable(config) {
    init {
        trim(drawables)
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
