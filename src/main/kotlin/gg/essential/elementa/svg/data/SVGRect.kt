package gg.essential.elementa.svg.data

import gg.essential.elementa.impl.dom4j.Element
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

class SVGRect(
    private val topLeft: Point,
    private val topRight: Point,
    private val bottomRight: Point,
    private val bottomLeft: Point
) : SVGElement() {
    override fun getVertexCount(): Int {
        return 4
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        buffer.put(bottomLeft.x)
        buffer.put(bottomLeft.y)

        buffer.put(bottomRight.x)
        buffer.put(bottomRight.y)

        buffer.put(topRight.x)
        buffer.put(topRight.y)

        buffer.put(topLeft.x)
        buffer.put(topLeft.y)

        return GL11.GL_LINE_LOOP
    }

    companion object {
        internal fun from(element: Element): SVGRect {
            val topLeft = Point(element.attributeValue("x").toFloat(), element.attributeValue("y").toFloat())
            val width = element.attributeValue("width").toFloat()
            val height = element.attributeValue("height").toFloat()

            return SVGRect(
                topLeft,
                topLeft.copy(x = topLeft.x + width),
                topLeft.copy(x = topLeft.x +  width, y = topLeft.y + height),
                topLeft.copy(y = topLeft.y + height)
            )
        }
    }
}