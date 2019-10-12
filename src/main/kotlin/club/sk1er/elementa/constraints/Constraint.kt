package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import java.awt.Color

interface SuperConstraint {
    fun animationFrame() {  }
}

interface GeneralConstraint : PositionConstraint, SizeConstraint {
    fun getXValue(component: UIComponent, parent: UIComponent): Float

    fun getYValue(component: UIComponent, parent: UIComponent): Float

    override fun getXPosition(component: UIComponent, parent: UIComponent) = getXValue(component, parent)

    override fun getYPosition(component: UIComponent, parent: UIComponent) = getYValue(component, parent)

    override fun getXSize(component: UIComponent, parent: UIComponent) = getXValue(component, parent)

    override fun getYSize(component: UIComponent, parent: UIComponent) = getYValue(component, parent)
}

interface PositionConstraint : XConstraint, YConstraint

interface XConstraint : SuperConstraint {
    fun getXPosition(component: UIComponent, parent: UIComponent): Float
}

interface YConstraint : SuperConstraint {
    fun getYPosition(component: UIComponent, parent: UIComponent): Float
}

interface SizeConstraint : WidthConstraint, HeightConstraint

interface WidthConstraint : SuperConstraint {
    fun getXSize(component: UIComponent, parent: UIComponent): Float
}

interface HeightConstraint : SuperConstraint {
    fun getYSize(component: UIComponent, parent: UIComponent): Float
}

interface ColorConstraint : SuperConstraint {
    fun getColor(component: UIComponent, parent: UIComponent): Color
}