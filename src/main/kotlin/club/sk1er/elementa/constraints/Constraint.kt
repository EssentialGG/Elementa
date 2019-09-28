package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

interface GeneralConstraint : PositionConstraint, SizeConstraint {
    fun getXValue(component: UIComponent, parent: UIComponent): Float

    fun getYValue(component: UIComponent, parent: UIComponent): Float

    override fun getXPosition(component: UIComponent, parent: UIComponent) = getXValue(component, parent)

    override fun getYPosition(component: UIComponent, parent: UIComponent) = getYValue(component, parent)

    override fun getXSize(component: UIComponent, parent: UIComponent) = getXValue(component, parent)

    override fun getYSize(component: UIComponent, parent: UIComponent) = getYValue(component, parent)
}

interface PositionConstraint {
    fun getXPosition(component: UIComponent, parent: UIComponent): Float

    fun getYPosition(component: UIComponent, parent: UIComponent): Float
}

interface SizeConstraint {
    fun getXSize(component: UIComponent, parent: UIComponent): Float

    fun getYSize(component: UIComponent, parent: UIComponent): Float
}