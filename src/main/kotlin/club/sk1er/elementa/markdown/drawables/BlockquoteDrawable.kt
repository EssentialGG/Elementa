package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.MarkdownConfig

class BlockquoteDrawable(config: MarkdownConfig, val drawables: DrawableList) : Drawable(config) {
    private var dividerHeight: Height = -1f

    override fun layoutImpl(): Height {
        val config = config.blockquoteConfig
        val padding = config.spaceBeforeDivider + config.dividerWidth + if (insertSpaceAfter) {
            config.spaceAfterDivider
        } else 0f
        var y = this.y
        y += config.spaceBeforeBlockquote
        val dividerStart = y
        y += config.dividerPaddingTop

        drawables.last().also {
            if (it is ParagraphDrawable)
                it.insertSpaceAfter = false
        }

        drawables.forEach {
            y += it.layout(this.x + padding, y, this.width)
        }

        y += config.dividerPaddingBottom
        dividerHeight = y - dividerStart
        if (insertSpaceAfter)
            y += config.spaceAfterBlockquote

        return y - this.y
    }

    override fun draw() {
        UIBlock.drawBlockSized(
            config.blockquoteConfig.dividerColor,
            this.x + config.blockquoteConfig.spaceBeforeDivider.toDouble(),
            this.y + config.blockquoteConfig.spaceBeforeBlockquote.toDouble(),
            config.blockquoteConfig.dividerWidth.toDouble(),
            dividerHeight.toDouble()
        )

        drawables.forEach(Drawable::draw)
    }
}
