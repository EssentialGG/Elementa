package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class CenterConstraint @JvmOverloads constructor(private var value: Float = 0f) : PositionConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        return value + parent.getLeft() + parent.getWidth() / 2 - component.getWidth() / 2
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        return value + parent.getTop() + parent.getHeight() / 2 - component.getHeight() / 2
    }
}