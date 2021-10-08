package gg.essential.elementa.constraints.resolution

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.SuperConstraint
import gg.essential.elementa.constraints.debug.ConstraintDebugger
import gg.essential.elementa.constraints.debug.withDebugger

internal class ConstraintResolverV2(private val window: Window) {
    fun getCyclicNodes(): List<ResolverNode>? {
        val debugger = CycleSearchingDebugger()
        withDebugger(debugger) {
            window.forEachChild {
                it.constraints.x.getXPosition(it)
                it.constraints.y.getYPosition(it)
                it.constraints.width.getWidth(it)
                it.constraints.height.getHeight(it)
                it.constraints.radius.getRadius(it)
            }
        }
        return debugger.cycles.firstOrNull()
    }
}

private class CycleSearchingDebugger : ConstraintDebugger {
    private val visited = mutableSetOf<SuperConstraint<*>>()
    private val stack = mutableSetOf<ResolverNode>()

    val cycles = mutableListOf<List<ResolverNode>>()

    override fun evaluate(constraint: SuperConstraint<Float>, type: ConstraintType, component: UIComponent): Float {
        if (visited.add(constraint)) {
            // If this is the first time we see this constraint, mark it dirty.
            // Usually this is done by the animationFrame call, but we cannot issue one of those because that may
            // advance animations and give us a different result than what we are looking for.
            constraint.recalculate = true
        }

        if (constraint.recalculate) {
            val node = ResolverNode(component, constraint, type)

            if (node in stack) {
                // Found a cycle!

                // remove the non-cyclic beginnings of the stack and store the cycle for later
                cycles.add(stack.dropWhile { it != node } + node)

                // let's just return some arbitrary value so we can continue
                return constraint.cachedValue
            }

            stack.add(node)
            constraint.cachedValue = invokeImpl(constraint, type, component)
            stack.remove(node)

            constraint.recalculate = false
        }

        return constraint.cachedValue
    }
}
