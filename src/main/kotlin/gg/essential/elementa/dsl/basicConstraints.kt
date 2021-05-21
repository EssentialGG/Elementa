package gg.essential.elementa.dsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import java.awt.Color

abstract class BasicConstraint<T>(defaultValue: T) : SuperConstraint<T> {
    override var cachedValue = defaultValue
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}
}

fun basicXConstraint(calculator: (component: UIComponent) -> Float): XConstraint =
    object : BasicConstraint<Float>(0f), XConstraint {
        override fun getXPositionImpl(component: UIComponent) = calculator(component)
    }

fun basicYConstraint(calculator: (component: UIComponent) -> Float): YConstraint =
    object : BasicConstraint<Float>(0f), YConstraint {
        override fun getYPositionImpl(component: UIComponent) = calculator(component)
    }

fun basicWidthConstraint(calculator: (component: UIComponent) -> Float): WidthConstraint =
    object : BasicConstraint<Float>(0f), WidthConstraint {
        override fun getWidthImpl(component: UIComponent) = calculator(component)
    }

fun basicHeightConstraint(calculator: (component: UIComponent) -> Float): HeightConstraint =
    object : BasicConstraint<Float>(0f), HeightConstraint {
        override fun getHeightImpl(component: UIComponent) = calculator(component)
    }

fun basicRadiusConstraint(calculator: (component: UIComponent) -> Float): RadiusConstraint =
    object : BasicConstraint<Float>(0f), RadiusConstraint {
        override fun getRadiusImpl(component: UIComponent) = calculator(component)
    }

fun basicTextScaleConstraint(calculator: (component: UIComponent) -> Float) = basicHeightConstraint(calculator)

fun basicColorConstraint(calculator: (component: UIComponent) -> Color): ColorConstraint =
    object : BasicConstraint<Color>(Color.WHITE), ColorConstraint {
        override fun getColorImpl(component: UIComponent) = calculator(component)
    }
