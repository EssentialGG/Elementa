package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.YConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.animate
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.effects.ScissorEffect
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

/**
 * Basic scroll component that will only draw what is currently visible.
 *
 * Also prevents scrolling past what should be reasonable.
 */
class ScrollComponent : UIContainer() {
    private val actualHolder = UIContainer().constrain {
        width = RelativeConstraint(1f)
        height = RelativeConstraint(1f)
    }

    private var offset = 0f
    private var scrollAdjustEvent: (scrollPercentage: Float, percentageOfParent: Float) -> Unit = ::updateScrollBar
    private var scrollBarGrip: UIComponent? = null
    private var dragBeginPos = -1f
    private val allChildren = CopyOnWriteArrayList<UIComponent>()

    init {
        super.addChild(actualHolder)

        this.enableEffects(ScissorEffect())

        onMouseScroll(::onScroll)
    }

    fun setScrollAdjustEvent(event: (scrollPercentage: Float, percentageOfParent: Float) -> Unit) {
        scrollAdjustEvent = event
    }

    /**
     * A scroll bar component is an optional component that can visually display the scroll status
     * of this scroll component (just like any scroll bar).
     *
     * The utility here is that it is automatically updated by this component with no extra work on the user-end.
     *
     * The hierarchy for this scrollbar grip component must be as follows:
     *  - Have a containing parent being the full height range of this scroll bar.
     *
     *  [component]'s parent's mouse events will all be overridden by this action.
     */
    fun setScrollBarComponent(component: UIComponent) {
        scrollBarGrip = component

        component.parent.onMouseScroll(::onScroll)

        component.onMouseClick { _, mouseY, _ ->
            dragBeginPos = mouseY
        }

        component.onMouseDrag { _, mouseY, _ ->
            if (dragBeginPos == -1f) return@onMouseDrag

            updateGrip(component, mouseY)
        }

        component.onMouseRelease {
            dragBeginPos = -1f
        }

        onScroll(0)
    }

    fun filterChildren(filter: (component: UIComponent) -> Boolean) {
        actualHolder.children = allChildren.filterTo(CopyOnWriteArrayList(), filter)

        onScroll(0)
    }

    private fun updateGrip(component: UIComponent, mouseY: Float) {
        val minY = component.parent.getTop()
        val maxY = component.parent.getBottom()

        val dragDelta = mouseY - dragBeginPos
        val newPos = component.getTop() + dragDelta - minY
        val percentage = newPos / (maxY - minY)

        offset = -(percentage * calculateActualHeight())

        onScroll(0)
    }

    private fun onScroll(delta: Int) {
        offset += (delta * 15)
        actualHolder.animate {
            val range = calculateOffsetRange()
            offset = if (range.isEmpty()) 0f else offset.coerceIn(range)

            setYAnimation(Animations.IN_SIN, 0.1f, offset.pixels())

            scrollAdjustEvent(abs(offset) / range.width(), this.getHeight() / calculateActualHeight())
        }
    }

    private fun updateScrollBar(scrollPercentage: Float, percentageOfParent: Float) {
        val comp = scrollBarGrip ?: return

        val clampedPercentage = percentageOfParent.coerceAtMost(1f)
        comp.setHeight(RelativeConstraint(clampedPercentage))

        comp.animate {
            setYAnimation(
                Animations.IN_SIN, 0.1f, object : YConstraint {
                    override fun getYPositionImpl(component: UIComponent): Float {
                        return component.parent.getTop() + (component.parent.getHeight() - component.getHeight()) * scrollPercentage
                    }

                    override var cachedValue = 0f
                    override var recalculate = true
                    override var constrainTo: UIComponent? = null
                }
            )
        }
    }

    private fun calculateActualHeight(): Float {
        if (actualHolder.children.isEmpty()) return 0f

        return actualHolder.children.last().getBottom() - actualHolder.children.first().getTop()
    }

    private fun calculateOffsetRange(): ClosedFloatingPointRange<Float> {
        val actualHeight = calculateActualHeight()
        val maxNegative = this.getHeight() - actualHeight
        return maxNegative..0f
    }

    override fun addChild(component: UIComponent) = apply {
        actualHolder.addChild(component)
        allChildren.add(component)

        val range = calculateOffsetRange()
        scrollAdjustEvent(abs(offset) / range.width(), calculateActualHeight() / this.getHeight())
    }

    private fun ClosedFloatingPointRange<Float>.width() = abs(this.start - this.endInclusive)
}