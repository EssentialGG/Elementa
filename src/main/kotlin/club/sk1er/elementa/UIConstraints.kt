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
//        return when (xConstraint) {
//            is PixelConstraint -> component.parent.getLeft() + xConstraint.getValue()
//            is RelativeConstraint -> component.parent.getWidth() * xConstraint.getValue()
//            is AspectConstraint -> component.getTop() * xConstraint.getValue()
//            is CenterConstraint -> component.parent.getLeft() + component.parent.getWidth() / 2 - component.getWidth() / 2
//            else -> xConstraint.getValue()
//        }
    }
    fun setX(constraint: PositionConstraint) {
        xConstraint = constraint
    }

    fun getY(): Float {
        return yConstraint.getYPosition(component, component.parent)
//        return when (yConstraint) {
//            is PixelConstraint -> component.parent.getTop() + yConstraint.getValue()
//            is RelativeConstraint -> component.parent.getHeight() * yConstraint.getValue()
//            is AspectConstraint -> component.getLeft() * yConstraint.getValue()
//            is CenterConstraint -> component.parent.getTop() + component.parent.getHeight() / 2 - component.getHeight() / 2
//            else -> yConstraint.getValue()
//        }
    }
    fun setY(constraint: PositionConstraint) {
        yConstraint = constraint
    }

    fun getWidth(): Float {
        return widthConstraint.getXSize(component, component.parent)
//        return when (widthConstraint) {
//            is PixelConstraint -> component.parent.getTop() + widthConstraint.getValue()
//            is RelativeConstraint -> component.parent.getWidth() * widthConstraint.getValue()
//            is AspectConstraint -> component.getHeight() * widthConstraint.getValue()
//            else -> widthConstraint.getValue()
//        }
    }
    fun setWidth(constraint: SizeConstraint) {
        widthConstraint = constraint
    }

    fun getHeight(): Float {
        return heightConstraint.getYSize(component, component.parent)
//        return when(heightConstraint) {
//            is PixelConstraint -> component.parent.getTop() + heightConstraint.getValue()
//            is RelativeConstraint -> component.parent.getHeight() * heightConstraint.getValue()
//            is AspectConstraint -> component.getWidth() * heightConstraint.getValue()
//            else -> heightConstraint.getValue()
//        }
    }
    fun setHeight(constraint: SizeConstraint) {
        heightConstraint = constraint
    }
}