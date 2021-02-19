package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.MarkdownConfig

typealias Height = Float

abstract class Drawable(val config: MarkdownConfig) {
    var x = -1f
    var y = -1f
    var width = -1f

    fun layout(x: Float, y: Float, width: Float): Height {
        this.x = x
        this.y = y
        this.width = width
        return layoutImpl()
    }

    abstract fun layoutImpl(): Height

    abstract fun draw()
}
