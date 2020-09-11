package club.sk1er.elementa.components.graph

enum class PointType(private val drawFunc: (List<GraphPoint>, GraphStyle) -> Unit) {
    None({ _, _ -> });

    fun draw(points: List<GraphPoint>, style: GraphStyle) {
        drawFunc(points, style)
    }
}