package gg.essential.elementa.utils

import gg.essential.elementa.components.UIPoint
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Color

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
        // TODO convert to shader for 1.17
        UGraphics.enableBlend()
        UGraphics.disableTexture2D()

        GL11.glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
        GL11.glLineWidth(width)

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        matrixStack.runWithGlobalState {
            GL11.glBegin(GL11.GL_LINE_STRIP)
            points.forEach { (x, y) -> GL11.glVertex2f(x.toFloat(), y.toFloat()) }
            GL11.glEnd()
        }

        UGraphics.enableTexture2D()
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
