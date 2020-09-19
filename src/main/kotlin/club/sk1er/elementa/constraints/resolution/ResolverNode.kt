package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.SuperConstraint

data class ResolverNode(
    val component: UIComponent,
    val constraint: SuperConstraint<*>,
    val constraintType: ConstraintType
)