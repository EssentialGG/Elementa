package gg.essential.elementa.svg.data

import kotlin.math.cos
import kotlin.math.sin

data class SVGAttributes(
    var strokeWidth: Float? = null,
    var transform: Transform? = null
) {
    fun modify(point: Point) {
        transform?.modify(point)
    }
}

data class Transform(
    var rotation: Rotation? = null
) {
    fun modify(point: Point) {
        rotation?.rotate(point)
    }
}

data class Rotation(val angle: Int, val originX: Float?, val originY: Float?) {
    fun rotate(point: Point) {
        val s = sin(Math.toRadians(angle.toDouble()))
        val c = cos(Math.toRadians(angle.toDouble()))

        // translate point back to origin:
        val normalX = point.x - (originX ?: 0f)
        val normalY = point.y - (originY ?: 0f)

        // rotate point
        var newX = (normalX * c - normalY * s).toFloat()
        var newY = (normalX * s + normalY * c).toFloat()

        // translate point back:
        newX += (originX ?: 0f)
        newY += (originY ?: 0f)

        point.x = newX
        point.y = newY
    }
}