package gg.essential.elementa.svg.data

import gg.essential.elementa.impl.dom4j.Element
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

data class SVGPolyline(val points: List<Point>) : SVGElement() {
    override fun getVertexCount(): Int {
        return points.size
    }

    override fun applyAttributes() {
        points.forEach(attributes::modify)
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        points.forEach {
            buffer.put(it.x)
            buffer.put(it.y)
        }

        return GL11.GL_LINE_STRIP
    }

    companion object {
        internal fun from(element: Element): SVGPolyline {
            val points = element.attributeValue("points")
                .split(" ")
                .zipWithNext()
                .map { Point(it.first.toFloat(), it.second.toFloat()) }
                .filterIndexed { index, _ -> index % 2 == 0 }

            return SVGPolyline(points)
        }
    }
}