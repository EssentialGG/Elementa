package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownComponent
import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.elementa.utils.withAlpha
import java.awt.Color
import kotlin.math.floor

class ParagraphDrawable(
    config: MarkdownConfig,
    texts: DrawableList
) : Drawable(config) {
    private var texts = texts.toMutableList()

    // Used by HeaderDrawable
    var scaleModifier = 1f
        set(value) {
            field = value
            texts.filterIsInstance<TextDrawable>().forEach {
                it.scaleModifier = value
            }
        }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        val marginTop = if (insertSpaceBefore) config.paragraphConfig.spaceBefore else 0f
        val marginBottom = if (insertSpaceAfter) config.paragraphConfig.spaceAfter else 0f

        var currX = x
        var currY = y + marginTop
        var widthRemaining = width
        val centered = config.paragraphConfig.centered
        var startOfLine = true

        val lines = mutableListOf<List<TextDrawable>>()
        val currentLine = mutableListOf<TextDrawable>()

        fun gotoNextLine() {
            currX = x
            currY += 9f * scaleModifier + config.paragraphConfig.spaceBetweenLines
            widthRemaining = width
            lines.add(currentLine.toList())
            currentLine.clear()
            startOfLine = true
        }

        fun layout(text: TextDrawable, width: Float) {
            val newWidth = if (startOfLine) {
                text.ensureTrimmed()
                text.width()
            } else width
            text.layout(currX, currY, newWidth)
            widthRemaining -= newWidth
            currX += newWidth
            startOfLine = false
            currentLine.add(text)
        }

        for (text in texts) {
            if (text is SoftBreakDrawable) {
                val spaceWidth = ' '.width(scaleModifier)
                widthRemaining -= spaceWidth
                currX += spaceWidth
                if (widthRemaining <= 0)
                    gotoNextLine()
                continue
            }

            if (text is HardBreakDrawable) {
                gotoNextLine()
                continue
            }

            if (text !is TextDrawable)
                TODO()

            var target: TextDrawable = text

            while (true) {
                val targetWidth = target.width()
                if (targetWidth <= widthRemaining) {
                    layout(target, targetWidth)
                    if (widthRemaining <= 0)
                        gotoNextLine()
                    break
                }

                val splitResult = target.split(widthRemaining)
                if (splitResult != null) {
                    layout(splitResult.first, targetWidth)
                    gotoNextLine()
                    target = splitResult.second
                    continue
                }

                // If we can't split the text in a way that doesn't break
                // a word, we'll just draw the whole thing on the next line.
                // Before we do that though, we have to make sure that its
                // width isn't greater than the width of the entire component.
                // If it is, we need to split it on the overall width and
                // continue this splitting loop
                gotoNextLine()

                if (targetWidth > width) {
                    val splitResult2 = target.split(width)

                    if (splitResult2 == null) {
                        // Edge case where the width of the MarkdownComponent is
                        // probably very small, and we can't split it on a word
                        // boundary. In this case we opt to split again, breaking
                        // words if we have to. We run split twice here, but as
                        // this is a rare edge case, it's not a problem.
                        val splitResult3 = target.split(width, breakWords = true)
                            ?: throw IllegalStateException("not possible")

                        layout(splitResult3.first, splitResult3.first.width())
                        gotoNextLine()
                        target = splitResult3.second
                        continue
                    }

                    layout(splitResult2.first, splitResult2.first.width())
                    gotoNextLine()
                    target = splitResult2.second
                    continue
                }

                layout(target, targetWidth)
                break
            }
        }

        if (currentLine.isNotEmpty())
            lines.add(currentLine.toList())

        if (centered) {
            for (line in lines) {
                val totalWidth = line.sumByDouble { it.width().toDouble() }.toFloat()
                val shift = (width - totalWidth) / 2f
                for (text in line) {
                    text.x += shift
                }
            }
        }

        texts = lines.flatten().toMutableList()

        val height = currY - y + 9f * scaleModifier + if (insertSpaceAfter) {
            config.paragraphConfig.spaceAfter
        } else 0f

        return Layout(
            x,
            y,
            width,
            height,
            Margin(0f, marginTop, 0f, marginBottom)
        )
    }

    override fun draw(state: DrawState) {
        texts.forEach { it.draw(state) }

        if (MarkdownComponent.DEBUG) {
            UIBlock.drawBlockSized(
                rc,
                layout.elementLeft.toDouble() + state.xShift,
                layout.elementTop.toDouble() + state.yShift,
                layout.elementWidth.toDouble(),
                layout.elementHeight.toDouble()
            )
        }
    }

    private val rc = randomColor().withAlpha(100)

    private fun randomColor(): Color {
        return Color(randomComponent(), randomComponent(), randomComponent())
    }

    private fun randomComponent(): Int = floor(Math.random() * 256f).toInt()

}
