package gg.essential.elementa.svg.data

import gg.essential.elementa.impl.dom4j.Element
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

    override fun drawSmoothPoints() = false

    companion object {
        const val VERTEX_COUNT = 25

        internal fun from(element: Element): SVGCircle {
            return SVGCircle(
                element.attributeValue("cx").toFloat(),
                element.attributeValue("cy").toFloat(),
                element.attributeValue("r").toFloat()
            )
        }
    }
}