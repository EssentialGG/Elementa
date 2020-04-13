package club.sk1er.elementa.svg.data

import org.dom4j.Element
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

data class SVGLine(val x1: Float, val y1: Float, val x2: Float, val y2: Float) : SVGElement() {
    override fun getVertexCount(): Int {
        return 2
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        val (startX, startY) = attributes.modify(x1, y1)
        buffer.put(startX)
        buffer.put(startY)

        val (endX, endY) = attributes.modify(x2, y2)
        buffer.put(endX)
        buffer.put(endY)

        return GL11.GL_LINES
    }

    companion object {
        fun from(element: Element): SVGLine {
            return SVGLine(
                element.attributeValue("x1").toFloat(),
                element.attributeValue("y1").toFloat(),
                element.attributeValue("x2").toFloat(),
                element.attributeValue("y2").toFloat()
            )
        }
    }
}