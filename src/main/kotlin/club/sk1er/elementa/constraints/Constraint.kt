package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.animation.AnimationComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import java.awt.Color

/**
 * The "super" constraint that all other constraints inherit from.
 *
 * [T] is what this constraint deals with, for example Float for WidthConstraint
 * or Color for ColorConstraint
 */
interface SuperConstraint<T> {
    var cachedValue: T
    var recalculate: Boolean
    var constrainTo: UIComponent?

    fun animationFrame() {
        recalculate = true
    }

    fun to(component: UIComponent) = apply {
        constrainTo = component
    }

    fun pauseIfSupported() {
        (this as? AnimationComponent<*>)?.pause()
    }

    fun resumeIfSupported() {
        (this as? AnimationComponent<*>)?.resume()
    }

    fun stopIfSupported() {
        (this as? AnimationComponent<*>)?.stop()
    }

    fun visit(visitor: ConstraintVisitor, type: ConstraintType) {
        // TODO: Support constrainTo
        if (constrainTo != null)
            return

        visitor.visit(this)
        visitImpl(visitor, type)
    }

    fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType)
}

interface GeneralConstraint : PositionConstraint, SizeConstraint {
    fun getXValue(component: UIComponent): Float

    fun getYValue(component: UIComponent): Float

    override fun getXPositionImpl(component: UIComponent) = getXValue(component)

    override fun getYPositionImpl(component: UIComponent) = getYValue(component)

    override fun getWidthImpl(component: UIComponent) = getXValue(component)

    override fun getHeightImpl(component: UIComponent) = getYValue(component)

    override fun getRadiusImpl(component: UIComponent) = getXValue(component)
}

interface PositionConstraint : XConstraint, YConstraint

interface XConstraint : SuperConstraint<Float> {
    fun getXPositionImpl(component: UIComponent): Float

    fun getXPosition(component: UIComponent): Float {
        if (recalculate) {
            cachedValue = getXPositionImpl(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface YConstraint : SuperConstraint<Float> {
    fun getYPositionImpl(component: UIComponent): Float

    fun getYPosition(component: UIComponent): Float {
        if (recalculate) {
            cachedValue = getYPositionImpl(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface SizeConstraint : WidthConstraint, HeightConstraint, RadiusConstraint

interface RadiusConstraint : SuperConstraint<Float> {
    fun getRadiusImpl(component: UIComponent): Float

    fun getRadius(component: UIComponent): Float {
        if (recalculate) {
            cachedValue = getRadiusImpl(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface WidthConstraint : SuperConstraint<Float> {
    fun getWidthImpl(component: UIComponent): Float

    fun getWidth(component: UIComponent): Float {
        if (recalculate) {
            cachedValue = getWidthImpl(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface HeightConstraint : SuperConstraint<Float> {
    fun getHeightImpl(component: UIComponent): Float

    fun getHeight(component: UIComponent): Float {
        if (recalculate) {
            cachedValue = getHeightImpl(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface ColorConstraint : SuperConstraint<Color> {
    fun getColorImpl(component: UIComponent): Color

    fun getColor(component: UIComponent): Color {
        if (recalculate) {
            cachedValue = getColorImpl(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface MasterConstraint : PositionConstraint, SizeConstraint