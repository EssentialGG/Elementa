package club.sk1er.elementa

import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.PositionConstraint
import club.sk1er.elementa.constraints.SizeConstraint

class UIConstraints(private val component: UIComponent) {
    private var xConstraint: PositionConstraint = PixelConstraint(0f)
    private var yConstraint: PositionConstraint = PixelConstraint(0f)
    private var widthConstraint: SizeConstraint = PixelConstraint(0f)
    private var heightConstraint: SizeConstraint = PixelConstraint(0f)


    fun getX(): Float {
        return xConstraint.getXPosition(component, component.parent)
    }
    fun setX(constraint: PositionConstraint) = apply {
        xConstraint = constraint
    }

    fun getY(): Float {
        return yConstraint.getYPosition(component, component.parent)
    }
    fun setY(constraint: PositionConstraint) = apply {
        yConstraint = constraint
    }

    fun getWidth(): Float {
        return widthConstraint.getXSize(component, component.parent)
    }
    fun setWidth(constraint: SizeConstraint) = apply {
        widthConstraint = constraint
    }

    fun getHeight(): Float {
        return heightConstraint.getYSize(component, component.parent)
    }
    fun setHeight(constraint: SizeConstraint) = apply {
        heightConstraint = constraint
    }

    fun copy() = UIConstraints(component).apply {
        this.xConstraint = this@UIConstraints.xConstraint
        this.yConstraint = this@UIConstraints.yConstraint
        this.widthConstraint = this@UIConstraints.widthConstraint
        this.heightConstraint = this@UIConstraints.heightConstraint
    }
}