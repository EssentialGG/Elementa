package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class FillConstraint @JvmOverloads constructor(private val value: Float = 0f) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getXSizeImpl(component: UIComponent, parent: UIComponent): Float {
        return parent.getRight() - component.getLeft() + value
    }

    override fun getYSizeImpl(component: UIComponent, parent: UIComponent): Float {
        return parent.getBottom() - component.getTop() + value
    }
}