package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.markdown.HeaderConfig
import club.sk1er.elementa.markdown.HeaderLevelConfig
import club.sk1er.elementa.markdown.MarkdownState

class HeaderElement private constructor(
    private val textElement: TextElement,
    private val level: Int
) : Element() {
    init {
        // No text should follow the header element
        textElement.spans.last().endsInNewline = true

        // The header renders as bold text
        textElement.spans.forEach { it.style.bold = true }
    }

    override fun onClick(mouseX: Float, mouseY: Float) {
        textElement.onClick(mouseX, mouseY)
    }

    override fun draw(state: MarkdownState) {
        if (state.x != state.newlineX)
            state.gotoNextLine()

        if (state.previousElementType != HeaderElement::class)
            state.y += getSpace(state.headerConfig, true)

        val scale = getScale(state.headerConfig)
        state.textScaleModifier *= scale
        textElement.draw(state, getColor(state.headerConfig))
        state.textScaleModifier /= scale

        if (hasDivider(state.headerConfig)) {
            val width = dividerWidth(state.headerConfig)
            state.y += width

            UIBlock.drawBlock(
                dividerColor(state.headerConfig),
                state.left.toDouble() + state.newlineX,
                state.top.toDouble() + state.y - width,
                state.left.toDouble() + state.width,
                state.top.toDouble() + state.y
            )
        }

        state.gotoNextLine()
        state.y += getSpace(state.headerConfig, false)
    }

    private fun getScale(config: HeaderConfig) = when (level) {
        1 -> config.level1.textScale
        2 -> config.level2.textScale
        3 -> config.level3.textScale
        4 -> config.level4.textScale
        5 -> config.level5.textScale
        6 -> config.level6.textScale
        else -> throw IllegalStateException()
    }

    private fun getColor(config: HeaderConfig) = when (level) {
        1 -> config.level1.fontColor
        2 -> config.level2.fontColor
        3 -> config.level3.fontColor
        4 -> config.level4.fontColor
        5 -> config.level5.fontColor
        6 -> config.level6.fontColor
        else -> throw IllegalStateException()
    }

    private fun getSpace(config: HeaderConfig, before: Boolean): Float {
        val method = if (before) HeaderLevelConfig::spaceBefore else HeaderLevelConfig::spaceAfter

        return when (level) {
            1 -> config.level1
            2 -> config.level2
            3 -> config.level3
            4 -> config.level4
            5 -> config.level5
            6 -> config.level6
            else -> throw IllegalStateException()
        }.let(method)
    }

    private fun hasDivider(config: HeaderConfig) = when (level) {
        1 -> config.level1.hasDivider
        2 -> config.level2.hasDivider
        3 -> config.level3.hasDivider
        4 -> config.level4.hasDivider
        5 -> config.level5.hasDivider
        6 -> config.level6.hasDivider
        else -> throw IllegalStateException()
    }

    private fun dividerColor(config: HeaderConfig) = when (level) {
        1 -> config.level1.dividerColor
        2 -> config.level2.dividerColor
        3 -> config.level3.dividerColor
        4 -> config.level4.dividerColor
        5 -> config.level5.dividerColor
        6 -> config.level6.dividerColor
        else -> throw IllegalStateException()
    }

    private fun dividerWidth(config: HeaderConfig) = when (level) {
        1 -> config.level1.dividerWidth
        2 -> config.level2.dividerWidth
        3 -> config.level3.dividerWidth
        4 -> config.level4.dividerWidth
        5 -> config.level5.dividerWidth
        6 -> config.level6.dividerWidth
        else -> throw IllegalStateException()
    }

    companion object {
        fun parse(lines: MutableList<String>): HeaderElement? {
            if (lines.isEmpty())
                return null

            val line = lines.first()
            if (!matches(line))
                return null

            val level = line.takeWhile { it == '#' }.length

            lines.removeAt(0)
            val text = if (level + 1 >= line.length) "" else line.substring(level + 1)
            return HeaderElement(TextElement.parse(text), level)
        }

        fun matches(line: String): Boolean {
            if (line.isEmpty())
                return false

            if (line.first() != '#')
                return false

            val level = line.takeWhile { it == '#' }.length
            if (level > 6)
                return false

            if (level >= line.length || line[level] != ' ')
                return false

            return true
        }
    }
}