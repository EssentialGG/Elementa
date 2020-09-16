package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.SuperConstraint


class ConstraintVisitor(var component: UIComponent) {
    val parent: UIComponent?
        get() = if (component.hasParent) component.parent else null

    val constraints: UIConstraints
        get() = component.constraints

    var isInvalid = false
        private set

    private var seenConstraints = mutableListOf<SuperConstraint<*>>()

    fun constraintHistory(): List<SuperConstraint<*>> = seenConstraints

    fun visit(constraint: SuperConstraint<*>) {
        if (isInvalid)
            return

        isInvalid = seenConstraints.contains(constraint)
        seenConstraints.add(constraint)
    }

    fun visitParent(type: ConstraintType) {
        if (isInvalid)
            return

        if (parent == null || parent is Window)
            return

        withComponent(parent!!) {
            visitSelf(type)
        }
    }

    fun visitSelf(type: ConstraintType) {
        if (isInvalid)
            return

        val previousSeenConstraints = seenConstraints.toMutableList()
        constraints.getConstraint(type).visit(this, type)
        seenConstraints = previousSeenConstraints
    }

    fun visitSibling(type: ConstraintType, index: Int) {
        if (isInvalid)
            return

        if (parent == null) {
            // TODO
            assert(false)
        }

        withComponent(parent!!.children[index]) {
            visitSelf(type)
        }
    }

    fun visitChildren(type: ConstraintType) {
        if (isInvalid)
            return

        component.children.forEach {
            withComponent(it) { visitSelf(type) }
        }
    }


    private fun withComponent(newComponent: UIComponent, action: () -> Unit) {
        val prevComponent = component
        component = newComponent
        action()
        component = prevComponent
    }
}
