package club.sk1er.elementa.dsl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import java.awt.Color

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

fun basicWidthConstraint(calculator: (component: UIComponent) -> Float) = object : WidthConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent) = calculator(component)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

fun basicXConstraint(calculator: (component: UIComponent) -> Float) = object : XConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent) = calculator(component)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

fun basicYConstraint(calculator: (component: UIComponent) -> Float) = object : YConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getYPositionImpl(component: UIComponent) = calculator(component)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

fun basicHeightConstraint(calculator: (component: UIComponent) -> Float) = object : HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getHeightImpl(component: UIComponent) = calculator(component)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

fun basicRadiusConstraint(calculator: (component: UIComponent) -> Float) = object : RadiusConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getRadiusImpl(component: UIComponent) = calculator(component)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

fun basicTextScaleConstraint(calculator: (component: UIComponent) -> Float) = basicHeightConstraint(calculator)

fun basicColorConstraint(calculator: (component: UIComponent) -> Color) = object : ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getColorImpl(component: UIComponent) = calculator(component)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}
