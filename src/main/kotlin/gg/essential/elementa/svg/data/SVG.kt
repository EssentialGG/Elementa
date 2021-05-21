package gg.essential.elementa.svg.data

data class SVG(
    val elements: List<SVGElement>,
    val width: Float?,
    val height: Float?,
    val strokeWidth: Float,
    val roundLineCaps: Boolean,
    val roundLineJoins: Boolean
)