package club.sk1er.elementa.svg.data

import kotlin.math.cos
import kotlin.math.sin

data class SVGAttributes(
    var strokeWidth: Float? = null,
    var transform: Transform? = null
) {
    fun modify(pointX: Float, pointY: Float): Pair<Float, Float> {
        return transform?.modify(pointX, pointY) ?: pointX to pointY
    }
}

data class Transform(
    var rotation: Rotation? = null
) {
    fun modify(pointX: Float, pointY: Float): Pair<Float, Float> {
        return rotation?.rotate(pointX, pointY) ?: pointX to pointY
    }
}

data class Rotation(val angle: Int, val originX: Float?, val originY: Float?) {
    fun rotate(
        pointX: Float,
        pointY: Float
    ): Pair<Float, Float> {
        val s = sin(Math.toRadians(angle.toDouble()))
        val c = cos(Math.toRadians(angle.toDouble()))

        // translate point back to origin:
        val normalX = pointX - (originX ?: 0f)
        val normalY = pointY - (originY ?: 0f)

        // rotate point
        var newX = (normalX * c - normalY * s).toFloat()
        var newY = (normalX * s + normalY * c).toFloat()

        // translate point back:
        newX += (originX ?: 0f)
        newY += (originY ?: 0f)

        return newX to newY
    }
}