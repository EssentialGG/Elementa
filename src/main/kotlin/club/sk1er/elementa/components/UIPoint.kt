package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.PositionConstraint

class UIPoint(x: PositionConstraint, y: PositionConstraint) : UIComponent() {
    init {
        setX(x)
        setY(y)
    }

    fun getX() = constraints.getX()
    fun getY() = constraints.getY()
}