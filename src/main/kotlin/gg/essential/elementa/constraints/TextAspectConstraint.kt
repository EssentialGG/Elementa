package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.universal.UGraphics
import java.lang.UnsupportedOperationException

/**
 * For size:
 * Sets the width/height to be [value] multiple of its own height/width respectively.
 *
 * For position:
 * Sets the x/y position to be [value] multiple of its own y/x position respectively.
 */
class TextAspectConstraint : WidthConstraint, HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent): Float {
        val text = (component as? UIText)?.getText() ?: throw IllegalStateException("TextAspectConstraint can only be used in UIText components")
        return UGraphics.getStringWidth(text) * component.getHeight() / 9
    }

    override fun getHeightImpl(component: UIComponent): Float {
        val text = (component as? UIText)?.getText() ?: throw IllegalStateException("TextAspectConstraint can only be used in UIText components")
        return 9 * component.getWidth() / UGraphics.getStringWidth(text)
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.WIDTH -> visitor.visitSelf(ConstraintType.HEIGHT)
            ConstraintType.HEIGHT -> visitor.visitSelf(ConstraintType.WIDTH)
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}
