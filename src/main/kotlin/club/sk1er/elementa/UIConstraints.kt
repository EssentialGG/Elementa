package club.sk1er.elementa

import club.sk1er.elementa.constraints.*
import java.awt.Color

open class UIConstraints(protected val component: UIComponent) {
    internal var xConstraint: XConstraint = PixelConstraint(0f)
    internal var yConstraint: YConstraint = PixelConstraint(0f)
    internal var widthConstraint: WidthConstraint = PixelConstraint(0f)
    internal var heightConstraint: HeightConstraint = PixelConstraint(0f)
    internal var colorConstraint: ColorConstraint = ConstantColorConstraint(Color.WHITE)

    open fun getX(): Float {
        return xConstraint.getXPosition(component, component.parent)
    }
    open fun setX(constraint: PositionConstraint) = apply {
        xConstraint = constraint
    }

    open fun getY(): Float {
        return yConstraint.getYPosition(component, component.parent)
    }
    open fun setY(constraint: PositionConstraint) = apply {
        yConstraint = constraint
    }

    open fun getWidth(): Float {
        return widthConstraint.getXSize(component, component.parent)
    }
    open fun setWidth(constraint: SizeConstraint) = apply {
        widthConstraint = constraint
    }

    open fun getHeight(): Float {
        return heightConstraint.getYSize(component, component.parent)
    }
    open fun setHeight(constraint: SizeConstraint) = apply {
        heightConstraint = constraint
    }

    open fun getColor(): Color {
        return colorConstraint.getColor(component, component.parent)
    }
    open fun setColor(constraint: ColorConstraint) = apply {
        colorConstraint = constraint
    }

    internal open fun animationFrame() {
        xConstraint.animationFrame()
        yConstraint.animationFrame()
        widthConstraint.animationFrame()
        heightConstraint.animationFrame()
        colorConstraint.animationFrame()
    }

    fun finish(): UIComponent {
        return component
    }

    fun copy() = UIConstraints(component).apply {
        this.xConstraint = this@UIConstraints.xConstraint
        this.yConstraint = this@UIConstraints.yConstraint
        this.widthConstraint = this@UIConstraints.widthConstraint
        this.heightConstraint = this@UIConstraints.heightConstraint
        this.colorConstraint = this@UIConstraints.colorConstraint
    }
}