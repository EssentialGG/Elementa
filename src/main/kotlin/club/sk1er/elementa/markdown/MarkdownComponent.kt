package club.sk1er.elementa.markdown

import club.sk1er.elementa.UIComponent

/**
 * Component that parses a string as Markdown and renders it.
 *
 * This component's width and height must be non-child-related
 * constraints. This is because this component will not have any
 * children, as all rendering is done directly via rendering calls.
 */
class MarkdownComponent(
    text: String,
    private val config: MarkdownConfig = MarkdownConfig()
) : UIComponent() {
    private val document = Document.fromString(text)

    override fun draw() {
        document?.draw(MarkdownState(
            getLeft(),
            getTop(),
            getWidth(),
            getHeight(),
            config = config
        ))
    }
}