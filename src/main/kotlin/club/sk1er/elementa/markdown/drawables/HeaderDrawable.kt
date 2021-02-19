package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.MarkdownConfig

class HeaderDrawable(
    config: MarkdownConfig,
    private val level: Int,
    private val paragraph: ParagraphDrawable
) : Drawable(config) {
    private val headerConfig = when (level) {
        1 -> config.headerConfig.level1
        2 -> config.headerConfig.level2
        3 -> config.headerConfig.level3
        4 -> config.headerConfig.level4
        5 -> config.headerConfig.level5
        6 -> config.headerConfig.level6
        else -> throw IllegalStateException()
    }

    private var textHeight: Height = -1f

    init {
        paragraph.scaleModifier = headerConfig.textScale
    }

    override fun layoutImpl(): Height {
        textHeight = paragraph.layout(this.x, this.y + headerConfig.verticalSpaceBefore, this.width) -
            config.paragraphConfig.spaceAfter // Ignore this paragraph config here

        return headerConfig.verticalSpaceBefore + textHeight + headerConfig.verticalSpaceAfter + if (headerConfig.hasDivider) {
            headerConfig.spaceBeforeDivider + headerConfig.dividerWidth
        } else 0f
    }

    override fun draw() {
        paragraph.draw()

        if (headerConfig.hasDivider) {
            val y = this.y + headerConfig.verticalSpaceBefore + textHeight + headerConfig.spaceBeforeDivider
            UIBlock.drawBlockSized(
                headerConfig.dividerColor, 
                this.x.toDouble(),
                y.toDouble(),
                this.width.toDouble(),
                headerConfig.dividerWidth.toDouble()
            )
        }
    }
}
