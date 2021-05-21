package gg.essential.elementa.components.plot

import java.awt.Color

data class PlotStyle(
    val pointStyle: PointStyle = PointStyle(),
    val lineStyle: LineStyle = LineStyle(),
    val gridStyle: GridStyle = GridStyle(),
    val padding: Padding = Padding(10)
)

class Padding(
    left: Number,
    top: Number,
    right: Number,
    bottom: Number
) {
    val left = left.toFloat()
    val top = top.toFloat()
    val right = right.toFloat()
    val bottom = bottom.toFloat()

    constructor(horizontal: Number, vertical: Number) : this(horizontal, vertical, horizontal, vertical)

    constructor(all: Number) : this(all, all, all, all)
}

data class PointStyle(
    val color: Color = Color.WHITE,
    val radius: Float = 2f,
    val type: PointType = PointType.None
)

data class LineStyle(
    val color: Color = Color.WHITE,
    val width: Float = 2f,
    val type: LineType = LineType.Linear
)

data class GridStyle(
    val color: Color = Color(80, 80, 80),
    val width: Float = 2f,
    val type: LineType = LineType.Linear
)