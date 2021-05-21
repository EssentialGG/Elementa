package gg.essential.elementa.svg.data

data class Point(var x: Float, var y: Float) {
    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())

    infix fun reflectedAround(other: Point): Point {
        return Point(
            2f * other.x - x,
            2f * other.y - y
        )
    }

    operator fun plus(num: Number) = Point(x + num.toFloat(), y + num.toFloat())
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    operator fun minus(num: Number) = Point(x - num.toFloat(), y - num.toFloat())
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun times(num: Number) = Point(x * num.toFloat(), y * num.toFloat())

    operator fun div(num: Number) = Point(x / num.toFloat(), y / num.toFloat())
}

infix operator fun <T : Number> T.plus(point: Point) = point + this
infix operator fun <T : Number> T.minus(point: Point) = point - this
infix operator fun <T : Number> T.times(point: Point) = point * this
