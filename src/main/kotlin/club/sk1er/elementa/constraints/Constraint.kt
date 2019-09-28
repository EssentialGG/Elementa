package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

interface PositionConstraint {
    fun getXPosition(component: UIComponent, parent: UIComponent): Float

    fun getYPosition(component: UIComponent, parent: UIComponent): Float
}

interface SizeConstraint {
    fun getXSize(component: UIComponent, parent: UIComponent): Float

    fun getYSize(component: UIComponent, parent: UIComponent): Float
}