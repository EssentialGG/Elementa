package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.PositionConstraint
import gg.essential.elementa.dsl.pixels

class UIPoint(
    val x: PositionConstraint,
    val y: PositionConstraint
) : UIComponent() {
    val relativeX: Float
        get() = constraints.getX()

    val relativeY: Float
        get() = constraints.getY()

    val absoluteX: Float
        get() = getLeft()

    val absoluteY: Float
        get() = getTop()

    val point: Pair<Float, Float>
        get() = relativeX to relativeY

    val absolutePoint: Pair<Float, Float>
        get() = absoluteX to absoluteY

    init {
        setX(x)
        setY(y)
    }

    constructor(x: Number, y: Number) : this(x.pixels(), y.pixels())

    constructor(point: Pair<Number, Number>) : this(point.first.pixels(), point.second.pixels())

    fun withX(x: PositionConstraint) = UIPoint(x, y)

    fun withX(x: Number) = UIPoint(x.pixels(), y)

    fun withY(y: PositionConstraint) = UIPoint(x, y)

    fun withY(y: Number) = UIPoint(x, y.pixels())
}