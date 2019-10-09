package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class ChildBasedSizeConstraint : SizeConstraint {
    override fun getXSize(component: UIComponent, parent: UIComponent): Float {
        return component.children.maxBy { it.getWidth() }?.getWidth() ?: 0f
    }

    override fun getYSize(component: UIComponent, parent: UIComponent): Float {
        return component.children.sumByDouble { it.getHeight().toDouble() }.toFloat()
    }
}