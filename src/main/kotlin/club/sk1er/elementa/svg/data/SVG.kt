package club.sk1er.elementa.svg.data

data class SVG(
    val elements: List<SVGElement>,
    val width: Int,
    val height: Int,
    val strokeWidth: Float
)