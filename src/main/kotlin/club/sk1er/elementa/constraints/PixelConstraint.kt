package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent

class PixelConstraint(private val offset: Int) : PositionConstraint() {
    override fun getPosition(component: UIComponent, parent: UIComponent): Int {
        return offset
    }
}