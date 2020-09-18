package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.SuperConstraint

class ConstraintVisitor(
    private val graph: DirectedAcyclicGraph<ResolverNode>,
    val component: UIComponent
) {
    private lateinit var currentConstraint: SuperConstraint<*>

    fun setConstraint(constraint: SuperConstraint<*>) {
        currentConstraint = constraint
    }

    fun visitParent(type: ConstraintType) {
        if (!component.hasParent || component is Window || component.parent is Window)
            return

        graph.addEdge(
            ResolverNode(component, currentConstraint),
            ResolverNode(component.parent, component.parent.constraints.getConstraint(type))
        )
    }

    fun visitSelf(type: ConstraintType) {
        graph.addEdge(
            ResolverNode(component, currentConstraint),
            ResolverNode(component, component.constraints.getConstraint(type))
        )
    }

    fun visitSibling(type: ConstraintType, index: Int) {
        if (!component.hasParent) {
            throw IllegalStateException("""
                Error during Elementa constraint validation: the current component has no parent,
                but visitSibling was called. This shouldn't be possible -- if you are seeing this,
                please notify an Elementa developer ASAP!
            """.trimIndent())
        }

        val sibling = component.parent.children[index]

        graph.addEdge(
            ResolverNode(component, currentConstraint),
            ResolverNode(sibling, sibling.constraints.getConstraint(type))
        )
    }

    fun visitChildren(type: ConstraintType) {
        val currNode = ResolverNode(component, currentConstraint)

        component.children.forEach {
            graph.addEdge(
                currNode,
                ResolverNode(it, it.constraints.getConstraint(type))
            )
        }
    }
}
