package gg.essential.elementa.markdown.selection

import gg.essential.elementa.dsl.width
import gg.essential.elementa.font.DefaultFonts
import gg.essential.elementa.markdown.drawables.CodeBlockDrawable

class CodeTextCursor(target: CodeBlockDrawable, val line: Int,  val offset: Int, val numericalOffset: Int) : Cursor<CodeBlockDrawable>(target) {
    override val xBase = target.x + target.config.codeBlockConfig.leftPadding +
            target.lines[line].substring(0, offset).width(1f)
    override val yBase =
        target.let { drawable ->
            drawable.y + drawable.config.codeBlockConfig.topMargin
        } + DefaultFonts.VANILLA_FONT_RENDERER.getStringHeight("", 10f) * line
    override val height = DefaultFonts.VANILLA_FONT_RENDERER.getStringHeight("", 10f).toDouble()
    override val width: Double = 1.0

    override operator fun compareTo(other: Cursor<*>): Int {
        if (other !is CodeTextCursor) {
            return target.y.compareTo(other.target.y).let {
                if (it == 0) target.x.compareTo(other.target.x) else it
            }
        }

        if (target == other.target)
            return offset.compareTo(other.offset)

        if (target.y == other.target.y)
            return target.x.compareTo(other.target.x)

        return target.y.compareTo(other.target.y)
    }
}