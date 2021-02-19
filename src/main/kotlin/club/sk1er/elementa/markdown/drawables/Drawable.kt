package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.MarkdownConfig

typealias Height = Float

abstract class Drawable(val config: MarkdownConfig) {
    var x = -1f
    var y = -1f
    var width = -1f

    // Used to disable top and bottom padding in some elements
    var insertSpaceBefore = true
    var insertSpaceAfter = true

    var previous: Drawable? = null
    var next: Drawable? = null

    fun layout(x: Float, y: Float, width: Float): Height {
        this.x = x
        this.y = y
        this.width = width
        return layoutImpl()
    }

    abstract fun layoutImpl(): Height

    abstract fun draw()

    companion object {
        // To resolve the ambiguity
        fun trim(drawableList: DrawableList) {
            trim(drawableList as List<Drawable>)
        }

        fun trim(drawables: List<Drawable>) {
            drawables.firstOrNull()?.also {
                it.insertSpaceBefore = false
            }
            drawables.lastOrNull()?.also {
                it.insertSpaceAfter = false
            }
        }

        fun trim(drawable: Drawable) {
            if (drawable is DrawableList) {
                trim(drawable.drawables)
            } else {
                drawable.insertSpaceBefore = false
                drawable.insertSpaceAfter = false
            }
        }
    }
}
