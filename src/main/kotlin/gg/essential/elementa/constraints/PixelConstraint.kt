package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State as StateV1
import gg.essential.elementa.state.v2.*

/**
 * Sets this component's X/Y position or width/height to be a constant
 * number of pixels.
 */
class PixelConstraint @JvmOverloads constructor(
    value: State<Float>,
    alignOpposite: State<Boolean> = stateOf(false),
    alignOutside: State<Boolean> = stateOf(false),
) : MasterConstraint {
    @JvmOverloads constructor(
        value: Float,
        alignOpposite: Boolean = false,
        alignOutside: Boolean = false
    ) : this(stateOf(value), stateOf(alignOpposite), stateOf(alignOutside))

    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private val valueState = value.wrapWithDelegatingMutableState()
    private val alignOppositeState = alignOpposite.wrapWithDelegatingMutableState()
    private val alignOutsideState = alignOutside.wrapWithDelegatingMutableState()

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

    @JvmOverloads
    @Deprecated("Legacy State API", level = DeprecationLevel.HIDDEN)
    constructor(
        value: StateV1<Float>,
        alignOpposite: StateV1<Boolean> = BasicState(false),
        alignOutside: StateV1<Boolean> = BasicState(false)
    ) : this(value, alignOpposite, alignOutside)

    @Deprecated("Legacy State API", level = DeprecationLevel.HIDDEN)
    fun bindValue(newState: StateV1<Float>) = bindValue(newState)

    @Deprecated("Legacy State API", level = DeprecationLevel.HIDDEN)
    fun bindAlignOpposite(newState: StateV1<Boolean>) = bindAlignOpposite(newState)

    @Deprecated("Legacy State API", level = DeprecationLevel.HIDDEN)
    fun bindAlignOutside(newState: StateV1<Boolean>) = bindAlignOutside(newState)
}
