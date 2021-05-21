package gg.essential.elementa.markdown.selection

import gg.essential.elementa.markdown.drawables.ImageDrawable

class ImageCursor(target: ImageDrawable) : Cursor<ImageDrawable>(target) {
    override fun compareTo(other: Cursor<*>): Int {
        if (other !is ImageCursor) {
            return target.y.compareTo(other.target.y).let {
                if (it == 0) target.x.compareTo(other.target.x) else it
            }
        }

        return if (target.url == other.target.url) return 0 else 1
    }
}
