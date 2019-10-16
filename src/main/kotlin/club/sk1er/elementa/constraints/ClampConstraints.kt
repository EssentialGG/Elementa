package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class MaxWidthConstraint(
    private val widthConstraint: WidthConstraint,
    private val maxWidthConstraint: WidthConstraint
) : WidthConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getXSizeImpl(component: UIComponent, parent: UIComponent): Float {
        return widthConstraint.getXSize(component, parent).coerceAtMost(maxWidthConstraint.getXSize(component, parent))
    }
}

class MinWidthConstraint(
    private val widthConstraint: WidthConstraint,
    private val minWidthConstraint: WidthConstraint
) : WidthConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getXSizeImpl(component: UIComponent, parent: UIComponent): Float {
        return widthConstraint.getXSize(component, parent).coerceAtLeast(minWidthConstraint.getXSize(component, parent))
    }
}

class MaxHeightConstraint(
    private val heightConstraint: HeightConstraint,
    private val maxHeightConstraint: HeightConstraint
) : HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getYSizeImpl(component: UIComponent, parent: UIComponent): Float {
        return heightConstraint.getYSize(component, parent).coerceAtMost(maxHeightConstraint.getYSize(component, parent))
    }
}

class MinHeightConstraint(
    private val heightConstraint: HeightConstraint,
    private val minHeightConstraint: HeightConstraint
) : HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getYSizeImpl(component: UIComponent, parent: UIComponent): Float {
        return heightConstraint.getYSize(component, parent).coerceAtLeast(minHeightConstraint.getYSize(component, parent))
    }
}