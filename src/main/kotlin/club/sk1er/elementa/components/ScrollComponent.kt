package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.YConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.animate
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.plus
import club.sk1er.elementa.effects.ScissorEffect
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

/**
 * Basic scroll component that will only draw what is currently visible.
 *
 * Also prevents scrolling past what should be reasonable.
 */
class ScrollComponent(emptyString: String = "", private val scrollOpposite: Boolean = false) : UIContainer() {
    private val actualHolder = UIContainer().constrain {
        width = RelativeConstraint(1f)
        height = RelativeConstraint(1f)
    }

    private val emptyText = UIText(emptyString).constrain {
        x = CenterConstraint()
        y = SiblingConstraint() + 4.pixels()
    }

    private var offset = 0f
    private var scrollAdjustEvent: (scrollPercentage: Float, percentageOfParent: Float) -> Unit = ::updateScrollBar
    private var scrollBarGrip: UIComponent? = null
    private var dragBeginPos = -1f
    private val allChildren = CopyOnWriteArrayList<UIComponent>()
    private var needsUpdate = true

    init {
        super.addChild(actualHolder)
        actualHolder.addChild(emptyText)
        this.enableEffects(ScissorEffect())

        onMouseScroll(::onScroll)
    }

    override fun draw() {
        if (needsUpdate) {
            needsUpdate = false
            val range = calculateOffsetRange()

            // Recalculate our scroll box and move the content inside if needed.
            actualHolder.animate {
                offset = if (range.isEmpty()) 0f else offset.coerceIn(range)

                setYAnimation(Animations.IN_SIN, 0.1f, offset.pixels())
            }

            // Run our scroll adjust event, normally updating [scrollBarGrip]
            scrollAdjustEvent(abs(offset) / range.width(), this.getHeight() / calculateActualHeight())
        }

        super.draw()
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

        needsUpdate = true
    }

    fun filterChildren(filter: (component: UIComponent) -> Boolean) {
        setChildren(allChildren.filter(filter))
    }

    private fun updateGrip(component: UIComponent, mouseY: Float) {
        val minY = component.parent.getTop()
        val maxY = component.parent.getBottom()

        val dragDelta = mouseY - dragBeginPos

        offset = if (scrollOpposite) {
            val newPos = maxY - component.getBottom() - dragDelta
            val percentage = newPos / (maxY - minY)

            percentage * calculateActualHeight()
        } else {
            val newPos = component.getTop() + dragDelta - minY
            val percentage = newPos / (maxY - minY)

            -(percentage * calculateActualHeight())
        }

        needsUpdate = true
    }

    private fun onScroll(delta: Int) {
        offset += (delta * 15)

        needsUpdate = true
    }

    private fun updateScrollBar(scrollPercentage: Float, percentageOfParent: Float) {
        val comp = scrollBarGrip ?: return

        val clampedPercentage = percentageOfParent.coerceAtMost(1f)
        comp.setHeight(RelativeConstraint(clampedPercentage))

        comp.animate {
            setYAnimation(
                Animations.IN_SIN, 0.1f, object : YConstraint {
                    override fun getYPositionImpl(component: UIComponent): Float {
                        val offset = (component.parent.getHeight() - component.getHeight()) * scrollPercentage

                        return if (scrollOpposite) component.parent.getBottom() - component.getHeight() - offset
                                else component.parent.getTop() + offset
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

        return if (scrollOpposite) {
            actualHolder.children.first().getBottom() - actualHolder.children.last().getTop()
        } else {
            actualHolder.children.last().getBottom() - actualHolder.children.first().getTop()
        }
    }

    private fun calculateOffsetRange(): ClosedFloatingPointRange<Float> {
        val actualHeight = calculateActualHeight()
        val maxNegative = this.getHeight() - actualHeight
        return if (scrollOpposite) 0f..-maxNegative else maxNegative..0f
    }

    override fun addChild(component: UIComponent) = apply {
        actualHolder.removeChild(emptyText)

        actualHolder.addChild(component)
        allChildren.add(component)

        needsUpdate = true
    }

    fun insertChild(component: UIComponent, pos: Int = 0) = apply {
        actualHolder.removeChild(emptyText)

        actualHolder.children.add(pos, component)
        allChildren.add(component)

        needsUpdate = true
    }

    fun setChildren(components: List<UIComponent>) = apply {
        actualHolder.children = CopyOnWriteArrayList(components.ifEmpty { listOf(emptyText) })

        needsUpdate = true
    }

    private fun ClosedFloatingPointRange<Float>.width() = abs(this.start - this.endInclusive)
}