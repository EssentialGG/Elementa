package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Sets this component's width or height to be the sum of its children's width or height
 */
class ChildBasedSizeConstraint(private val padding: Float = 0f) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent): Float {
        val holder = (constrainTo ?: component)
        val totalPadding = (holder.children.size - 1) * padding

        return holder.children
            .sumByDouble { it.getWidth().toDouble() }.toFloat() + totalPadding
    }

    override fun getHeightImpl(component: UIComponent): Float {
        val holder = (constrainTo ?: component)
        val totalPadding = (holder.children.size - 1) * padding

        return holder.children
            .sumByDouble { it.getHeight().toDouble() }.toFloat() + totalPadding
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constrainTo ?: component).children.sumByDouble { it.getHeight().toDouble() }.toFloat() * 2f
    }
}

class ChildBasedMaxSizeConstraint : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent): Float {
        return (constrainTo ?: component).children.maxBy { it.getWidth() }?.getWidth() ?: 0f
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constrainTo ?: component).children.maxBy { it.getHeight() }?.getHeight() ?: 0f
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constrainTo ?: component).children.maxBy { it.getHeight() }?.getHeight()?.times(2f) ?: 0f
    }
}