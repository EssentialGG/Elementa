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

//        if (component.children.size == 1) {
//            return component.children[0].getWidth()
//        }
//        return component.children.filter {
//            it.getConstraints().x is SiblingConstraint
//        }.sumByDouble { it.getWidth().toDouble() }.toFloat()
    }

    override fun getHeightImpl(component: UIComponent, parent: UIComponent): Float {
        return component.children.sumByDouble { it.getHeight().toDouble() }.toFloat()

//        if (component.children.size == 1) {
//            return component.children[0].getHeight()
//        }
//        return component.children.filter {
//            it.getConstraints().y is SiblingConstraint
//        }.sumByDouble { it.getHeight().toDouble() }.toFloat()
    }
}