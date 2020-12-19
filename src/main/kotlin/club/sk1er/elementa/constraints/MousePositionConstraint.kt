package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.mods.core.universal.UMouse
import club.sk1er.mods.core.universal.UResolution

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
