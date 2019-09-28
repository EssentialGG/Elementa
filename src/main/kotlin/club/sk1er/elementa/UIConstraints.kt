package club.sk1er.elementa

import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.PositionConstraint

class UIConstraints(private val component: UIComponent) {
    private var xConstraint: PositionConstraint = PixelConstraint(0)
    private var yConstraint: PositionConstraint = PixelConstraint(0)

    fun getX() = xConstraint.getPosition(component, component.parent)
    fun setX(constraint: PositionConstraint) {
        xConstraint = constraint
    }

    fun getY() = yConstraint.getPosition(component, component.parent)
    fun setY(constraint: PositionConstraint) {
        yConstraint = constraint
    }
}