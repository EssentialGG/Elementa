package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class CramSiblingConstraint : SiblingConstraint() {
    override var cachedValue = 0f
    override var recalculate = true

    override fun getXPositionImpl(component: UIComponent, parent: UIComponent): Float {
        val index = parent.children.indexOf(component)

        if (index == 0) {
            return parent.getLeft()
        }

        val sibling = parent.children[index - 1]

        if (sibling.getRight() + component.getWidth() < parent.getRight()) {
            return sibling.getRight()
        }

        return parent.getLeft()
    }

    override fun getYPositionImpl(component: UIComponent, parent: UIComponent): Float {
        val index = parent.children.indexOf(component)

        if (index == 0) {
            return parent.getTop()
        }

        val sibling = parent.children[index - 1]

        if (sibling.getRight() + component.getWidth() < parent.getRight()) {
            return sibling.getTop()
        }

        return getLowestPoint(sibling, parent, index)
    }
}