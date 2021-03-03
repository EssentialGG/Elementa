package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownConfig

class BlockquoteDrawable(config: MarkdownConfig, val drawables: DrawableList) : Drawable(config) {
    private var dividerHeight: Float = -1f

    init {
        drawables.parent = this
    }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        val config = config.blockquoteConfig

        // Horizontal padding due to the quote bar, which will shift the drawables to the right
        val padding = config.spaceBeforeDivider + config.dividerWidth + config.spaceAfterDivider

        var currY = y
        currY += if (insertSpaceBefore) config.spaceBeforeBlockquote else 0f
        val dividerStart = currY
        currY += config.dividerPaddingTop

        trim(drawables)

        drawables.forEach {
            // Layout our children taking into account the quote bar padding
            currY += it.layout(x + padding, currY, width - padding).height
        }

        currY += config.dividerPaddingBottom
        dividerHeight = currY - dividerStart
        if (insertSpaceAfter)
            currY += config.spaceAfterBlockquote

        val height = currY - y

        return Layout(
            x,
            y,
            width,
            height,
            Margin(0f, config.spaceBeforeBlockquote, 0f, config.spaceAfterBlockquote)
        )
    }

    override fun draw(state: DrawState) {
        UIBlock.drawBlockSized(
            config.blockquoteConfig.dividerColor,
            x + state.xShift + config.blockquoteConfig.spaceBeforeDivider.toDouble(),
            y + state.yShift + config.blockquoteConfig.spaceBeforeBlockquote.toDouble(),
            config.blockquoteConfig.dividerWidth.toDouble(),
            dividerHeight.toDouble()
        )

        drawables.forEach { it.draw(state) }
    }

    override fun cursorAt(mouseX: Float, mouseY: Float) = drawables.cursorAt(mouseX, mouseY)
    override fun cursorAtStart() = drawables.cursorAtStart()
    override fun cursorAtEnd() = drawables.cursorAtEnd()
}
