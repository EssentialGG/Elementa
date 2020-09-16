package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.mods.core.universal.UniversalMinecraft

class ConstraintResolver(private val window: Window) {
    fun resolve() {
        ConstraintType.values().forEach { resolve(it) }
    }

    private fun resolve(constraintType: ConstraintType) {
        window.forEachChild { node ->
            val visitor = ConstraintVisitor(node)
            node.constraints.getConstraint(constraintType).visit(visitor, constraintType)

            if (visitor.isInvalid)
                throw ConstraintValidationException(visitor.constraintHistory())
        }
    }
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
        if (component !is Window)
            action(component)
        component.children.forEach(::helper)
    }

    helper(this)
}
