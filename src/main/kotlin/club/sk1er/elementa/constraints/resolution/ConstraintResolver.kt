package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*

class ConstraintResolver(window: Window) {
    private val graph = DirectedAcyclicGraph<ResolverNode>()

    init {
        window.forEachChild {
            // Color constraints are not added because they are always resolvable
            graph.addVertices(
                ResolverNode(it, it.constraints.x),
                ResolverNode(it, it.constraints.y),
                ResolverNode(it, it.constraints.width),
                ResolverNode(it, it.constraints.height),
                ResolverNode(it, it.constraints.textScale),
                ResolverNode(it, it.constraints.radius)
            )
        }

        window.forEachChild {
            val visitor = ConstraintVisitor(graph, it)

            it.constraints.x.visit(visitor, ConstraintType.X)
            it.constraints.y.visit(visitor, ConstraintType.Y)
            it.constraints.width.visit(visitor, ConstraintType.WIDTH)
            it.constraints.height.visit(visitor, ConstraintType.HEIGHT)
            it.constraints.textScale.visit(visitor, ConstraintType.TEXT_SCALE)
            it.constraints.radius.visit(visitor, ConstraintType.RADIUS)
        }
    }

    fun getCyclicNodes() = graph.getCyclicLoop()
}

fun UIConstraints.getConstraint(type: ConstraintType) = when (type) {
    ConstraintType.X -> x
    ConstraintType.Y -> y
    ConstraintType.WIDTH -> width
    ConstraintType.HEIGHT -> height
    ConstraintType.RADIUS -> radius
    ConstraintType.COLOR -> color
    ConstraintType.TEXT_SCALE -> textScale
}

fun Window.forEachChild(action: (UIComponent) -> Unit) {
    fun helper(component: UIComponent) {
        action(component)
        component.children.forEach(::helper)
    }

    helper(this)
}
