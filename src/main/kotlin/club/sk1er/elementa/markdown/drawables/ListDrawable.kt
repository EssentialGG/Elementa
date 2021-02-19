package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.MarkdownConfig

class ListDrawable(
    config: MarkdownConfig,
    val elements: List<ListElement>,
    val isOrdered: Boolean
) : Drawable(config) {
    override fun layoutImpl(): Height {
        TODO("Not yet implemented")
    }

    override fun draw() {
        TODO("Not yet implemented")
    }
}

class ListElement(val indent: Int, val text: List<Drawable>)
