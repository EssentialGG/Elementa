package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.MarkdownConfig

class BlockquoteDrawable(config: MarkdownConfig, val drawables: DrawableList) : Drawable(config) {
    private var dividerHeight: Float = -1f

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        val config = config.blockquoteConfig
        val padding = config.spaceBeforeDivider + config.dividerWidth + if (insertSpaceAfter) {
            config.spaceAfterDivider
        } else 0f

        var currY = y
        currY += config.spaceBeforeBlockquote
        val dividerStart = currY
        currY += config.dividerPaddingTop

        drawables.last().also {
            if (it is ParagraphDrawable)
                it.insertSpaceAfter = false
        }

        drawables.forEach {
            currY += it.layout(x + padding, currY, width).height
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

    override fun draw() {
        UIBlock.drawBlockSized(
            config.blockquoteConfig.dividerColor,
            x + config.blockquoteConfig.spaceBeforeDivider.toDouble(),
            y + config.blockquoteConfig.spaceBeforeBlockquote.toDouble(),
            config.blockquoteConfig.dividerWidth.toDouble(),
            dividerHeight.toDouble()
        )

        drawables.forEach(Drawable::draw)
    }
}
