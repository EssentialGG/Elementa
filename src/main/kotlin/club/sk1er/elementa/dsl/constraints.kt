package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.*

infix fun SuperConstraint<Float>.min(minConstraint: SuperConstraint<Float>) =
    MinConstraint(this, minConstraint)

infix fun SuperConstraint<Float>.max(minConstraint: SuperConstraint<Float>) =
    MaxConstraint(this, minConstraint)

fun SuperConstraint<Float>.minMax(minConstraint: SuperConstraint<Float>, maxConstraint: SuperConstraint<Float>) =
    MaxConstraint(MinConstraint(this, minConstraint), maxConstraint)

operator fun SuperConstraint<Float>.plus(other: SuperConstraint<Float>) =
    AdditiveConstraint(this, other)

operator fun SuperConstraint<Float>.minus(other: SuperConstraint<Float>) =
    SubtractiveConstraint(this, other)