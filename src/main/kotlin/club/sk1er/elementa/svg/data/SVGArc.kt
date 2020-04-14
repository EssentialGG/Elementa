package club.sk1er.elementa.svg.data

import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import kotlin.math.*

data class SVGArc(
    val startPoint: Point,
    val radiusX: Float,
    val radiusY: Float,
    val xAxisRotation: Int,
    val largeArc: Boolean,
    val sweep: Boolean,
    val endPoint: Point
) : SVGElement() {
    private var cx: Double
    private var cy: Double
    private var startTheta: Double
    private var thetaDifference: Double

    init {
        // Adapted from https://www.w3.org/TR/SVG2/implnote.html#ArcImplementationNotes

        val xRotRad = Math.toRadians(xAxisRotation.toDouble())

        val cosX = cos(xRotRad)
        val sinX = sin(xRotRad)

        val x1 = cosX * ((startPoint.x - endPoint.x) / 2) + sinX * ((startPoint.y - endPoint.y) / 2)
        val y1 = -sinX * ((startPoint.x - endPoint.x) / 2) + cosX * ((startPoint.y - endPoint.y) / 2)

        val rx2 = radiusX * radiusX
        val ry2 = radiusY * radiusY
        val rxy = (rx2 * (y1 * y1))
        val ryx = (ry2 * (x1 * x1))
        val sqrt = sqrt(((rx2 * ry2) - rxy - ryx) / (rxy + ryx))

        val sign = if (largeArc == sweep) -1 else 1

        val cxD = (sqrt * ((radiusX * y1) / radiusY)).withSign(sign)
        val cyD = (sqrt * -((radiusY * x1) / radiusX)).withSign(sign)

        cx = (cosX * cxD) + (-sinX * cyD) + ((startPoint.x + endPoint.x) / 2)
        cy = (sinX * cxD) + (cosX * cyD) + ((startPoint.y + endPoint.y) / 2)

        val xDiv = (x1 - cxD) / radiusX
        val yDiv = (y1 - cyD) / radiusY

        val theta1 = angle(
            1.0,
            0.0,
            xDiv,
            yDiv
        )
        var thetaD = angle(
            xDiv,
            yDiv,
            (-x1 - cxD) / radiusX,
            (-y1 - cyD) / radiusY
        ) % 360

//        if (sweep && thetaD < 0) {
//            thetaD += 360
//        } else if (!sweep && thetaD > 0) {
//            thetaD -= 360
//        }

//        if (cx < 0) cx++
//        else cx--

        startTheta = theta1
        thetaDifference = thetaD
    }

    override fun getVertexCount(): Int {
        return ceil((abs(thetaDifference) / 360) * SVGCircle.VERTEX_COUNT).toInt() + 1
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        val numVertices = getVertexCount()
        val thetaPerVertex = thetaDifference / (numVertices - 1)

        var currTheta = startTheta
        repeat(numVertices) {
            val currRad = Math.toRadians(currTheta)
            buffer.put((cx + (radiusX * cos(currRad))).toFloat())
            buffer.put((cy + (radiusY * sin(currRad))).toFloat())

            currTheta += thetaPerVertex
        }

        return GL11.GL_LINE_STRIP
    }

    private fun angle(ux: Double, uy: Double, vx: Double, vy: Double): Double {
        val magU = sqrt(ux * ux + uy * uy).let { if (it == 0.0) 1.0 else it }
        val magV = sqrt(vx * vx + vy * vy).let { if (it == 0.0) 1.0 else it }
        val dot = ux * vx + uy * vy

        val acos = acos(dot / (magU * magV))
        val sign = sign(ux * vy - uy * vx)

        return Math.toDegrees(acos).withSign(sign)
    }

    override fun drawSmoothPoints() = false
}