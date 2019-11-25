package club.sk1er.elementa

import club.sk1er.elementa.constraints.*
import java.awt.Color

open class UIConstraints(protected val component: UIComponent) {
    var x: XConstraint = PixelConstraint(0f)
    var y: YConstraint = PixelConstraint(0f)
    var width: WidthConstraint = PixelConstraint(0f)
    var height: HeightConstraint = PixelConstraint(0f)
    var color: ColorConstraint = ConstantColorConstraint(Color.WHITE)
    var textScale: HeightConstraint = PixelConstraint(1f)

    open fun getX(): Float {
        return x.getXPosition(component, component.parent)
    }
    open fun withX(constraint: XConstraint) = apply {
        x = constraint
    }

    open fun getY(): Float {
        return y.getYPosition(component, component.parent)
    }
    open fun withY(constraint: YConstraint) = apply {
        y = constraint
    }

    open fun getWidth(): Float {
        return width.getWidth(component, component.parent)
    }
    open fun withWidth(constraint: WidthConstraint) = apply {
        width = constraint
    }

    open fun getHeight(): Float {
        return height.getHeight(component, component.parent)
    }
    open fun withHeight(constraint: HeightConstraint) = apply {
        height = constraint
    }

    open fun getColor(): Color {
        return color.getColor(component, component.parent)
    }
    open fun withColor(constraint: ColorConstraint) = apply {
        color = constraint
    }

    open fun getTextScale(): Float {
        return textScale.getHeight(component, component.parent)
    }

    open fun withTextScale(constraint: HeightConstraint) = apply {
        textScale = constraint
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