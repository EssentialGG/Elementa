package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class CenterConstraint() : PositionConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        return parent.getLeft() + parent.getWidth() / 2 - component.getWidth() / 2
    }

    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        return parent.getTop() + parent.getHeight() / 2 - component.getHeight() / 2
    }
}