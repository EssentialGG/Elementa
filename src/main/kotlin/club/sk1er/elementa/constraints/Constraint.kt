package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import java.awt.Color

interface SuperConstraint <T> {
    var cachedValue: T
    var recalculate: Boolean

    fun animationFrame() {
        recalculate = true
    }
}

interface GeneralConstraint : PositionConstraint, SizeConstraint {
    fun getXValue(component: UIComponent, parent: UIComponent): Float

    fun getYValue(component: UIComponent, parent: UIComponent): Float

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent) = getXValue(component, parent)

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent) = getYValue(component, parent)

    override fun getXSizeImpl(component: UIComponent, parent: UIComponent) = getXValue(component, parent)

    override fun getYSizeImpl(component: UIComponent, parent: UIComponent) = getYValue(component, parent)
}

interface PositionConstraint : XConstraint, YConstraint

interface XConstraint : SuperConstraint<Float> {
    fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float

    fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        if (recalculate) {
            cachedValue = getXPositionImpl(component, parent)
            recalculate = false
        }

        return cachedValue
    }
}

interface YConstraint : SuperConstraint<Float> {
    fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float

    fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        if (recalculate) {
            cachedValue = getYPositionImpl(component, parent)
            recalculate = false
        }

        return cachedValue
    }
}

interface SizeConstraint : WidthConstraint, HeightConstraint

interface WidthConstraint : SuperConstraint<Float> {
    fun getXSizeImpl(component: UIComponent, parent: UIComponent): Float

    fun getXSize(component: UIComponent, parent: UIComponent): Float {
        if (recalculate) {
            cachedValue = getXSizeImpl(component, parent)
            recalculate = false
        }

        return cachedValue
    }
}

interface HeightConstraint : SuperConstraint<Float> {
    fun getYSizeImpl(component: UIComponent, parent: UIComponent): Float

    fun getYSize(component: UIComponent, parent: UIComponent): Float {
        if (recalculate) {
            cachedValue = getYSizeImpl(component, parent)
            recalculate = false
        }

        return cachedValue
    }
}

interface ColorConstraint : SuperConstraint<Color> {
    fun getColorImpl(component: UIComponent, parent: UIComponent): Color

    fun getColor(component: UIComponent, parent: UIComponent): Color {
        if (recalculate) {
            cachedValue = getColorImpl(component, parent)
            recalculate = false
        }

        return cachedValue
    }
}