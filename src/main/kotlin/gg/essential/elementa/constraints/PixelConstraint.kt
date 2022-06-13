package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.MappedState
import gg.essential.elementa.state.State

/**
 * Sets this component's X/Y position or width/height to be a constant
 * number of pixels.
 */
class PixelConstraint @JvmOverloads constructor(
    value: State<Float>,
    alignOpposite: State<Boolean> = BasicState(false),
    alignOutside: State<Boolean> = BasicState(false)
) : MasterConstraint {
    @JvmOverloads constructor(
        value: Float,
        alignOpposite: Boolean = false,
        alignOutside: Boolean = false
    ) : this(BasicState(value), BasicState(alignOpposite), BasicState(alignOutside))
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private val valueState: MappedState<Float, Float> = value.map { it }
    private val alignOppositeState: MappedState<Boolean, Boolean> = alignOpposite.map { it }
    private val alignOutsideState: MappedState<Boolean, Boolean> = alignOutside.map { it }

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
        valueState.rebind(newState)
    }

    fun bindAlignOpposite(newState: State<Boolean>) = apply {
        alignOppositeState.rebind(newState)
    }

    fun bindAlignOutside(newState: State<Boolean>) = apply {
        alignOutsideState.rebind(newState)
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
