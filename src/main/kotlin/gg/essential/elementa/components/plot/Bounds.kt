package gg.essential.elementa.components.plot

import java.awt.Color
import kotlin.math.abs
import kotlin.math.round

data class PlotPoint(val x: Float, val y: Float) {
    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())
}

class Bounds(
    min: Number,
    max: Number,
    val numberOfGridLines: Int,
    val showLabels: Boolean = false,
    val labelColor: Color = Color.WHITE,
    val labelToString: (Float) -> String = {
        if (abs(it - round(it)) <= 0.00001) {
            round(it).toInt().toString()
        } else "%.1f".format(it)
    }
) {
    val min = min.toFloat()
    val max = max.toFloat()

    val range: Float
        get() = max - min

    companion object {
        fun fromPoints(points: List<Float>, numberOfGridLines: Int = 5) =
            Bounds(points.minOrNull()!!, points.maxOrNull()!!, numberOfGridLines)
    }
}