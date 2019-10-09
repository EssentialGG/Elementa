package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class PixelConstraint @JvmOverloads constructor(
    private val value: Float,
    private val alignOpposite: Boolean = false
) : PositionConstraint, SizeConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        return if (!alignOpposite) parent.getLeft() + value
                else parent.getRight() - value - component.getWidth()
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        return if (!alignOpposite) parent.getTop() + value
                else parent.getBottom() - value - component.getHeight()
    }

    override fun getXSize(component: UIComponent, parent: UIComponent): Float {
        return value
    }

    override fun getYSize(component: UIComponent, parent: UIComponent): Float {
        return value
    }
}