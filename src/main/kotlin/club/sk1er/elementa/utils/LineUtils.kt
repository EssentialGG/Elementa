package club.sk1er.elementa.utils

import club.sk1er.elementa.components.UIPoint
import gg.essential.universal.UGraphics
import org.lwjgl.opengl.GL11
import java.awt.Color

object LineUtils {
    @JvmStatic
    fun drawLine(x1: Number, y1: Number, x2: Number, y2: Number, color: Color, width: Float) {
        UGraphics.pushMatrix()
        UGraphics.enableBlend()
        UGraphics.disableTexture2D()

        GL11.glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
        GL11.glLineWidth(width)

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2f(x1.toFloat(), y1.toFloat())
        GL11.glVertex2f(x2.toFloat(), y2.toFloat())
        GL11.glEnd()

        UGraphics.enableTexture2D()
        UGraphics.popMatrix()
    }

    @JvmStatic
    fun drawLine(p1: UIPoint, p2: UIPoint, color: Color, width: Float) {
        drawLine(p1.absoluteX, p1.absoluteY, p2.absoluteX, p2.absoluteY, color, width)
    }
}
