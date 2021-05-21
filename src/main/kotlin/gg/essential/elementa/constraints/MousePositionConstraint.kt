package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.universal.UMouse
import gg.essential.universal.UResolution

class MousePositionConstraint : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return UMouse.getScaledX().toFloat()
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return UResolution.scaledHeight - UMouse.getScaledY().toFloat()
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}
