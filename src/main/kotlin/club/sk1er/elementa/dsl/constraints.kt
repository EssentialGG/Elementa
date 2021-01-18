package club.sk1er.elementa.dsl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.elementa.constraints.MinConstraint
import java.awt.Color

infix fun SuperConstraint<Float>.coerceAtLeast(minConstraint: SuperConstraint<Float>) =
    CoerceAtLeastConstraint(this, minConstraint)

infix fun SuperConstraint<Float>.coerceAtMost(minConstraint: SuperConstraint<Float>) =
    CoerceAtMostConstraint(this, minConstraint)

fun SuperConstraint<Float>.coerceIn(minConstraint: SuperConstraint<Float>, maxConstraint: SuperConstraint<Float>) =
    CoerceInConstraint(this, minConstraint, maxConstraint)

operator fun SuperConstraint<Float>.plus(other: SuperConstraint<Float>) =
    AdditiveConstraint(this, other)

operator fun SuperConstraint<Float>.minus(other: SuperConstraint<Float>) =
    SubtractiveConstraint(this, other)

fun max(first: SuperConstraint<Float>, second: SuperConstraint<Float>) = MaxConstraint(first, second)

fun min(first: SuperConstraint<Float>, second: SuperConstraint<Float>) = MinConstraint(first, second)

infix fun <T, U : SuperConstraint<T>> U.boundTo(component: UIComponent) = apply { this.to(component) }
