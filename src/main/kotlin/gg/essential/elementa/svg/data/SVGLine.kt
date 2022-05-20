package gg.essential.elementa.svg.data

import gg.essential.elementa.impl.dom4j.Element
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

data class SVGLine(val point1: Point, val point2: Point) : SVGElement() {
    override fun getVertexCount(): Int {
        return 2
    }

    override fun applyAttributes() {
        attributes.modify(point1)
        attributes.modify(point2)
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        buffer.put(point1.x)
        buffer.put(point1.y)

        buffer.put(point2.x)
        buffer.put(point2.y)

        return GL11.GL_LINES
    }

    companion object {
        internal fun from(element: Element): SVGLine {
            return SVGLine(
                Point(element.attributeValue("x1").toFloat(), element.attributeValue("y1").toFloat()),
                Point(element.attributeValue("x2").toFloat(), element.attributeValue("y2").toFloat())
            )
        }
    }
}