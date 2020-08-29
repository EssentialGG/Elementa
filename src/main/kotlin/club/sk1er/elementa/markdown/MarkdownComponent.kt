package club.sk1er.elementa.markdown

import club.sk1er.elementa.UIComponent

class MarkdownComponent(
    text: String,
    private val config: MarkdownConfig = MarkdownConfig()
) : UIComponent() {
    private val document = Document.fromString(text)

    override fun draw() {
        document?.draw(MarkdownState(
            getLeft(),
            getTop(),
            0f,
            0f,
            getWidth(),
            getHeight(),
            1f,
            config
        ))
    }
}