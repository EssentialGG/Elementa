package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.*

fun SuperConstraint<Float>.min(minConstraint: SuperConstraint<Float>) =
    MinConstraint(this, minConstraint)

fun SuperConstraint<Float>.minMax(minConstraint: SuperConstraint<Float>, maxConstraint: SuperConstraint<Float>) =
    MaxConstraint(MinConstraint(this, minConstraint), maxConstraint)

operator fun SuperConstraint<Float>.plus(other: SuperConstraint<Float>) =
    AdditiveConstraint(this, other)

operator fun SuperConstraint<Float>.minus(other: SuperConstraint<Float>) =
    SubtractiveConstraint(this, other)