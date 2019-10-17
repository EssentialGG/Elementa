package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Tries to expand to fill all of the remaining width/height available in this component's
 * parent.
 */
class FillConstraint @JvmOverloads constructor(private val value: Float = 0f) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return parent.getRight() - component.getLeft() + value
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return parent.getBottom() - component.getTop() + value
    }
}