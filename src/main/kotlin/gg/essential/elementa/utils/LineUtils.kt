package gg.essential.elementa.utils

import gg.essential.elementa.components.UIPoint
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.sqrt

object LineUtils {
    @Deprecated(UMatrixStack.Compat.DEPRECATED, ReplaceWith("drawLine(UMatrixStack(), x1, y1, x2, y2, color, width)"))
    @JvmStatic
    fun drawLine(x1: Number, y1: Number, x2: Number, y2: Number, color: Color, width: Float) =
        drawLine(UMatrixStack(), x1, y1, x2, y2, color, width)

    @JvmStatic
    fun drawLine(matrixStack: UMatrixStack, x1: Number, y1: Number, x2: Number, y2: Number, color: Color, width: Float) {
        drawLineStrip(matrixStack, listOf(x1 to y1, x2 to y2), color, width)
    }

    @JvmStatic
    fun drawLineStrip(matrixStack: UMatrixStack, points: List<Pair<Number, Number>>, color: Color, width: Float) {
        UGraphics.enableBlend()

        val buffer = UGraphics.getFromTessellator()
        buffer.beginWithDefaultShader(UGraphics.DrawMode.TRIANGLE_STRIP, UGraphics.CommonVertexFormats.POSITION_COLOR);
        points.forEachIndexed { index, curr ->
            val (x, y) = curr
            val prev = points.getOrNull(index - 1)
            val next = points.getOrNull(index + 1)
            val (dx, dy) = when {
                prev == null -> next!!.sub(curr)
                next == null -> curr.sub(prev)
                else -> next.sub(prev)
            }
            val dLen = sqrt(dx * dx + dy * dy)
            val nx = dx / dLen * width / 2
            val ny = dy / dLen * width / 2
            buffer.pos(matrixStack, x.toDouble() + ny, y.toDouble() - nx, 0.0)
                .color(color.red, color.green, color.blue, color.alpha)
                .endVertex()
            buffer.pos(matrixStack, x.toDouble() - ny, y.toDouble() + nx, 0.0)
                .color(color.red, color.green, color.blue, color.alpha)
                .endVertex()
        }
        buffer.drawDirect()
    }

    private fun Pair<Number, Number>.sub(other: Pair<Number, Number>): Pair<Double, Double> {
        val (x1, y1) = this
        val (x2, y2) = other
        return Pair(x1.toDouble() - x2.toDouble(), y1.toDouble() - y2.toDouble())
    }

    @Deprecated(UMatrixStack.Compat.DEPRECATED, ReplaceWith("drawLine(UMatrixStack(), p1, p1, color, width)"))
    @JvmStatic
    fun drawLine(p1: UIPoint, p2: UIPoint, color: Color, width: Float) =
        drawLine(UMatrixStack(), p1, p2, color, width)

    @JvmStatic
    fun drawLine(matrixStack: UMatrixStack, p1: UIPoint, p2: UIPoint, color: Color, width: Float) {
        drawLine(matrixStack, p1.absoluteX, p1.absoluteY, p2.absoluteX, p2.absoluteY, color, width)
    }
}
