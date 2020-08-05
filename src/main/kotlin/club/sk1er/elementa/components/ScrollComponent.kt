package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.events.UIClickEvent
import club.sk1er.elementa.svg.SVGParser
import club.sk1er.mods.core.universal.UniversalMouse
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs


/**
 * Basic scroll component that will only draw what is currently visible.
 *
 * Also prevents scrolling past what should be reasonable.
 */
class ScrollComponent @JvmOverloads constructor(
    emptyString: String = "",
    private val scrollOpposite: Boolean = false,
    private val innerPadding: Float = 0f,
    private val scrollIconColor: Color = Color.WHITE
) : UIContainer() {
    private val actualHolder = UIContainer().constrain {
        x = innerPadding.pixels()
        y = innerPadding.pixels()
        width = RelativeConstraint(1f) - innerPadding.pixels()
        height = RelativeConstraint(1f)
    }

    private val emptyText = UIText(emptyString).constrain {
        x = CenterConstraint()
        y = SiblingConstraint() + 4.pixels()
    }

    private var offset = innerPadding
    private val scrollAdjustEvents: MutableList<(Float, Float) -> Unit> = mutableListOf(::updateScrollBar)
    private var scrollBarGrip: UIComponent? = null
    private var hideScrollWhenUseless = false
    private var dragBeginPos = -1f
    private var needsUpdate = true

    private var isAutoScrolling = false
    private var autoScrollBegin: Pair<Float, Float> = -1f to -1f

    val allChildren = CopyOnWriteArrayList<UIComponent>()

    init {
        super.addChild(actualHolder)
        actualHolder.addChild(emptyText)
        this.enableEffects(ScissorEffect())

        onMouseScroll { onScroll(it) }
        onMouseClick { event -> onClick(event.relativeX, event.relativeY, event.mouseButton) }
    }

    private val scrollSVGComponent = (SVGComponent(scrollSVG).constrain {
        width = 24.pixels()
        height = 24.pixels()

        color = scrollIconColor.asConstraint()
    }).also {
        super.addChild(it)
        it.hide(instantly = true)
    }

    override fun draw() {
        if (needsUpdate) {
            needsUpdate = false
            val range = calculateOffsetRange()

            // Recalculate our scroll box and move the content inside if needed.
            actualHolder.animate {
                offset = if (range.isEmpty()) innerPadding else offset.coerceIn(range)

                setYAnimation(Animations.IN_SIN, 0.1f, offset.pixels())
            }

            // Run our scroll adjust event, normally updating [scrollBarGrip]
            val percent = abs(offset) / range.width()
            val percentageOfParent = this.getHeight() / calculateActualHeight()
            scrollAdjustEvents.forEach { it(percent, percentageOfParent) }
        }

        super.draw()
    }

    /**
     * Sets the text that appears when no items are shown
     */
    fun setEmptyText(text: String) {
        emptyText.setText(text)
    }

    fun addScrollAdjustEvent(event: (scrollPercentage: Float, percentageOfParent: Float) -> Unit) {
        scrollAdjustEvents.add(event)
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
     *
     *  If [hideWhenUseless] is enabled, [component] will have [hide] called on it when the scrollbar is full height
     *  and dragging it would do nothing.
     */
    @JvmOverloads
    fun setScrollBarComponent(component: UIComponent, hideWhenUseless: Boolean = false) {
        scrollBarGrip = component
        hideScrollWhenUseless = hideWhenUseless

        component.parent.onMouseScroll { onScroll(it) }

        component.onMouseClick { event ->
            dragBeginPos = event.relativeY
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
        actualHolder.children = CopyOnWriteArrayList(allChildren.filter(filter).ifEmpty { listOf(emptyText) })
        actualHolder.children.forEach { it.parent = actualHolder }

        needsUpdate = true
    }

    fun <T : Comparable<T>> sortChildren(comparator: (UIComponent) -> T, descending: Boolean = false) {
        if (descending) {
            actualHolder.children.sortByDescending(comparator)
        } else {
            actualHolder.children.sortBy(comparator)
        }
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

        if (hideScrollWhenUseless) {
            if (clampedPercentage == 1f) {
                comp.hide()
                return
            } else {
                comp.unhide()
            }
        }

        comp.setHeight(RelativeConstraint(clampedPercentage))

        comp.animate {
            setYAnimation(
                Animations.IN_SIN, 0.1f, basicYConstraint { component ->
                val offset = (component.parent.getHeight() - component.getHeight()) * scrollPercentage

                if (scrollOpposite) component.parent.getBottom() - component.getHeight() - offset
                else component.parent.getTop() + offset
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
        val maxNegative = this.getHeight() - actualHeight - innerPadding
        return if (scrollOpposite) (-innerPadding)..-maxNegative else maxNegative..(innerPadding)
    }

    private fun onClick(mouseX: Float, mouseY: Float, mouseButton: Int) {
        if (isAutoScrolling) {
            isAutoScrolling = false
            scrollSVGComponent.hide()
            return
        }

        if (mouseButton == 2) {
            // Middle click, begin the auto scroll
            isAutoScrolling = true
            autoScrollBegin = mouseX to mouseY

            scrollSVGComponent.constrain {
                x = (mouseX - 12).pixels()
                y = (mouseY - 12).pixels()
            }

            scrollSVGComponent.unhide(useLastPosition = false)
        }
    }

    override fun animationFrame() {
        super.animationFrame()

        if (!isAutoScrolling) return
        val yBegin = autoScrollBegin.second + getTop()

        val sr = Window.of(this).scaledResolution
        val scaledHeight = sr.scaledHeight
        val currentY = scaledHeight - UniversalMouse.getScaledY() - 1
        val currentX = UniversalMouse.getScaledX()

        if (currentY < getTop() || currentY > getBottom()) return
        if (currentX < getLeft() || currentX > getRight()) return

        val deltaY = currentY - yBegin
        val percent = deltaY / ((getTop() - getBottom()) / 2)

        offset += (percent * 5)
        needsUpdate = true
    }

    override fun addChild(component: UIComponent) = apply {
        actualHolder.removeChild(emptyText)

        actualHolder.addChild(component)
        allChildren.add(component)

        needsUpdate = true
    }

    override fun insertChildAt(component: UIComponent, index: Int) = apply {
        if (index < 0 || index >= children.size) {
            println("Bad index given to insertChildAt (index: $index, children size: ${children.size}")
            return@apply
        }

        actualHolder.removeChild(emptyText)

        component.parent = actualHolder
        actualHolder.children.add(index, component)
        allChildren.add(index, component)

        needsUpdate = true
    }

    override fun insertChildBefore(newComponent: UIComponent, targetComponent: UIComponent) = apply {
        val indexOfExisting = children.indexOf(targetComponent)
        if (indexOfExisting == -1) {
            println("targetComponent given to insertChildBefore is not a child of this component")
            return@apply
        }

        insertChildAt(newComponent, indexOfExisting)
    }

    override fun insertChildAfter(newComponent: UIComponent, targetComponent: UIComponent) = apply {
        val indexOfExisting = children.indexOf(targetComponent)
        if (indexOfExisting == -1) {
            println("targetComponent given to insertChildAfter is not a child of this component")
            return@apply
        }

        insertChildAt(newComponent, indexOfExisting + 1)
    }

    override fun replaceChild(newComponent: UIComponent, componentToReplace: UIComponent) = apply {
        val indexOfExisting = children.indexOf(componentToReplace)
        if (indexOfExisting == -1) {
            println("componentToReplace given to replaceChild is not a child of this component")
            return@apply
        }

        actualHolder.removeChild(emptyText)

        actualHolder.children.removeAt(indexOfExisting)
        allChildren.removeAt(indexOfExisting)

        newComponent.parent = actualHolder
        actualHolder.children.add(indexOfExisting, newComponent)
        allChildren.add(indexOfExisting, newComponent)
    }

    override fun alwaysDrawChildren(): Boolean {
        return true
    }

    override fun <T> childrenOfType(clazz: Class<T>): List<T> {
        return actualHolder.childrenOfType(clazz)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int) {
        for (i in actualHolder.children.lastIndex downTo 0) {
            val child = actualHolder.children[i]

            if (child.isPointInside(mouseX.toFloat(), mouseY.toFloat())) {
                return child.mouseClick(mouseX, mouseY, button)
            }
        }

        fireMouseEvent(UIClickEvent(mouseX.toFloat(), mouseY.toFloat(), button, this, this))
    }

    fun searchAndInsert(components: List<UIComponent>, comparison: (UIComponent) -> Int) {
        if (components.isEmpty()) return

        actualHolder.children.remove(emptyText)
        val searchIndex = actualHolder.children.binarySearch(comparison = comparison)

        components.forEach { it.parent = actualHolder }
        allChildren.addAll(components)
        actualHolder.children.addAll(
            if (searchIndex >= 0) searchIndex else -(searchIndex + 1),
            components
        )

        needsUpdate = true
    }

    fun setChildren(components: List<UIComponent>) = apply {
        actualHolder.children = CopyOnWriteArrayList(components.ifEmpty { listOf(emptyText) })
        actualHolder.children.forEach { it.parent = actualHolder }

        allChildren.clear()
        allChildren.addAll(actualHolder.children)

        needsUpdate = true
    }

    private fun ClosedFloatingPointRange<Float>.width() = abs(this.start - this.endInclusive)

    companion object {
        private val scrollSVG = SVGParser.parseFromResource("/scroll.svg")
    }
}