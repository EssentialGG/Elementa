package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State

/**
 * Sets this component's X/Y position or width/height to be a constant
 * number of pixels.
 */
class PixelConstraint @JvmOverloads constructor(
    value: Float,
    alignOpposite: Boolean = false,
    alignOutside:  Boolean = false
) : MasterConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private var valueState: State<Float> = BasicState(value)
    private var alignOppositeState: State<Boolean> = BasicState(alignOpposite)
    private var alignOutsideState: State<Boolean> = BasicState(alignOutside)

    var value: Float
        get() = valueState.get()
        set(value) { valueState.set(value) }
    var alignOpposite: Boolean
        get() = alignOppositeState.get()
        set(value) { alignOppositeState.set(value) }
    var alignOutside: Boolean
        get() = alignOutsideState.get()
        set(value) { alignOutsideState.set(value) }

    fun bindValue(newState: State<Float>) = apply {
        valueState = newState
    }

    fun bindAlignOpposite(newState: State<Boolean>) = apply {
        alignOppositeState = newState
    }

    fun bindAlignOutside(newState: State<Boolean>) = apply {
        alignOutsideState = newState
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)
        val value = this.valueState.get()

        return if (alignOppositeState.get()) {
            if (alignOutsideState.get()) {
                target.getRight() + value
            } else {
                target.getRight() - value - component.getWidth()
            }
        } else {
            if (alignOutsideState.get()) {
                target.getLeft() - component.getWidth() - value
            } else {
                target.getLeft() + value
            }
        }
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)
        val value = this.valueState.get()

        return if (alignOppositeState.get()) {
            if (alignOutsideState.get()) {
                target.getBottom() + value
            } else {
                target.getBottom() - value - component.getHeight()
            }
        } else {
            if (alignOutsideState.get()) {
                target.getTop() - component.getHeight() - value
            } else {
                target.getTop() + value
            }
        }
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return valueState.get()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return valueState.get()
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return valueState.get()
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.X -> {
                if (alignOppositeState.get()) {
                    visitor.visitParent(ConstraintType.X)
                    visitor.visitParent(ConstraintType.WIDTH)
                    if (alignOutsideState.get())
                        visitor.visitSelf(ConstraintType.WIDTH)
                } else {
                    visitor.visitParent(ConstraintType.X)
                    if (alignOutsideState.get())
                        visitor.visitSelf(ConstraintType.WIDTH)
                }
            }
            ConstraintType.Y -> {
                if (alignOppositeState.get()) {
                    visitor.visitParent(ConstraintType.Y)
                    visitor.visitParent(ConstraintType.HEIGHT)
                    if (alignOutsideState.get())
                        visitor.visitSelf(ConstraintType.HEIGHT)
                } else {
                    visitor.visitParent(ConstraintType.Y)
                    if (alignOutsideState.get())
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
