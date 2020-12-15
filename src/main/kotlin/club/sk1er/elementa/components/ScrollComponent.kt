package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.svg.SVGParser
import club.sk1er.elementa.utils.bindLast
import club.sk1er.mods.core.universal.UniversalKeyboard
import club.sk1er.mods.core.universal.UniversalMouse
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
import kotlin.math.max


/**
 * Basic scroll component that will only draw what is currently visible.
 *
 * Also prevents scrolling past what should be reasonable.
 */
class ScrollComponent @JvmOverloads constructor(
    emptyString: String = "",
    private val innerPadding: Float = 0f,
    private val scrollIconColor: Color = Color.WHITE,
    private val horizontalScrollEnabled: Boolean = false,
    private val verticalScrollEnabled: Boolean = true,
    private val horizontalScrollOpposite: Boolean = false,
    private val verticalScrollOpposite: Boolean = false,
    private val pixelsPerScroll: Float = 15f,
    customScissorBoundingBox: UIComponent? = null
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

    private val scrollSVGComponent = SVGComponent(scrollSVG).constrain {
        width = 24.pixels()
        height = 24.pixels()

        color = scrollIconColor.asConstraint()
    }

    var horizontalOffset = innerPadding
        private set
    var verticalOffset = innerPadding
        private set

    private var horizontalScrollBarGrip: UIComponent? = null
    private var horizontalHideScrollWhenUseless = false
    private var verticalScrollBarGrip: UIComponent? = null
    private var verticalHideScrollWhenUseless = false

    private var horizontalDragBeginPos = -1f
    private var verticalDragBeginPos = -1f

    private val horizontalScrollAdjustEvents: MutableList<(Float, Float) -> Unit> = mutableListOf(::updateScrollBar.bindLast(true))
    private val verticalScrollAdjustEvents: MutableList<(Float, Float) -> Unit> = mutableListOf(::updateScrollBar.bindLast(false))
    private var needsUpdate = true

    private var isAutoScrolling = false
    private var autoScrollBegin: Pair<Float, Float> = -1f to -1f

    val allChildren = CopyOnWriteArrayList<UIComponent>()

    /**
     * Difference between the ScrollComponent's width and its contents' width.
     * Will not be less than zero, even if the contents' width is less than the
     * component's width.
     */
    val horizontalOverhang: Float
        get() = max(0f, calculateActualWidth() - getWidth())

    /**
     * Difference between the ScrollComponent's height and its contents' height.
     * Will not be less than zero, even if the contents' height is less than the
     * component's height.
     */
    val verticalOverhang: Float
        get() = max(0f, calculateActualHeight() - getHeight())

    init {
        if (!horizontalScrollEnabled && !verticalScrollEnabled)
            throw IllegalArgumentException("ScrollComponent must have at least one direction of scrolling enabled")

        super.addChild(actualHolder)
        actualHolder.addChild(emptyText)
        this.enableEffects(ScissorEffect(customScissorBoundingBox))

        super.addChild(scrollSVGComponent)
        scrollSVGComponent.hide(instantly = true)

        onMouseScroll {
            if (UniversalKeyboard.isShiftKeyDown() && horizontalScrollEnabled) {
                onScroll(it.delta.toFloat(), isHorizontal = true)
            } else if (verticalScrollEnabled) {
                onScroll(it.delta.toFloat(), isHorizontal = false)
            }

            it.stopPropagation()
        }

        onMouseClick { event ->
            onClick(event.relativeX, event.relativeY, event.mouseButton)
        }
    }

    override fun draw() {
        if (needsUpdate) {
            needsUpdate = false
            val horizontalRange = calculateOffsetRange(isHorizontal = true)
            val verticalRange = calculateOffsetRange(isHorizontal = false)

            // Recalculate our scroll box and move the content inside if needed.
            actualHolder.animate {
                horizontalOffset = if (horizontalRange.isEmpty()) innerPadding else horizontalOffset.coerceIn(horizontalRange)
                verticalOffset = if (verticalRange.isEmpty()) innerPadding else verticalOffset.coerceIn(verticalRange)

                setXAnimation(Animations.IN_SIN, 0.1f, horizontalOffset.pixels())
                setYAnimation(Animations.IN_SIN, 0.1f, verticalOffset.pixels())
            }

            // Run our scroll adjust event, normally updating [scrollBarGrip]
            var percent = abs(horizontalOffset) / horizontalRange.width()
            var percentageOfParent = this.getWidth() / calculateActualWidth()
            horizontalScrollAdjustEvents.forEach { it(percent, percentageOfParent) }

            percent = abs(verticalOffset) / verticalRange.width()
            percentageOfParent = this.getHeight() / calculateActualHeight()
            verticalScrollAdjustEvents.forEach { it(percent, percentageOfParent) }
        }

        super.draw()
    }

    /**
     * Sets the text that appears when no items are shown
     */
    fun setEmptyText(text: String) {
        emptyText.setText(text)
    }

    fun addScrollAdjustEvent(isHorizontal: Boolean, event: (scrollPercentage: Float, percentageOfParent: Float) -> Unit) {
        if (isHorizontal) horizontalScrollAdjustEvents.add(event) else verticalScrollAdjustEvents.add(event)
    }

    @JvmOverloads
    fun setHorizontalScrollBarComponent(component: UIComponent, hideWhenUseless: Boolean = false) {
        setScrollBarComponent(component, hideWhenUseless, isHorizontal = true)
    }

    @JvmOverloads
    fun setVerticalScrollBarComponent(component: UIComponent, hideWhenUseless: Boolean = false) {
        setScrollBarComponent(component, hideWhenUseless, isHorizontal = false)
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
     *  [component]'s mouse events will all be overridden by this action.
     *
     *  If [hideWhenUseless] is enabled, [component] will have [hide] called on it when the scrollbar is full height
     *  and dragging it would do nothing.
     */
    fun setScrollBarComponent(component: UIComponent, hideWhenUseless: Boolean, isHorizontal: Boolean) {
        if (isHorizontal) {
            horizontalScrollBarGrip = component
            horizontalHideScrollWhenUseless = hideWhenUseless
        } else {
            verticalScrollBarGrip = component
            verticalHideScrollWhenUseless = hideWhenUseless
        }

        component.onMouseScroll {
            if (isHorizontal && horizontalScrollEnabled && UniversalKeyboard.isShiftKeyDown()) {
                onScroll(it.delta.toFloat(), isHorizontal = true)
            } else if (!isHorizontal && verticalScrollEnabled) {
                onScroll(it.delta.toFloat(), isHorizontal = false)
            }

            it.stopPropagation()
        }

        component.onMouseClick { event ->
            if (isHorizontal) {
                horizontalDragBeginPos = event.relativeX
            } else {
                verticalDragBeginPos = event.relativeY
            }
        }

        component.onMouseDrag { mouseX, mouseY, _ ->
            if (isHorizontal) {
                if (horizontalDragBeginPos == -1f)
                    return@onMouseDrag
                updateGrip(component, mouseX, isHorizontal = true)
            } else {
                if (verticalDragBeginPos == -1f)
                    return@onMouseDrag
                updateGrip(component, mouseY, isHorizontal = false)
            }
        }

        component.onMouseRelease {
            if (isHorizontal) {
                horizontalDragBeginPos = -1f
            } else {
                verticalDragBeginPos = -1f
            }
        }

        needsUpdate = true
    }

    fun filterChildren(filter: (component: UIComponent) -> Boolean) {
        actualHolder.children.clear()
        actualHolder.children.addAll(allChildren.filter(filter).ifEmpty { listOf(emptyText) })
        actualHolder.children.forEach { it.parent = actualHolder }

        needsUpdate = true
    }

    @JvmOverloads
    fun <T : Comparable<T>> sortChildren(descending: Boolean = false, comparator: (UIComponent) -> T) {
        if (descending) {
            actualHolder.children.sortByDescending(comparator)
        } else {
            actualHolder.children.sortBy(comparator)
        }
    }

    private fun updateGrip(component: UIComponent, mouseCoord: Float, isHorizontal: Boolean) {
        if (isHorizontal) {
            val minCoord = component.parent.getLeft()
            val maxCoord = component.parent.getRight()
            val dragDelta = mouseCoord - horizontalDragBeginPos

            horizontalOffset = if (horizontalScrollOpposite) {
                val newPos = maxCoord - component.getRight() - dragDelta
                val percentage = newPos / (maxCoord - minCoord)

                percentage * calculateActualWidth()
            } else {
                val newPos = component.getLeft() + dragDelta - minCoord
                val percentage = newPos / (maxCoord - minCoord)

                -(percentage * calculateActualWidth())
            }
        } else {
            val minCoord = component.parent.getTop()
            val maxCoord = component.parent.getBottom()
            val dragDelta = mouseCoord - verticalDragBeginPos

            verticalOffset = if (verticalScrollOpposite) {
                val newPos = maxCoord - component.getBottom() - dragDelta
                val percentage = newPos / (maxCoord - minCoord)

                percentage * calculateActualHeight()
            } else {
                val newPos = component.getTop() + dragDelta - minCoord
                val percentage = newPos / (maxCoord - minCoord)

                -(percentage * calculateActualHeight())
            }
        }

        needsUpdate = true
    }

    private fun onScroll(delta: Float, isHorizontal: Boolean) {
        if (isHorizontal) {
            horizontalOffset += delta * pixelsPerScroll
        } else {
            verticalOffset += delta * pixelsPerScroll
        }

        needsUpdate = true
    }

    private fun updateScrollBar(scrollPercentage: Float, percentageOfParent: Float, isHorizontal: Boolean) {
        val component = if (isHorizontal) {
            horizontalScrollBarGrip ?: return
        } else {
            verticalScrollBarGrip ?: return
        }

        val clampedPercentage = percentageOfParent.coerceAtMost(1f)

        if ((isHorizontal && horizontalHideScrollWhenUseless) || (!isHorizontal && verticalHideScrollWhenUseless)) {
            if (clampedPercentage == 1f) {
                component.hide()
                return
            } else {
                component.unhide()
            }
        }

        if (isHorizontal) {
            component.setWidth(RelativeConstraint(clampedPercentage))
        } else {
            component.setHeight(RelativeConstraint(clampedPercentage))
        }

        component.animate {
            if (isHorizontal) {
                setXAnimation(
                    Animations.IN_SIN, 0.1f, basicXConstraint { component ->
                        val offset = (component.parent.getWidth() - component.getWidth()) * scrollPercentage

                        if (horizontalScrollOpposite) component.parent.getRight() - component.getHeight() - offset
                        else component.parent.getLeft() + offset
                    }
                )
            } else {
                setYAnimation(
                    Animations.IN_SIN, 0.1f, basicYConstraint { component ->
                        val offset = (component.parent.getHeight() - component.getHeight()) * scrollPercentage

                        if (verticalScrollOpposite) component.parent.getBottom() - component.getHeight() - offset
                        else component.parent.getTop() + offset
                    }
                )
            }
        }
    }

    private fun calculateActualWidth(): Float {
        if (actualHolder.children.isEmpty()) return 0f

        return actualHolder.children.let { c ->
            c.map { it.getRight() }.max()!! - c.map { it.getLeft() }.min()!!
        }
    }

    private fun calculateActualHeight(): Float {
        if (actualHolder.children.isEmpty()) return 0f

        return actualHolder.children.let { c ->
            c.map { it.getBottom() }.max()!! - c.map { it.getTop() }.min()!!
        }
    }

    private fun calculateOffsetRange(isHorizontal: Boolean): ClosedFloatingPointRange<Float> {
        return if (isHorizontal) {
            val actualWidth = calculateActualWidth()
            val maxNegative = this.getWidth() - actualWidth - innerPadding
            if (horizontalScrollOpposite) (-innerPadding)..-maxNegative else maxNegative..(innerPadding)
        } else {
            val actualHeight = calculateActualHeight()
            val maxNegative = this.getHeight() - actualHeight - innerPadding
            if (verticalScrollOpposite) (-innerPadding)..-maxNegative else maxNegative..(innerPadding)
        }
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

        val xBegin = autoScrollBegin.first + getLeft()
        val yBegin = autoScrollBegin.second + getTop()

        val scaledHeight = Window.of(this).scaledResolution.scaledHeight
        val currentX = UniversalMouse.getScaledX()
        val currentY = scaledHeight - UniversalMouse.getScaledY() - 1

        if (currentY < getTop() || currentY > getBottom()) return
        if (currentX < getLeft() || currentX > getRight()) return

        val deltaX = currentX - xBegin
        val deltaY = currentY - yBegin
        val percentX = deltaX / (-getWidth() / 2)
        val percentY = deltaY / (-getHeight() / 2)

        horizontalOffset += (percentX * 5)
        verticalOffset += (percentY * 5)

        needsUpdate = true
    }

    override fun addChild(component: UIComponent) = apply {
        actualHolder.removeChild(emptyText)

        actualHolder.addChild(component)
        allChildren.add(component)

        needsUpdate = true
    }

    override fun insertChildAt(component: UIComponent, index: Int) = apply {
        if (index < 0 || index > children.size) {
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

        needsUpdate = true
    }

    override fun removeChild(component: UIComponent) = apply {
        if (component == scrollSVGComponent) {
            super.removeChild(component)
            return@apply
        }

        actualHolder.removeChild(component)
        allChildren.remove(component)

        if (allChildren.isEmpty())
            actualHolder.addChild(emptyText)

        needsUpdate = true
    }

    override fun clearChildren() = apply {
        allChildren.clear()
        actualHolder.clearChildren()
        actualHolder.addChild(emptyText)

        needsUpdate = true
    }

    override fun alwaysDrawChildren(): Boolean {
        return true
    }

    override fun <T> childrenOfType(clazz: Class<T>): List<T> {
        return actualHolder.childrenOfType(clazz)
    }

    override fun mouseClick(mouseX: Double, mouseY: Double, button: Int) {
        actualHolder.mouseClick(mouseX, mouseY, button)
    }

    override fun hitTest(x: Float, y: Float): UIComponent {
        return actualHolder.hitTest(x, y)
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
        actualHolder.children.clear()
        actualHolder.children.addAll(components.ifEmpty { listOf(emptyText) })
        actualHolder.children.forEach { it.parent = actualHolder }

        allChildren.clear()
        allChildren.addAll(actualHolder.children)

        needsUpdate = true
    }

    private fun ClosedFloatingPointRange<Double>.width() = abs(this.start - this.endInclusive)
    private fun ClosedFloatingPointRange<Float>.width() = abs(this.start - this.endInclusive)

    class DefaultScrollBar(isHorizontal: Boolean) : UIComponent() {
        val grip: UIComponent

        init {
            if (isHorizontal) {
                constrain {
                    y = 2.pixels(alignOpposite = true)
                    width = FillConstraint()
                    height = 10.pixels()
                }

                val container = UIContainer().constrain {
                    x = 2.pixels()
                    y = CenterConstraint()
                    width = RelativeConstraint() - 4.pixels()
                    height = 4.pixels()
                } childOf this

                grip = UIBlock(Color(70, 70, 70)).constrain {
                    x = 0.pixels(alignOpposite = true)
                    y = CenterConstraint()
                    width = 30.pixels()
                    height = 3.pixels()
                } childOf container
            } else {
                constrain {
                    x = 2.pixels(alignOpposite = true)
                    width = 10.pixels()
                    height = FillConstraint()
                }

                val container = UIContainer().constrain {
                    x = CenterConstraint()
                    y = 2.pixels()
                    width = 4.pixels()
                    height = RelativeConstraint() - 4.pixels()
                } childOf this

                grip = UIBlock(Color(70, 70, 70)).constrain {
                    x = CenterConstraint()
                    y = 0.pixels(alignOpposite = true)
                    width = 3.pixels()
                    height = 30.pixels()
                } childOf container
            }
        }
    }

    companion object {
        private val scrollSVG = SVGParser.parseFromResource("/svg/scroll.svg")
    }
}
