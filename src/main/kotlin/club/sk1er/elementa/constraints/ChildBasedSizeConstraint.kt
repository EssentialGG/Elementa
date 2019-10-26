package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's width to be the greatest of its children's widths,
 * or sets its height to be the total of all it's children's heights.
 */
class ChildBasedSizeConstraint : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.maxBy { it.getWidth() }?.getWidth() ?: 0f
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.sumByDouble { it.getHeight().toDouble() }.toFloat()
    }
}