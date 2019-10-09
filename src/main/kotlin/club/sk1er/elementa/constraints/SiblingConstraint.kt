package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.helpers.Padding

class SiblingConstraint(val padding: Padding = Padding()) : PositionConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        return parent.getLeft() + padding.paddingValue
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        val index = parent.children.indexOf(component)

        if (index == 0) {
            return parent.getTop() + padding.paddingValue
        }

        val sibling = parent.children[index - 1]


        return sibling.getBottom() + padding.paddingValue
    }
}