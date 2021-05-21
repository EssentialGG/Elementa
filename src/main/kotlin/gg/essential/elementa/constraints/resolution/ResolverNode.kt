package gg.essential.elementa.constraints.resolution

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.SuperConstraint

data class ResolverNode(
    val component: UIComponent,
    val constraint: SuperConstraint<*>,
    val constraintType: ConstraintType
)