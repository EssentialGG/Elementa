package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import java.awt.Color

class ConstantColorConstraint(private val color: Color) : ColorConstraint {
    override fun getColor(component: UIComponent, parent: UIComponent): Color {
        return color
    }
}