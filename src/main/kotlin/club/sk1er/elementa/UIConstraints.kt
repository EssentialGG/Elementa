package club.sk1er.elementa

import club.sk1er.elementa.constraints.*
import java.awt.Color

open class UIConstraints(protected val component: UIComponent) {
    var x: XConstraint = PixelConstraint(0f)
    var y: YConstraint = PixelConstraint(0f)
    var width: WidthConstraint = PixelConstraint(0f)
    var height: HeightConstraint = PixelConstraint(0f)
    var color: ColorConstraint = ConstantColorConstraint(Color.WHITE)

    open fun getX(): Float {
        return x.getXPosition(component, component.parent)
    }
    open fun setX(constraint: PositionConstraint) = apply {
        x = constraint
    }

    open fun getY(): Float {
        return y.getYPosition(component, component.parent)
    }
    open fun setY(constraint: PositionConstraint) = apply {
        y = constraint
    }

    open fun getWidth(): Float {
        return width.getXSize(component, component.parent)
    }
    open fun setWidth(constraint: SizeConstraint) = apply {
        width = constraint
    }

    open fun getHeight(): Float {
        // TODO: Obviously not good. It makes no sense for Padding to be specific to SiblingConstraints,
        //  rather, it should probably be a wrapping constraint (like MaxWidthConstraint)
        return height.getYSize(component, component.parent) +
                if (y is SiblingConstraint) (y as SiblingConstraint).padding.paddingValue else 0f
    }
    open fun setHeight(constraint: SizeConstraint) = apply {
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