package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.animation.AnimationComponent
import gg.essential.elementa.constraints.debug.constraintDebugger
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.utils.roundToRealPixels
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

    fun visit(visitor: ConstraintVisitor, type: ConstraintType, setNewConstraint: Boolean = true) {
        // TODO: Support constrainTo
        if (constrainTo != null)
            return

        if (setNewConstraint)
            visitor.setConstraint(this, type)
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
        val debugger = constraintDebugger
        if (debugger != null) {
            return debugger.evaluate(this, ConstraintType.X, component)
        }

        if (recalculate) {
            cachedValue = getXPositionImpl(component).roundToRealPixels(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface YConstraint : SuperConstraint<Float> {
    fun getYPositionImpl(component: UIComponent): Float

    fun getYPosition(component: UIComponent): Float {
        val debugger = constraintDebugger
        if (debugger != null) {
            return debugger.evaluate(this, ConstraintType.Y, component)
        }

        if (recalculate) {
            cachedValue = getYPositionImpl(component).roundToRealPixels(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface SizeConstraint : WidthConstraint, HeightConstraint, RadiusConstraint

interface RadiusConstraint : SuperConstraint<Float> {
    fun getRadiusImpl(component: UIComponent): Float

    fun getRadius(component: UIComponent): Float {
        val debugger = constraintDebugger
        if (debugger != null) {
            return debugger.evaluate(this, ConstraintType.RADIUS, component)
        }

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
        val debugger = constraintDebugger
        if (debugger != null) {
            return debugger.evaluate(this, ConstraintType.WIDTH, component)
        }

        if (recalculate) {
            cachedValue = getWidthImpl(component).roundToRealPixels(component)
            recalculate = false
        }

        return cachedValue
    }
}

interface HeightConstraint : SuperConstraint<Float> {
    fun getHeightImpl(component: UIComponent): Float

    fun getHeight(component: UIComponent): Float {
        val debugger = constraintDebugger
        if (debugger != null) {
            return debugger.evaluate(this, ConstraintType.HEIGHT, component)
        }

        if (recalculate) {
            cachedValue = getHeightImpl(component).roundToRealPixels(component)
            recalculate = false
        }

        return cachedValue
    }

    fun getTextScale(component: UIComponent): Float {
        return getHeight(component)
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