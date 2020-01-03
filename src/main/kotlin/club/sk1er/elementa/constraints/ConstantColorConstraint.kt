package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import java.awt.Color
import java.lang.UnsupportedOperationException

/**
 * Sets the color to be a constant, determined color.
 */
class ConstantColorConstraint(private val color: Color) : ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getColorImpl(component: UIComponent): Color {
        return color
    }

    override fun to(component: UIComponent) {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }
}