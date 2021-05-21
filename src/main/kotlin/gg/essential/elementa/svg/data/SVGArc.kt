package gg.essential.elementa.svg.data

import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import kotlin.math.*

data class SVGArc(
    val startPoint: Point,
    val rx: Float,
    val ry: Float,
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
        // and https://github.com/BigBadaboom/androidsvg/blob/ef779a3c0fcbf1ee84fed67ccede111803d870b0/androidsvg/src/main/java/com/caverock/androidsvg/SVGAndroidRenderer.java#L2613

        val angleRad = Math.toRadians(xAxisRotation % 360.0)
        val cosAngle = cos(angleRad)
        val sinAngle = sin(angleRad)

        // We simplify the calculations by transforming the arc so that the origin is at the
        // midpoint calculated above followed by a rotation to line up the coordinate axes
        // with the axes of the ellipse.

        // Compute the midpoint of the line between the current and the end point

        // We simplify the calculations by transforming the arc so that the origin is at the
        // midpoint calculated above followed by a rotation to line up the coordinate axes
        // with the axes of the ellipse.

        // Compute the midpoint of the line between the current and the end point
        val dx2 = (startPoint.x - endPoint.x) / 2.0
        val dy2 = (startPoint.y - endPoint.y) / 2.0

        // Step 1 : Compute (x1', y1')
        // x1,y1 is the midpoint vector rotated to take the arc's angle out of consideration

        // Step 1 : Compute (x1', y1')
        // x1,y1 is the midpoint vector rotated to take the arc's angle out of consideration
        val x1 = cosAngle * dx2 + sinAngle * dy2
        val y1 = -sinAngle * dx2 + cosAngle * dy2

        var radiusX = rx.toDouble()
        var radiusY = ry.toDouble()

        var squareRX = radiusX * radiusX
        var squareRY = radiusY * radiusY
        val squareX1 = x1 * x1
        val squareY1 = y1 * y1

        // Check that radii are large enough.
        // If they are not, the spec says to scale them up so they are.
        // This is to compensate for potential rounding errors/differences between SVG implementations.
        val radiiCheck = squareX1 / squareRX + squareY1 / squareRY
        if (radiiCheck > 0.99999) {
            val radiiScale = sqrt(radiiCheck) * 1.00001
            radiusX = (radiiScale * radiusX)
            radiusY = (radiiScale * radiusY)
            squareRX = radiusX * radiusX
            squareRY = radiusY * radiusY
        }

        // Step 2 : Compute (cx1, cy1) - the transformed centre point
        var sign = if (largeArc == sweep) -1.0 else 1.0
        val sq = ((squareRX * squareRY - squareRX * squareY1 - squareRY * squareX1) / (squareRX * squareY1 + squareRY * squareX1)).coerceAtLeast(0.0)
        val coefficient = sign * sqrt(sq)
        val cx1 = coefficient * (radiusX * y1 / radiusY)
        val cy1 = coefficient * -(radiusY * x1 / radiusX)

        // Step 3 : Compute (cx, cy) from (cx1, cy1)
        val sx2 = (startPoint.x + endPoint.x) / 2.0
        val sy2 = (startPoint.y + endPoint.y) / 2.0
        cx = sx2 + (cosAngle * cx1 - sinAngle * cy1)
        cy = sy2 + (sinAngle * cx1 + cosAngle * cy1)

        // Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
        val ux = (x1 - cx1) / radiusX
        val uy = (y1 - cy1) / radiusY
        val vx = (-x1 - cx1) / radiusX
        val vy = (-y1 - cy1) / radiusY

        // Angle between two vectors is +/- acos( u.v / len(u) * len(v))
        // Where '.' is the dot product. And +/- is calculated from the sign of the cross product (u x v)

        // Compute the start angle
        // The angle between (ux,uy) and the 0deg angle (1,0)
        var n = sqrt(ux * ux + uy * uy) // len(u) * len(1,0) == len(u)

        var p = ux // u.v == (ux,uy).(1,0) == (1 * ux) + (0 * uy) == ux

        sign = if (uy < 0) -1.0 else 1.0 // u x v == (1 * uy - ux * 0) == uy

        startTheta = sign * acos(p / n) // No need for checkedArcCos() here. (p >= n) should always be true.

        // Compute the angle extent
        n = sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy))
        p = ux * vx + uy * vy
        sign = if (ux * vy - uy * vx < 0) -1.0 else 1.0
        thetaDifference = sign * safeAcos(p / n)

        if (!sweep && thetaDifference > 0) {
            thetaDifference -= TWO_PI
        } else if (sweep && thetaDifference < 0) {
            thetaDifference += TWO_PI
        }

        thetaDifference %= TWO_PI
        startTheta %= TWO_PI
    }

    override fun getVertexCount(): Int {
        return ceil((abs(thetaDifference) / (TWO_PI)) * SVGCircle.VERTEX_COUNT).toInt() + 1
    }

    override fun createBuffer(buffer: FloatBuffer): Int {
        val numVertices = getVertexCount()
        val thetaPerVertex = thetaDifference / (numVertices - 1)

        var currTheta = startTheta
        repeat(numVertices) {
            buffer.put((cx + (rx * cos(currTheta))).toFloat())
            buffer.put((cy + (ry * sin(currTheta))).toFloat())

            currTheta += thetaPerVertex
        }

        return GL11.GL_LINE_STRIP
    }

    private fun safeAcos(value: Double): Double {
        return if (value < -1.0) Math.PI else if (value > 1.0) 0.0 else acos(value)
    }

    override fun drawSmoothPoints() = false

    companion object {
        private const val TWO_PI = PI * 2
    }
}