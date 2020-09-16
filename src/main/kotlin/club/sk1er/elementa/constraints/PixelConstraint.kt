package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor

/**
 * Sets this component's X/Y position or width/height to be a constant
 * number of pixels.
 */
class PixelConstraint @JvmOverloads constructor(
    var value: Float,
    var alignOpposite: Boolean = false,
    var alignOutside:  Boolean = false
) : MasterConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    fun alignOutside(value: Boolean) = apply {
        this.alignOutside = value
    }

    fun alignOpposite(value: Boolean) = apply {
        this.alignOpposite = value
    }

    fun setValue(value: Float) = apply {
        this.value = value
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)

        return if (alignOpposite) {
            if (alignOutside) {
                target.getRight() + value
            } else {
                target.getRight() - value - component.getWidth()
            }
        } else {
            if (alignOutside) {
                target.getLeft() - component.getWidth() - value
            } else {
                target.getLeft() + value
            }
        }
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)

        return if (alignOpposite) {
            if (alignOutside) {
                target.getBottom() + value
            } else {
                target.getBottom() - value - component.getHeight()
            }
        } else {
            if (alignOutside) {
                target.getTop() - component.getHeight() - value
            } else {
                target.getTop() + value
            }
        }
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return value
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return value
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return value
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.X -> {
                if (alignOpposite) {
                    visitor.visitParent(ConstraintType.X)
                    visitor.visitParent(ConstraintType.WIDTH)
                    if (alignOutside)
                        visitor.visitSelf(ConstraintType.WIDTH)
                } else {
                    visitor.visitParent(ConstraintType.X)
                    if (alignOutside)
                        visitor.visitSelf(ConstraintType.WIDTH)
                }
            }
            ConstraintType.Y -> {
                if (alignOpposite) {
                    visitor.visitParent(ConstraintType.Y)
                    visitor.visitParent(ConstraintType.HEIGHT)
                    if (alignOutside)
                        visitor.visitSelf(ConstraintType.HEIGHT)
                } else {
                    visitor.visitParent(ConstraintType.Y)
                    if (alignOutside)
                        visitor.visitSelf(ConstraintType.HEIGHT)
                }
            }
            ConstraintType.WIDTH,
            ConstraintType.HEIGHT,
            ConstraintType.RADIUS,
            ConstraintType.TEXT_SCALE -> {}
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}