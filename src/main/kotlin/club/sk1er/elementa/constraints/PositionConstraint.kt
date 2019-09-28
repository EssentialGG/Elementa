package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

abstract class PositionConstraint {
    abstract fun getPosition(component: UIComponent, parent: UIComponent): Int
}