package gg.essential.elementa.svg.data

import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import kotlin.math.pow

sealed class SVGCurve(private val steps: Int) : SVGElement() {
    abstract val lastControlPoint: Point

    private val points by lazy {
        (0 until steps).map {
            it.toFloat() / steps
        }.map(::getPoint)
    }

    override fun getVertexCount() = steps

    override fun createBuffer(buffer: FloatBuffer): Int {
        points.forEach {
            buffer.put(it.x)
            buffer.put(it.y)
        }

        return GL11.GL_LINES
    }

    abstract fun getPoint(percent: Float): Point
}

class SVGQuadraticCurve(
    private val start: Point,
    private val control: Point,
    private val end: Point,
    steps: Int = 100
) : SVGCurve(steps) {
    override val lastControlPoint = control

    override fun getPoint(percent: Float): Point {
        val percentOp = 1f - percent
        return percentOp.pow(2) * start + 2f * percent * percentOp * control + percent.pow(2) * end
    }
}

class SVGCubicCurve(
    private val start: Point,
    private val control1: Point,
    private val control2: Point,
    private val end: Point,
    steps: Int = 100
) : SVGCurve(steps) {
    override val lastControlPoint = control2

    override fun getPoint(percent: Float): Point {
        val percentOp = 1f - percent
        return percentOp.pow(3) * start + 3f * percent * percentOp.pow(2) * control1 +
            3f * percent.pow(2) * percentOp * control2 + percent.pow(3) * end
    }
}
