package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

/**
 * Positions this component to be directly after its previous sibling.
 *
 * Intended for use in either the x or y direction but not both at the same time.
 * If you would like for components to try and fit inline, use [CramSiblingConstraint]
 */
open class SiblingConstraint @JvmOverloads constructor(private val alignOpposite: Boolean = false) : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        val index = parent.children.indexOf(component)

        if (alignOpposite) {
            if (index == 0) return parent.getRight() - component.getWidth()
            val sibling = parent.children[index - 1]
            return getLeftmostPoint(sibling, parent, index) - component.getWidth()
        } else {
            if (index == 0) return parent.getLeft()
            val sibling = parent.children[index - 1]
            return getRightmostPoint(sibling, parent, index)
        }
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        val index = parent.children.indexOf(component)

        if (alignOpposite) {
            if (index == 0) return parent.getBottom() - component.getHeight()
            val sibling = parent.children[index - 1]
            return getHighestPoint(sibling, parent, index) - component.getHeight()
        } else {
            if (index == 0) return parent.getTop()
            val sibling = parent.children[index - 1]
            return getLowestPoint(sibling, parent, index)
        }
    }

    protected fun getLowestPoint(sibling: UIComponent, parent: UIComponent, index: Int): Float {
        var lowestPoint = sibling.getBottom()

        for (n in index - 1 downTo 0) {
            val child = parent.children[n]

            if (child.getTop() != sibling.getTop()) break

            if (child.getBottom() > lowestPoint) lowestPoint = child.getBottom()
        }

        return lowestPoint
    }

    protected fun getHighestPoint(sibling: UIComponent, parent: UIComponent, index: Int): Float {
        var highestPoint = sibling.getTop()

        for (n in index - 1 downTo 0) {
            val child = parent.children[n]

            if (child.getBottom() != sibling.getBottom()) break

            if (child.getTop() < highestPoint) highestPoint = child.getTop()
        }

        return highestPoint
    }

    protected fun getRightmostPoint(sibling: UIComponent, parent: UIComponent, index: Int): Float {
        var rightmostPoint = sibling.getRight()

        for (n in index - 1 downTo 0) {
            val child = parent.children[n]

            if (child.getLeft() != sibling.getLeft()) break

            if (child.getRight() > rightmostPoint) rightmostPoint = child.getRight()
        }

        return rightmostPoint
    }

    protected fun getLeftmostPoint(sibling: UIComponent, parent: UIComponent, index: Int): Float {
        var leftmostPoint = sibling.getLeft()

        for (n in index - 1 downTo 0) {
            val child = parent.children[n]

            if (child.getRight() != sibling.getRight()) break

            if (child.getLeft() < leftmostPoint) leftmostPoint = child.getLeft()
        }

        return leftmostPoint
    }
}