package club.sk1er.elementa.svg

import org.dom4j.Element
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class SVG(
    val elements: List<SVGElement>,
    val width: Int,
    val height: Int,
    val strokeWidth: Float
)

sealed class SVGElement {
    abstract fun getVertexCount(): Int

    abstract fun createBuffer(buffer: FloatBuffer): Int
}

data class SVGCircle(val cx: Float, val cy: Float, val r: Float) : SVGElement() {
    override fun getVertexCount(): Int {
        return VERTEX_COUNT
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        val vertices = VERTEX_COUNT - 1
        val doublePi = 2f * PI.toFloat()

        for (i in 0..vertices) {
            buffer.put(cx + (r * cos(i * doublePi / vertices)))
            buffer.put(cy + (r * sin(i * doublePi / vertices)))
        }

        return GL11.GL_LINE_LOOP
    }

    companion object {
        const val VERTEX_COUNT = 20

        fun from(element: Element): SVGCircle {
            return SVGCircle(
                element.attributeValue("cx").toFloat(),
                element.attributeValue("cy").toFloat(),
                element.attributeValue("r").toFloat()
            )
        }
    }
}

data class SVGLine(val x1: Float, val y1: Float, val x2: Float, val y2: Float) : SVGElement() {
    override fun getVertexCount(): Int {
        return 2
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        buffer.put(x1)
        buffer.put(y1)

        buffer.put(x2)
        buffer.put(y2)

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