package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.helpers.Padding

class CramSiblingConstraint(private val padding: Padding = Padding()) : PositionConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        val index = parent.children.indexOf(component)

        if (index == 0) {
            return parent.getLeft() + padding.paddingValue
        }

        val sibling = parent.children[index - 1]

        if (sibling.getRight() + padding.paddingValue + component.getWidth() < parent.getRight()) {
            return sibling.getRight() + padding.paddingValue
        }

        return parent.getLeft() + padding.paddingValue
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        val index = parent.children.indexOf(component)

        if (index == 0) {
            return parent.getTop() + padding.paddingValue
        }

        val sibling = parent.children[index - 1]

        if (sibling.getRight() + padding.paddingValue + component.getWidth() < parent.getRight()) {
            return sibling.getTop()
        }

        var lowestPoint = sibling.getBottom()

        for (n in index - 1 downTo 0) {
            val child = parent.children[n]

            if (child.getTop() != sibling.getTop()) break

            if (child.getBottom() > lowestPoint) lowestPoint = child.getBottom()
        }

        return lowestPoint + padding.paddingValue
    }
}