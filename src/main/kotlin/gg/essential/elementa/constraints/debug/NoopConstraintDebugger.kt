package gg.essential.elementa.constraints.debug

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.SuperConstraint

internal class NoopConstraintDebugger : ConstraintDebugger {
    override fun evaluate(constraint: SuperConstraint<Float>, type: ConstraintType, component: UIComponent): Float {
        if (constraint.recalculate) {
            constraint.cachedValue = invokeImpl(constraint, type, component)
            constraint.recalculate = false
        }

        return constraint.cachedValue
    }
}
