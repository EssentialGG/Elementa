package gg.essential.elementa.components.plot

enum class PointType(private val drawFunc: (List<PlotPoint>, PlotStyle) -> Unit) {
    None({ _, _ -> });

    fun draw(points: List<PlotPoint>, style: PlotStyle) {
        drawFunc(points, style)
    }
}