package gg.essential.elementa.layoutdsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.utils.ObservableAddEvent
import gg.essential.elementa.utils.ObservableClearEvent
import gg.essential.elementa.utils.ObservableListEvent
import gg.essential.elementa.utils.ObservableRemoveEvent

abstract class Arrangement {
    internal lateinit var mainAxis: Axis
    internal var recalculatePositions = true
    internal var recalculateSizes = true

    protected lateinit var boundComponent: UIComponent
        private set
    protected val lastPosValues = hashMapOf<UIComponent, Float>()
    protected val lastSizeValues = hashMapOf<UIComponent, Float>()

    abstract fun layoutPositions()
    open fun layoutSizes() {}
    abstract fun getPadding(child: UIComponent): Float

    fun getPosValue(component: UIComponent): Float {
        if (recalculatePositions) {
            layoutPositions()
            recalculatePositions = false
        }
        return lastPosValues[component]
            ?: error("Component $component's position was not laid out by arrangement $this")
    }

    fun getSizeValue(component: UIComponent): Float {
        if (recalculateSizes) {
            layoutSizes()
            recalculateSizes = false
        }
        return lastSizeValues[component]
            ?: error("Component $component's size was not laid out by arrangement $this")
    }

    @Suppress("UNCHECKED_CAST")
    open fun initialize(component: UIComponent) {
        boundComponent = component
        component.children.forEach(::conformChild)
        component.children.addObserver { _, arg ->
            when (val event = arg as? ObservableListEvent<UIComponent> ?: return@addObserver) {
                is ObservableAddEvent -> conformChild(event.element.value)
                is ObservableRemoveEvent -> {
                    lastPosValues.remove(event.element.value)
                    lastSizeValues.remove(event.element.value)
                }
                is ObservableClearEvent -> {
                    lastPosValues.clear()
                    lastSizeValues.clear()
                }
            }
        }
    }

    open fun conformChild(child: UIComponent) {
        when (mainAxis) {
            Axis.HORIZONTAL -> child.setX(ArrangementControlledPositionConstraint(this))
            Axis.VERTICAL -> child.setY(ArrangementControlledPositionConstraint(this))
        }
    }

    protected fun UIComponent.getMainAxisSize() = when (mainAxis) {
        Axis.HORIZONTAL -> getWidth()
        Axis.VERTICAL -> getHeight()
    }

    protected fun UIComponent.getCrossAxisSize() = when (mainAxis) {
        Axis.HORIZONTAL -> getHeight()
        Axis.VERTICAL -> getWidth()
    }

    protected fun UIComponent.getMainAxisStart() = when (mainAxis) {
        Axis.HORIZONTAL -> getLeft()
        Axis.VERTICAL -> getTop()
    }

    protected fun UIComponent.getCrossAxisStart() = when (mainAxis) {
        Axis.HORIZONTAL -> getTop()
        Axis.VERTICAL -> getLeft()
    }

    companion object {
        val SpaceAround: Arrangement get() = SpaceAroundArrangement()
        val SpaceBetween: Arrangement get() = SpaceBetweenArrangement()
        val SpaceEvenly: Arrangement get() = SpaceEvenlyArrangement()

        fun spacedBy(spacing: Float = 0f, float: FloatPosition = FloatPosition.CENTER): Arrangement = SpacedArrangement(spacing, float)
        fun equalWeight(spacing: Float = 0f): Arrangement = EqualWeightArrangement(spacing)
    }
}

private open class SpacedArrangement(
    protected val spacing: Float = 0f,
    protected val floatPosition: FloatPosition = FloatPosition.CENTER,
) : Arrangement() {
    open fun getSpacing(parent: UIComponent) = spacing

    open fun getStartOffset(parent: UIComponent, spacing: Float): Float {
        val childrenSize = parent.children.sumOf { it.getMainAxisSize() } + spacing * (parent.children.size - 1)
        return when (floatPosition) {
            FloatPosition.START -> 0f
            FloatPosition.CENTER -> parent.getMainAxisSize() / 2 - childrenSize / 2
            FloatPosition.END -> parent.getMainAxisSize() - childrenSize
        }
    }

    override fun layoutPositions() {
        val spacing = getSpacing(boundComponent)
        var nextStart = boundComponent.getMainAxisStart() + getStartOffset(boundComponent, spacing)
        boundComponent.children.forEach {
            lastPosValues[it] = nextStart
            nextStart += it.getMainAxisSize() + spacing
        }
    }

    override fun getPadding(child: UIComponent): Float {
        return if (child === boundComponent.children.last()) 0f else getSpacing(boundComponent)
    }
}

private class SpaceBetweenArrangement : SpacedArrangement() {
    override fun getSpacing(parent: UIComponent): Float {
        return (parent.getMainAxisSize() - parent.children.sumOf { it.getMainAxisSize() }) / (parent.children.size - 1)
    }
}

private class SpaceEvenlyArrangement : SpacedArrangement() {
    override fun getSpacing(parent: UIComponent): Float {
        return (parent.getMainAxisSize() - parent.children.sumOf { it.getMainAxisSize() }) / (parent.children.size + 1)
    }

    override fun getStartOffset(parent: UIComponent, spacing: Float): Float {
        return spacing
    }
}

private class SpaceAroundArrangement : SpacedArrangement() {
    override fun getSpacing(parent: UIComponent): Float {
        return (parent.getMainAxisSize() - parent.children.sumOf { it.getMainAxisSize() }) / parent.children.size
    }

    override fun getStartOffset(parent: UIComponent, spacing: Float): Float {
        return spacing / 2
    }
}

private class EqualWeightArrangement(spacing: Float) : SpacedArrangement(spacing, FloatPosition.CENTER) {
    override fun conformChild(child: UIComponent) {
        super.conformChild(child)
        when (mainAxis) {
            Axis.HORIZONTAL -> child.setWidth(ArrangementControlledSizeConstraint(this))
            Axis.VERTICAL -> child.setHeight(ArrangementControlledSizeConstraint(this))
        }
    }

    override fun layoutSizes() {
        val childCount = boundComponent.children.size
        val childSize = (boundComponent.getMainAxisSize() - (childCount - 1) * spacing) / childCount
        boundComponent.children.forEach {
            lastSizeValues[it] = childSize
        }
    }
}

private class ArrangementControlledPositionConstraint(private val arrangement: Arrangement) : PositionConstraint, PaddingConstraint {
    override var cachedValue = 0f
    override var recalculate = true
        set(value) {
            field = value
            if (value) {
                arrangement.recalculatePositions = true
            }
        }
    override var constrainTo: UIComponent?
        get() = null
        set(_) = error("Cannot bind an arrangement-controlled constraint to another component!")

    init {
        arrangement.recalculatePositions = true
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
    }

    override fun getXPositionImpl(component: UIComponent) = arrangement.getPosValue(component)

    override fun getYPositionImpl(component: UIComponent) = arrangement.getPosValue(component)

    override fun getHorizontalPadding(component: UIComponent): Float {
        return if (arrangement.mainAxis == Axis.HORIZONTAL) arrangement.getPadding(component) else 0f
    }

    override fun getVerticalPadding(component: UIComponent): Float {
        return if (arrangement.mainAxis == Axis.VERTICAL) arrangement.getPadding(component) else 0f
    }
}

private class ArrangementControlledSizeConstraint(private val arrangement: Arrangement) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
        set(value) {
            field = value
            if (value) {
                arrangement.recalculateSizes = true
            }
        }
    override var constrainTo: UIComponent?
        get() = null
        set(_) = error("Cannot bind an arrangement-controlled constraint to another component!")

    init {
        arrangement.recalculateSizes = true
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
    }

    override fun getWidthImpl(component: UIComponent) = arrangement.getSizeValue(component)

    override fun getHeightImpl(component: UIComponent) = arrangement.getSizeValue(component)

    override fun getRadiusImpl(component: UIComponent) = arrangement.getSizeValue(component)
}