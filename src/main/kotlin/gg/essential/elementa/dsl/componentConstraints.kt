package gg.essential.elementa.dsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import java.awt.Color

abstract class ComponentConstraint<T>(defaultValue: T) : SuperConstraint<T> {
    override var cachedValue = defaultValue
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}
}

fun componentXConstraint(boundComponent: UIComponent): XConstraint =
    object : ComponentConstraint<Float>(0f), XConstraint {
        override fun getXPositionImpl(component: UIComponent) = boundComponent.getLeft() - boundComponent.parent.getLeft()
    }

fun componentYConstraint(boundComponent: UIComponent): YConstraint =
    object : ComponentConstraint<Float>(0f), YConstraint {
        override fun getYPositionImpl(component: UIComponent) = boundComponent.getTop() - boundComponent.parent.getTop()
    }

fun componentWidthConstraint(boundComponent: UIComponent): WidthConstraint =
    object : ComponentConstraint<Float>(0f), WidthConstraint {
        override fun getWidthImpl(component: UIComponent) = boundComponent.getWidth()
    }

fun componentHeightConstraint(boundComponent: UIComponent): HeightConstraint =
    object : ComponentConstraint<Float>(0f), HeightConstraint {
        override fun getHeightImpl(component: UIComponent) = boundComponent.getHeight()
    }

fun componentRadiusConstraint(boundComponent: UIComponent): RadiusConstraint =
    object : ComponentConstraint<Float>(0f), RadiusConstraint {
        override fun getRadiusImpl(component: UIComponent) = boundComponent.getRadius()
    }

fun componentTextScaleConstraint(boundComponent: UIComponent) = componentHeightConstraint(boundComponent)

fun componentColorConstraint(boundComponent: UIComponent): ColorConstraint =
    object : ComponentConstraint<Color>(Color.WHITE), ColorConstraint {
        override fun getColorImpl(component: UIComponent) = boundComponent.getColor()
    }
