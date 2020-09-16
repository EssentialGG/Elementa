package club.sk1er.elementa

import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.elementa.dsl.pixels
import java.awt.Color
import java.util.*

open class UIConstraints(protected val component: UIComponent) : Observable() {
    var x: XConstraint = 0.pixels()
        set(value) {
            field = value
            update(ConstraintType.X)
        }
    var y: YConstraint = 0.pixels()
        set(value) {
            field = value
            update(ConstraintType.Y)
        }
    var width: WidthConstraint = 0.pixels()
        set(value) {
            field = value
            update(ConstraintType.WIDTH)
        }
    var height: HeightConstraint = 0.pixels()
        set(value) {
            field = value
            update(ConstraintType.HEIGHT)
        }
    var radius: RadiusConstraint = 0.pixels()
        set(value) {
            field = value
            update(ConstraintType.RADIUS)
        }
    var textScale: HeightConstraint = 1.pixels()
        set(value) {
            field = value
            update(ConstraintType.TEXT_SCALE)
        }
    var color: ColorConstraint = Color.WHITE.asConstraint()
        set(value) {
            field = value
            update(ConstraintType.COLOR)
        }

    open fun getX(): Float {
        return x.getXPosition(component)
    }
    open fun withX(constraint: XConstraint) = apply {
        x = constraint
    }

    open fun getY(): Float {
        return y.getYPosition(component)
    }
    open fun withY(constraint: YConstraint) = apply {
        y = constraint
    }

    open fun getWidth(): Float {
        return width.getWidth(component)
    }
    open fun withWidth(constraint: WidthConstraint) = apply {
        width = constraint
    }

    open fun getHeight(): Float {
        return height.getHeight(component)
    }
    open fun withHeight(constraint: HeightConstraint) = apply {
        height = constraint
    }

    open fun getRadius(): Float {
        return radius.getRadius(component)
    }
    open fun withRadius(constraint: RadiusConstraint) = apply {
        radius = constraint
    }

    open fun getTextScale(): Float {
        return textScale.getHeight(component)
    }
    open fun withTextScale(constraint: HeightConstraint) = apply {
        textScale = constraint
    }

    open fun getColor(): Color {
        return color.getColor(component)
    }
    open fun withColor(constraint: ColorConstraint) = apply {
        color = constraint
    }

    internal open fun animationFrame() {
        x.animationFrame()
        y.animationFrame()
        width.animationFrame()
        height.animationFrame()
        radius.animationFrame()
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
        this.radius = this@UIConstraints.radius
        this.color = this@UIConstraints.color
    }

    private fun update(type: ConstraintType) {
        setChanged()
        notifyObservers(type)
    }
}
