package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.PositionConstraint
import club.sk1er.elementa.dsl.constrain

class UIPoint(x: PositionConstraint, y: PositionConstraint) : UIComponent() {
    init {
        constrain {
            withX(x)
            withY(y)
        }
    }

    fun getX() = getConstraints().getX()
    fun getY() = getConstraints().getY()
}