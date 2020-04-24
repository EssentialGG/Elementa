package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.mods.core.universal.UniversalMouse
import club.sk1er.mods.core.universal.UniversalResolutionUtil

class MousePositionConstraint : PositionConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return UniversalMouse.getScaledX().toFloat()
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return UniversalResolutionUtil.getInstance().scaledHeight - UniversalMouse.getScaledY().toFloat()
    }
}