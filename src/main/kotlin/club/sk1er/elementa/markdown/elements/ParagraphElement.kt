package club.sk1er.elementa.markdown.elements

import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.elementa.markdown.MarkdownState

class ParagraphElement private constructor(private val textElement: TextElement) : Element() {
    override fun onClick(mouseX: Float, mouseY: Float) {
        textElement.onClick(mouseX, mouseY)
    }

    override fun draw(state: MarkdownState) {
        textElement.draw(state)
    }

    companion object {
        fun parse(lines: MutableList<String>, config: MarkdownConfig): ParagraphElement? {
            var consumed = false
            val builder = StringBuilder()

            while (true) {
                if (lines.isEmpty())
                    break

                val line = lines[0]
                if (line.isBlank())
                    break

                if (config.blockquoteConfig.enabled && BlockquoteElement.matches(line))
                    break

                if (config.headerConfig.enabled && HeaderElement.matches(line))
                    break

                if (config.listConfig.enabled && ListElement.matches(line))
                    break

                if (config.codeblockConfig.enabled && CodeblockElement.matches(lines))
                    break

                if (consumed)
                    builder.append('\n')
                builder.append(line)
                lines.removeAt(0)
                consumed = true
            }

            if (!consumed)
                return null

            return ParagraphElement(TextElement.parse(builder.toString(), config))
        }
    }
}
