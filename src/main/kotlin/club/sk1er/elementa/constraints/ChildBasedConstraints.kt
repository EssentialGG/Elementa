package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's width or height to be the sum of its children's width or height
 */
class ChildBasedSizeConstraint : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.sumByDouble { it.getWidth().toDouble() }.toFloat()
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.sumByDouble { it.getHeight().toDouble() }.toFloat()
    }

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.sumByDouble { it.getHeight().toDouble() }.toFloat() * 2f
    }
}

class ChildBasedMaxSizeConstraint : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.maxBy{ it.getWidth() }!!.getWidth()
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.maxBy{ it.getHeight() }!!.getHeight()
    }

    override fun getRadiusImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.maxBy{ it.getHeight() }!!.getHeight() * 2f
    }
}