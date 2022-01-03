package gg.essential.elementa.dsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.constraints.MinConstraint
import gg.essential.elementa.state.State
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

operator fun SuperConstraint<Float>.times(factor: Number) = ScaleConstraint(this, factor.toFloat())
operator fun SuperConstraint<Float>.times(factor: State<Number>) = ScaleConstraint(this, 0f).bindValue(factor.map { it.toFloat() })

operator fun SuperConstraint<Float>.div(factor: Number) = ScaleConstraint(this, 1f / factor.toFloat())
operator fun SuperConstraint<Float>.div(factor: State<Number>) = ScaleConstraint(this, 0f).bindValue(factor.map { 1f / it.toFloat() })

fun max(first: SuperConstraint<Float>, second: SuperConstraint<Float>) = MaxConstraint(first, second)

fun min(first: SuperConstraint<Float>, second: SuperConstraint<Float>) = MinConstraint(first, second)

infix fun <T, U : SuperConstraint<T>> U.boundTo(component: UIComponent) = apply { this.to(component) }
