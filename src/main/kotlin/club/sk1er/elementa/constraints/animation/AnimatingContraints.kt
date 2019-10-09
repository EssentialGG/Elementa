package club.sk1er.elementa.constraints.animation

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.constraints.PositionConstraint
import club.sk1er.elementa.constraints.SizeConstraint
import javax.naming.OperationNotSupportedException

class AnimatingConstraints(
    component: UIComponent
) : UIConstraints(component) {


    override fun setX(constraint: PositionConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")

    override fun setY(constraint: PositionConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")

    override fun setWidth(constraint: SizeConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")

    override fun setHeight(constraint: SizeConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")
}