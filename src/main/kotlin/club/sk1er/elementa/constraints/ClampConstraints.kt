package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class MaxWidthConstraint(
    private val widthConstraint: WidthConstraint,
    private val maxWidthConstraint: WidthConstraint
) : WidthConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return widthConstraint.getWidth(component, parent).coerceAtMost(maxWidthConstraint.getWidth(component, parent))
    }
}

class MinWidthConstraint(
    private val widthConstraint: WidthConstraint,
    private val minWidthConstraint: WidthConstraint
) : WidthConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getWidthImpl(component: UIComponent, parent: UIComponent): Float {
        return widthConstraint.getWidth(component, parent).coerceAtLeast(minWidthConstraint.getWidth(component, parent))
    }
}

class MaxHeightConstraint(
    private val heightConstraint: HeightConstraint,
    private val maxHeightConstraint: HeightConstraint
) : HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return heightConstraint.getHeight(component, parent).coerceAtMost(maxHeightConstraint.getHeight(component, parent))
    }
}

class MinHeightConstraint(
    private val heightConstraint: HeightConstraint,
    private val minHeightConstraint: HeightConstraint
) : HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return heightConstraint.getHeight(component, parent).coerceAtLeast(minHeightConstraint.getHeight(component, parent))
    }
}