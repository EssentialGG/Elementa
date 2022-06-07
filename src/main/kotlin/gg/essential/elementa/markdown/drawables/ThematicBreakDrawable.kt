package gg.essential.elementa.markdown.drawables

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.markdown.DrawState
import gg.essential.elementa.markdown.MarkdownComponent
import gg.essential.elementa.markdown.selection.Cursor
import gg.essential.universal.UMatrixStack

internal class ThematicBreakDrawable(md: MarkdownComponent) : Drawable(md) {
    private val headerConfig = md.config.headerConfig.level1

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        return Layout(
            x,
            y,
            width,
            headerConfig
                .let {
                    it.spaceBeforeDivider + it.dividerWidth + it.verticalSpaceAfter
                },
            Margin(0f, 0f, 0f, 0f))
    }

    override fun draw(matrixStack: UMatrixStack, state: DrawState) {
        UIBlock.drawBlockSized(
            matrixStack,
            headerConfig.dividerColor,
            (x + state.xShift).toDouble(),
            (y + headerConfig.spaceBeforeDivider + state.yShift).toDouble(),
            width.toDouble(),
            headerConfig.dividerWidth.toDouble()
        )
    }

    override fun cursorAt(mouseX: Float, mouseY: Float, dragged: Boolean, mouseButton: Int): Cursor<*> {
        return if (mouseX >= width / 2) cursorAtEnd() else cursorAtStart()
    }

    override fun cursorAtStart(): Cursor<*> {
        return object : Cursor<ThematicBreakDrawable>(this) {
            override val xBase: Float = x

            override fun compareTo(other: Cursor<*>): Int {
                if (other.target !is ThematicBreakDrawable) {
                    return target.y.compareTo(other.target.y).let {
                        if (it == 0) target.x.compareTo(other.target.x) else it
                    }
                }

                if (target == other.target)
                    return 0

                if (target.y == other.target.y)
                    return target.x.compareTo(other.target.x)

                return target.y.compareTo(other.target.y)
            }
        }
    }

    override fun cursorAtEnd(): Cursor<*> {
        return object : Cursor<ThematicBreakDrawable>(this) {
            override val xBase: Float = x + this@ThematicBreakDrawable.width

            override fun compareTo(other: Cursor<*>): Int {
                if (other.target !is ThematicBreakDrawable) {
                    return target.y.compareTo(other.target.y).let {
                        if (it == 0) target.x.compareTo(other.target.x) else it
                    }
                }

                if (target == other.target)
                    return 0

                if (target.y == other.target.y)
                    return target.x.compareTo(other.target.x)

                return target.y.compareTo(other.target.y)
            }
        }
    }

    override fun selectedText(asMarkdown: Boolean): String {
        return ""
    }
}