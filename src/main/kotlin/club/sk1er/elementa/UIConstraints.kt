package club.sk1er.elementa

import club.sk1er.elementa.constraints.*
import java.awt.Color

open class UIConstraints(protected val component: UIComponent) {
    internal var x: XConstraint = PixelConstraint(0f)
    internal var y: YConstraint = PixelConstraint(0f)
    internal var width: WidthConstraint = PixelConstraint(0f)
    internal var height: HeightConstraint = PixelConstraint(0f)
    internal var color: ColorConstraint = ConstantColorConstraint(Color.WHITE)

    open fun getX(): Float {
        return x.getXPosition(component, component.parent)
    }
    open fun setX(constraint: XConstraint) = apply {
        x = constraint
    }

    open fun getY(): Float {
        return y.getYPosition(component, component.parent)
    }
    open fun setY(constraint: YConstraint) = apply {
        y = constraint
    }

    open fun getWidth(): Float {
        return width.getWidth(component, component.parent)
    }
    open fun setWidth(constraint: WidthConstraint) = apply {
        width = constraint
    }

    open fun getHeight(): Float {
        return height.getHeight(component, component.parent)
    }
    open fun setHeight(constraint: HeightConstraint) = apply {
        height = constraint
    }

    open fun getColor(): Color {
        return color.getColor(component, component.parent)
    }
    open fun setColor(constraint: ColorConstraint) = apply {
        color = constraint
    }

    internal open fun animationFrame() {
        x.animationFrame()
        y.animationFrame()
        width.animationFrame()
        height.animationFrame()
        color.animationFrame()
    }

    fun finish(): UIComponent {
        return component
    }

    fun copy() = UIConstraints(component).apply {
        this.x = this@UIConstraints.x
        this.y = this@UIConstraints.y
        this.width = this@UIConstraints.width
        this.height = this@UIConstraints.height
        this.color = this@UIConstraints.color
    }
}