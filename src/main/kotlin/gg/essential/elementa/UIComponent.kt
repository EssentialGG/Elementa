package gg.essential.elementa

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.*
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.effects.Effect
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.events.UIClickEvent
import gg.essential.elementa.events.UIScrollEvent
import gg.essential.elementa.font.FontProvider
import gg.essential.elementa.state.v2.ReferenceHolder
import gg.essential.elementa.utils.*
import gg.essential.elementa.utils.requireMainThread
import gg.essential.elementa.utils.requireState
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMouse
import gg.essential.universal.UResolution
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.math.*
import kotlin.reflect.KMutableProperty0

/**
 * UIComponent is the base of all drawing, meaning
 * everything visible on the screen is a UIComponent.
 */
abstract class UIComponent : Observable(), ReferenceHolder {

    // Except when debugging, the component name does not need to be resolved
    // and the performance hit of eagerly resolving the name via the java class
    // is non-negligible. To improve this behavior, the value is defaulted to
    // some marker value and resolved via java class only when needed. If
    // the component name is defined via the component delegated properly,
    // then the call to the java class is eliminated entirely.
    var componentName: String = defaultComponentName
        get() {
            if (field === defaultComponentName) {
                field = this.javaClass.simpleName
            }
            return field
        }
    open val children = CopyOnWriteArrayList<UIComponent>().observable()
    val effects = mutableListOf<Effect>()

    private var childrenLocked = 0
    init {
        children.addObserver { _, _ -> requireChildrenUnlocked() }
        children.addObserver { _, event -> setWindowCacheOnChangedChild(event) }
    }

    open lateinit var parent: UIComponent

    open val hasParent: Boolean
        get() = ::parent.isInitialized

    var constraints = UIConstraints(this)
        set(value) {
            field = value
            setChanged()
            notifyObservers(constraints)
        }

    var lastDraggedMouseX: Double? = null
    var lastDraggedMouseY: Double? = null

    /* Bubbling Events */
    var mouseScrollListeners = mutableListOf<UIComponent.(UIScrollEvent) -> Unit>()
    val mouseClickListeners = mutableListOf<UIComponent.(UIClickEvent) -> Unit>()
    private var lastClickTime = System.currentTimeMillis()
    private var lastClickCount = 0

    /* Non-Bubbling Events */
    val mouseReleaseListeners = mutableListOf<UIComponent.() -> Unit>()
    val mouseEnterListeners = mutableListOf<UIComponent.() -> Unit>()
    val mouseLeaveListeners = mutableListOf<UIComponent.() -> Unit>()
    val mouseDragListeners = mutableListOf<UIComponent.(mouseX: Float, mouseY: Float, button: Int) -> Unit>()
    val keyTypedListeners = mutableListOf<UIComponent.(typedChar: Char, keyCode: Int) -> Unit>()

    private var currentlyHovered = false
    private val beforeHideAnimations = mutableListOf<AnimatingConstraints.() -> Unit>()
    private val afterUnhideAnimations = mutableListOf<AnimatingConstraints.() -> Unit>()
    private val onFocusActions = mutableListOf<UIComponent.() -> Unit>()
    private val onFocusLostActions = mutableListOf<UIComponent.() -> Unit>()

    /**
     * Required for [unhide] so it can insert this component
     * back into the same position
     */
    private var indexInParent = 0

    // Field animation API
    private val fieldAnimationQueue = ConcurrentLinkedDeque<FieldAnimationComponent<*>>()

    // Timer API
    private val activeTimers = mutableMapOf<Int, Timer>()

    // We have to store stopped timers separately to avoid ConcurrentModificationException
    private val stoppedTimers = mutableSetOf<Int>()
    private var nextTimerId = 0

    private var heldReferences = mutableListOf<Any>()

    protected var isInitialized = false
    private var isFloating = false

    private var didCallBeforeDraw = false
    private var warnedAboutBeforeDraw = false

    internal var cachedWindow: Window? = null

    private fun setWindowCacheOnChangedChild(possibleEvent: Any) {
        @Suppress("UNCHECKED_CAST")
        when (val event = possibleEvent as? ObservableListEvent<UIComponent> ?: return) {
            is ObservableAddEvent -> event.element.value.recursivelySetWindowCache(Window.ofOrNull(this))
            is ObservableRemoveEvent -> event.element.value.recursivelySetWindowCache(null)
            is ObservableClearEvent -> event.oldChildren.forEach { it.recursivelySetWindowCache(null) }
        }
    }

    private fun recursivelySetWindowCache(window: Window?) {
        cachedWindow = window
        children.forEach { it.recursivelySetWindowCache(window) }
    }

    protected fun requireChildrenUnlocked() {
        requireState(childrenLocked == 0, "Cannot modify children while iterating over them.")
    }

    private inline fun <R> withChildrenLocked(block: () -> R): R {
        childrenLocked++
        try {
            return block()
        } finally {
            childrenLocked--
        }
    }

    private inline fun forEachChild(block: (UIComponent) -> Unit) {
        withChildrenLocked {
            children.forEach(block)
        }
    }

    /**
     * Adds [component] to this component's children tree,
     * as well as sets [component]'s parent to this component.
     */
    open fun addChild(component: UIComponent) = apply {
        component.parent = this
        children.add(component)
    }

    /**
     * Helper for inserting a child at a specific index in the
     * children list. If a bad index is given to the method,
     * it logs an error message and returns without modifying
     * this component.
     */
    open fun insertChildAt(component: UIComponent, index: Int) = apply {
        if (index < 0 || index > children.size) {
            println("Bad index given to insertChildAt (index: $index, children size: ${children.size}")
            return@apply
        }

        component.parent = this
        children.add(index, component)
    }

    /**
     * Helper for inserting a child before an existing child.
     * If the targetComponent is not a child of this component,
     * the method logs an error and returns without modifying
     * this component.
     */
    open fun insertChildBefore(newComponent: UIComponent, targetComponent: UIComponent) = apply {
        val indexOfExisting = children.indexOf(targetComponent)
        if (indexOfExisting == -1) {
            println("targetComponent given to insertChildBefore is not a child of this component")
            return@apply
        }

        newComponent.parent = this
        children.add(indexOfExisting, newComponent)
    }

    /**
     * Helper for inserting a child after an existing child.
     * If the targetComponent is not a child of this component,
     * the method logs an error and returns without modifying
     * this component.
     */
    open fun insertChildAfter(newComponent: UIComponent, targetComponent: UIComponent) = apply {
        val indexOfExisting = children.indexOf(targetComponent)
        if (indexOfExisting == -1) {
            println("targetComponent given to insertChildAfter is not a child of this component")
            return@apply
        }

        newComponent.parent = this
        children.add(indexOfExisting + 1, newComponent)
    }

    /**
     * Helper for replacing a child with another child. If
     * the componentToReplace is not a child of this component,
     * the method logs an error and returns without modifying
     * this component.
     */
    open fun replaceChild(newComponent: UIComponent, componentToReplace: UIComponent) = apply {
        val indexOfExisting = children.indexOf(componentToReplace)
        if (indexOfExisting == -1) {
            println("componentToReplace given to replaceChild is not a child of this component")
            return@apply
        }

        newComponent.parent = this
        children.removeAt(indexOfExisting)
        children.add(indexOfExisting, newComponent)
    }

    /**
     * Wrapper for [addChild].
     */
    open fun addChildren(vararg components: UIComponent) = apply {
        components.forEach { addChild(it) }
    }

    /**
     * Remove's [component] from this component's children, effectively
     * removing it from the hierarchy tree.
     *
     * However, [component]'s parent still references this.
     */
    open fun removeChild(component: UIComponent) = apply {
        children.remove(component)
    }

    /**
     * Removes all children, according to the same rules as [removeChild]
     */
    open fun clearChildren() = apply {
        children.clear()
    }

    /**
     * Kotlin wrapper for [childrenOfType]
     */
    inline fun <reified T> childrenOfType() = childrenOfType(T::class.java)

    /**
     * Fetches all children of this component that are instances of [clazz]
     */
    open fun <T> childrenOfType(clazz: Class<T>) = children.filterIsInstance(clazz)

    /**
     * Constructs an animation object specific to this component.
     *
     * A convenient Kotlin wrapper can be found at [gg.essential.elementa.dsl.animate]
     */
    fun makeAnimation() = AnimatingConstraints(this, constraints)

    /**
     * Begin animating to a previously constructed animation.
     *
     * This is handled internally by the [gg.essential.elementa.dsl.animate] dsl if used.
     */
    fun animateTo(constraints: AnimatingConstraints) {
        this.constraints = constraints
    }

    /**
     * Enables a set of effects to be applied when this component draws.
     */
    fun enableEffects(vararg effects: Effect) = apply {
        effects.forEach {
            it.bindComponent(this)
            if (isInitialized)
                it.setup()
        }
        this.effects.addAll(effects)
    }

    /**
     * Enables a single effect to be applied when the component draws.
     */
    fun enableEffect(effect: Effect) = apply {
        effect.bindComponent(this)
        if (isInitialized)
            effect.setup()
        this.effects.add(effect)
    }

    inline fun <reified T> removeEffect() {
        this.effects.removeIf { it is T }
    }

    fun <T : Effect> removeEffect(clazz: Class<T>) {
        this.effects.removeIf { clazz.isInstance(it) }
    }

    fun removeEffect(effect: Effect) {
        this.effects.remove(effect)
    }

    fun setChildOf(parent: UIComponent) = apply {
        parent.addChild(this)
    }

    fun setX(constraint: XConstraint) = apply {
        this.constraints.withX(constraint)
    }

    fun setY(constraint: YConstraint) = apply {
        this.constraints.withY(constraint)
    }

    fun setWidth(constraint: WidthConstraint) = apply {
        this.constraints.withWidth(constraint)
    }

    fun setHeight(constraint: HeightConstraint) = apply {
        this.constraints.withHeight(constraint)
    }

    fun setRadius(constraint: RadiusConstraint) = apply {
        this.constraints.withRadius(constraint)
    }

    fun setTextScale(constraint: HeightConstraint) = apply {
        this.constraints.withTextScale(constraint)
    }

    fun setFontProvider(fontProvider: FontProvider) = apply {
        this.constraints.fontProvider = fontProvider
    }

    fun setColor(constraint: ColorConstraint) = apply {
        this.constraints.withColor(constraint)
    }

    fun setColor(color: Color) = setColor(color.toConstraint())

    open fun getLeft() = constraints.getX()

    open fun getTop() = constraints.getY()

    open fun getRight() = getLeft() + getWidth()

    open fun getBottom() = getTop() + getHeight()

    open fun getWidth() = constraints.getWidth()

    open fun getHeight() = constraints.getHeight()

    open fun getRadius() = constraints.getRadius()

    open fun getTextScale() = constraints.getTextScale()

    open fun getFontProvider() = constraints.fontProvider

    open fun getColor() = constraints.getColor()

    open fun isPositionCenter(): Boolean {
        return false
    }

    /**
     * Checks if the player's mouse is currently on top of this component.
     *
     * It simply checks the bounds of this component's constraints (i.e. x,y and width,height).
     * If this component has children outside of its parent's bounds (which probably is not a good idea anyways...)
     * that are being hovered, it will NOT consider this component as hovered.
     */
    open fun isHovered(): Boolean {
        val (mouseX, mouseY) = getMousePosition()
        return isPointInside(mouseX, mouseY)
    }

    protected fun getMousePosition(): Pair<Float, Float> {
        return pixelCoordinatesToPixelCenter(UMouse.Scaled.x, UMouse.Scaled.y).let { (x, y) -> x.toFloat() to y.toFloat() }
    }

    internal fun pixelCoordinatesToPixelCenter(mouseX: Double, mouseY: Double): Pair<Double, Double> {
        // Move the position of a click to the center of a pixel. See [ElementaVersion.v2] for more info
        return if ((Window.ofOrNull(this)?.version ?: ElementaVersion.v0) >= ElementaVersion.v2) {
            val halfPixel = 0.5 / UResolution.scaleFactor
            mouseX + halfPixel to mouseY + halfPixel
        } else {
            mouseX to mouseY
        }
    }

    open fun isPointInside(x: Float, y: Float): Boolean {
        return x > getLeft()
            && x < getRight()
            && y > getTop()
            && y < getBottom()
    }

    open fun hitTest(x: Float, y: Float): UIComponent {
        for (i in children.lastIndex downTo 0) {
            val child = children[i]

            if (child.isPointInside(x, y)) {
                return child.hitTest(x, y)
            }
        }

        return this
    }

    open fun isChildOf(component: UIComponent): Boolean {
        var currentParent = parent

        do {
            if (currentParent == component)
                return true
            currentParent = currentParent.parent
        } while (currentParent.parent != currentParent)

        return false
    }

    /**
     * Called once before the component's first draw. This method can
     * be used to do any initialization that dependent on the component
     * hierarchy (such as calls to getWidth/getHeight/etc).
     */
    open fun afterInitialization() {
        effects.forEach { it.setup() }
    }

    @Deprecated(UMatrixStack.Compat.DEPRECATED, ReplaceWith("draw(matrixStack)"))
    open fun draw() = draw(UMatrixStack.Compat.get())

    @Suppress("DEPRECATION")
    fun drawCompat(matrixStack: UMatrixStack) = UMatrixStack.Compat.runLegacyMethod(matrixStack) { draw() }

    /**
     * Does the actual drawing for this component, meant to be overridden by specific components.
     * Also does some housekeeping dealing with hovering and effects.
     */
    open fun draw(matrixStack: UMatrixStack) {
        if (ElementaVersion.active < ElementaVersion.v4) {
            if (!isInitialized) {
                isInitialized = true
                afterInitialization()
            }
        }
        if (!didCallBeforeDraw && !warnedAboutBeforeDraw) {
            warnedAboutBeforeDraw = true
            handleInvalidUsage("${javaClass.name} failed to call `beforeDraw` at the start of its `draw` method. " +
                "Consider extending UIContainer if you do not wish to override the draw method. " +
                "If you do need to override it, then be sure to call `beforeDraw` from it before you do any drawing.")
        }
        didCallBeforeDraw = false

        // Draw colored outline around the components
        if (elementaDebug) {
            drawDebugOutline(
                matrixStack,
                getLeft().toDouble(), getTop().toDouble(),
                getRight().toDouble(), getBottom().toDouble(),
                this
            )
        }

        beforeChildrenDrawCompat(matrixStack)

        val parentWindow = Window.of(this)

        this.forEachChild { child ->
            if (child.isFloating) return@forEachChild

            // If the child is outside the current viewport, don't waste time drawing
            if (!this.alwaysDrawChildren() && !parentWindow.isAreaVisible(
                    child.getLeft().toDouble(),
                    child.getTop().toDouble(),
                    child.getRight().toDouble(),
                    child.getBottom().toDouble()
                )
            ) return@forEachChild

            child.drawCompat(matrixStack)
        }

        if (this is Window)
            drawFloatingComponents(matrixStack)

        afterDrawCompat(matrixStack)
    }

    open fun beforeDraw(matrixStack: UMatrixStack) {
        if (didCallBeforeDraw && !warnedAboutBeforeDraw) {
            warnedAboutBeforeDraw = true
            val advice = if (this is UIContainer) {
                "It should not be extending UIContainer if it overrides `draw` and calls `beforeDraw` on its own."
            } else {
                "Make sure that none of its super classes already call `beforeDraw`."
            }
            handleInvalidUsage("${javaClass.name} called `beforeDraw` more than once without a call to `draw`. $advice")
        }
        didCallBeforeDraw = true

        if (ElementaVersion.active >= ElementaVersion.v4) {
            if (!isInitialized) {
                isInitialized = true
                afterInitialization()
            }
        }

        effects.forEach { it.beforeDraw(matrixStack) }
    }

    open fun afterDraw(matrixStack: UMatrixStack) {
        if (ElementaVersion.active >= ElementaVersion.v3) {
            effects.asReversed().forEach { it.afterDraw(matrixStack) }
        } else {
            effects.forEach { it.afterDraw(matrixStack) }
        }
    }

    open fun beforeChildrenDraw(matrixStack: UMatrixStack) {
        effects.forEach { it.beforeChildrenDraw(matrixStack) }
    }

    @Deprecated(UMatrixStack.Compat.DEPRECATED, ReplaceWith("beforeDraw(matrixStack)"))
    open fun beforeDraw() = beforeDraw(UMatrixStack.Compat.get())

    @Deprecated(UMatrixStack.Compat.DEPRECATED, ReplaceWith("afterDraw(matrixStack)"))
    open fun afterDraw() = afterDraw(UMatrixStack.Compat.get())

    @Deprecated(UMatrixStack.Compat.DEPRECATED, ReplaceWith("beforeChildrenDraw(matrixStack)"))
    open fun beforeChildrenDraw() = beforeChildrenDraw(UMatrixStack.Compat.get())

    @Suppress("DEPRECATION")
    fun beforeDrawCompat(matrixStack: UMatrixStack) = UMatrixStack.Compat.runLegacyMethod(matrixStack) { beforeDraw() }

    @Suppress("DEPRECATION")
    fun afterDrawCompat(matrixStack: UMatrixStack) = UMatrixStack.Compat.runLegacyMethod(matrixStack) { afterDraw() }

    @Suppress("DEPRECATION")
    fun beforeChildrenDrawCompat(matrixStack: UMatrixStack) = UMatrixStack.Compat.runLegacyMethod(matrixStack) { beforeChildrenDraw() }

    open fun mouseMove(window: Window) {
        val hovered = isHovered() && window.hoveredFloatingComponent.let {
            it == null || it == this || isComponentInParentChain(it)
        }

        if (hovered && !currentlyHovered) {
            for (listener in mouseEnterListeners)
                this.listener()
            currentlyHovered = true
        } else if (!hovered && currentlyHovered) {
            for (listener in mouseLeaveListeners)
                this.listener()
            currentlyHovered = false
        }

        this.forEachChild { it.mouseMove(window) }
    }

    /**
     * Runs the set [onMouseClick] method for the component and it's children.
     * Use this in the proper mouse click event to cascade all component's mouse click events.
     * Most common use is on the [Window] object.
     */
    open fun mouseClick(mouseX: Double, mouseY: Double, button: Int) {
        val clicked = hitTest(mouseX.toFloat(), mouseY.toFloat())

        lastDraggedMouseX = mouseX
        lastDraggedMouseY = mouseY
        lastClickCount = if (System.currentTimeMillis() - lastClickTime < 500) lastClickCount + 1 else 1
        lastClickTime = System.currentTimeMillis()

        clicked.fireClickEvent(
            UIClickEvent(
                mouseX.toFloat(),
                mouseY.toFloat(),
                button,
                clicked,
                clicked,
                lastClickCount
            )
        )
    }

    protected fun fireClickEvent(event: UIClickEvent) {
        for (listener in mouseClickListeners) {
            this.listener(event)

            if (event.propagationStoppedImmediately) return
        }

        if (!event.propagationStopped && parent != this) {
            parent.fireClickEvent(event.copy(currentTarget = parent))
        }
    }

    /**
     * Runs the set [onMouseRelease] method for the component and it's children.
     * Use this in the proper mouse release event to cascade all component's mouse release events.
     * Most common use is on the [Window] object.
     */
    open fun mouseRelease() {
        for (listener in mouseReleaseListeners)
            this.listener()

        lastDraggedMouseX = null
        lastDraggedMouseY = null

        this.forEachChild { it.mouseRelease() }
    }

    /**
     * Runs the set [onMouseScroll] method for the component and it's children.
     * Use this in the proper mouse scroll event to cascade all component's mouse scroll events.
     * Most common use is on the [Window] object.
     */
    open fun mouseScroll(delta: Double) {
        if (delta == 0.0) return

        for (i in children.lastIndex downTo 0) {
            val child = children[i]

            if (child.isHovered()) {
                return child.mouseScroll(delta)
            }
        }

        fireScrollEvent(UIScrollEvent(delta, this, this))
    }

    open fun onWindowResize() {
        constraints.width.recalculate = true
        constraints.height.recalculate = true
        constraints.x.recalculate = true
        constraints.y.recalculate = true
        constraints.radius.recalculate = true
        constraints.textScale.recalculate = true
        constraints.color.recalculate = true
        constraints.fontProvider.recalculate = true

        this.forEachChild { it.onWindowResize() }
    }

    protected fun fireScrollEvent(event: UIScrollEvent) {
        for (listener in mouseScrollListeners) {
            this.listener(event)

            if (event.propagationStoppedImmediately) return
        }

        if (!event.propagationStopped && parent != this) {
            parent.fireScrollEvent(event.copy(currentTarget = parent))
        }
    }


    @Deprecated(
        "You no longer need to call mouseDrag manually, Elementa handles it internally.",
        level = DeprecationLevel.ERROR
    )
    open fun mouseDrag(mouseX: Int, mouseY: Int, button: Int) {
        // no-op
    }

    @Deprecated(
        "Replaced by override using Float for coordinates.",
        ReplaceWith("dragMouse(mouseX.toFloat(), mouseY.toFloat(), button)")
    )
    @Suppress("DEPRECATION")
    open fun dragMouse(mouseX: Int, mouseY: Int, button: Int) {
        doDragMouse(mouseX.toFloat(), mouseY.toFloat(), button) { dragMouse(mouseX, mouseY, button) }
    }

    /**
     * Runs the set [onMouseDrag] method for the component and it's children.
     * Use this in the proper mouse drag event to cascade all component's mouse scroll events.
     * Most common use is on the [Window] object.
     *
     * Note: This method is only called by [Window]s using an [ElementaVersion] of 2 or greater. Older versions will
     *       only call the deprecated integer overload.
     */
    open fun dragMouse(mouseX: Float, mouseY: Float, button: Int) {
        doDragMouse(mouseX, mouseY, button) { dragMouse(mouseX, mouseY, button) }
    }

    private inline fun doDragMouse(mouseX: Float, mouseY: Float, button: Int, superCall: UIComponent.() -> Unit) {
        if (lastDraggedMouseX == mouseX.toDouble() && lastDraggedMouseY == mouseY.toDouble())
            return

        lastDraggedMouseX = mouseX.toDouble()
        lastDraggedMouseY = mouseY.toDouble()

        val relativeX = mouseX - getLeft()
        val relativeY = mouseY - getTop()

        for (listener in mouseDragListeners)
            this.listener(relativeX, relativeY, button)

        this.forEachChild { it.superCall() }
    }

    open fun keyType(typedChar: Char, keyCode: Int) {
        for (listener in keyTypedListeners)
            this.listener(typedChar, keyCode)
    }

    open fun animationFrame() {
        constraints.animationFrame()

        effects.forEach(Effect::animationFrame)
        this.children.forEach(UIComponent::animationFrame)

        // Process field animations
        val queueIterator = fieldAnimationQueue.iterator()
        queueIterator.forEachRemaining {
            it.animationFrame()
            if (it.isComplete())
                queueIterator.remove()
        }

        // Process timers
        val timerIterator = activeTimers.iterator()
        timerIterator.forEachRemaining { (id, timer) ->
            if (id in stoppedTimers)
                return@forEachRemaining

            val time = System.currentTimeMillis()
            timer.timeLeft -= (time - timer.lastTime)
            timer.lastTime = time

            if (!timer.hasDelayed && timer.timeLeft <= 0L) {
                timer.hasDelayed = true
                timer.timeLeft += timer.interval
            }

            while (timer.timeLeft <= 0L && id !in stoppedTimers) {
                timer.callback(id)
                timer.timeLeft += timer.interval
            }
        }

        stoppedTimers.forEach { activeTimers.remove(it) }
    }

    open fun alwaysDrawChildren(): Boolean {
        return false
    }

    fun depth(): Int {
        var current = this
        var depth = 0

        while (current !is Window && current.hasParent && current.parent != current) {
            current = current.parent
            depth++
        }

        if (current !is Window)
            throw IllegalStateException("No window parent? It's possible you haven't called Window.addChild() at this point in time.")

        return depth
    }

    /**
     * Adds a method to be run when mouse is clicked within the component.
     */
    fun onMouseClick(method: UIComponent.(event: UIClickEvent) -> Unit) = apply {
        mouseClickListeners.add(method)
    }

    /**
     * Adds a method to be run when mouse is clicked within the component.
     */
    fun onMouseClickConsumer(method: Consumer<UIClickEvent>) = apply {
        mouseClickListeners.add { method.accept(it) }
    }

    /**
     * Adds a method to be run when mouse is released within the component.
     */
    fun onMouseRelease(method: UIComponent.() -> Unit) = apply {
        mouseReleaseListeners.add(method)
    }

    /**
     * Adds a method to be run when mouse is released within the component.
     */
    fun onMouseReleaseRunnable(method: Runnable) = apply {
        mouseReleaseListeners.add { method.run() }
    }

    /**
     * Adds a method to be run when mouse is dragged anywhere on screen.
     * This does not check if mouse is in component.
     */
    fun onMouseDrag(method: UIComponent.(mouseX: Float, mouseY: Float, mouseButton: Int) -> Unit) = apply {
        mouseDragListeners.add(method)
    }

    /**
     * Adds a method to be run when mouse is dragged anywhere on screen.
     * This does not check if mouse is in component.
     */
    fun onMouseDragConsumer(method: TriConsumer<Float, Float, Int>) = apply {
        mouseDragListeners.add { t: Float, u: Float, v: Int -> method.accept(t, u, v) }
    }

    /**
     * Adds a method to be run when mouse enters the component.
     */
    fun onMouseEnter(method: UIComponent.() -> Unit) = apply {
        mouseEnterListeners.add(method)
    }

    /**
     * Adds a method to be run when mouse enters the component.
     */
    fun onMouseEnterRunnable(method: Runnable) = apply {
        mouseEnterListeners.add { method.run() }
    }

    /**
     * Adds a method to be run when mouse leaves the component.
     */
    fun onMouseLeave(method: UIComponent.() -> Unit) = apply {
        mouseLeaveListeners.add(method)
    }

    /**
     * Adds a method to be run when mouse leaves the component.
     */
    fun onMouseLeaveRunnable(method: Runnable) = apply {
        mouseLeaveListeners.add { method.run() }
    }

    /**
     * Adds a method to be run when mouse scrolls while in the component.
     */
    fun onMouseScroll(method: UIComponent.(UIScrollEvent) -> Unit) = apply {
        mouseScrollListeners.add(method)
    }

    /**
     * Adds a method to be run when mouse scrolls while in the component.
     */
    fun onMouseScrollConsumer(method: Consumer<UIScrollEvent>) = apply {
        mouseScrollListeners.add { method.accept(it) }
    }

    fun onKeyType(method: UIComponent.(typedChar: Char, keyCode: Int) -> Unit) = apply {
        keyTypedListeners.add(method)
    }

    fun onKeyTypeConsumer(method: BiConsumer<Char, Int>) {
        keyTypedListeners.add { t: Char, u: Int -> method.accept(t, u) }
    }

    /*
     Hide API
     */

    /**
     * Hides this component. Behind the scenes, "hiding" entails removal of this component
     * from the entire hierarchy, leading to changes in sibling/children relationships.
     *
     * This also means hidden components will no longer receive events, or be drawn in any way.
     *
     * NOTE: Make sure to release any focus on this component, because it will likely cause
     * unintended side effects.
     *
     * @param instantly normally, hiding a component will run its before-hide
     * animations, and when they are complete, it will fully remove the component.
     * If [instantly] is true, it will skip the animation cycle and instantly remove the component.
     */
    @JvmOverloads
    fun hide(instantly: Boolean = false) {
        if (isInitialized) {
            requireMainThread()
        }

        if (instantly) {
            indexInParent = parent.children.indexOf(this@UIComponent)
            parent.removeChild(this@UIComponent)
            return
        }

        animate {
            for (animation in beforeHideAnimations)
                this.animation()

            val comp = this.completeAction
            onComplete {
                comp()

                indexInParent = parent.children.indexOf(this@UIComponent)
                parent.removeChild(this@UIComponent)
            }
        }
    }

    /**
     * Re-enables this component. This will do the opposite of [hide] and re-add this component
     * to the hierarchy, underneath the same parent.
     */
    fun unhide(useLastPosition: Boolean = true) {
        if (isInitialized) {
            requireMainThread()
        }

        if (parent.children.contains(this)) {
            return
        }

        if (useLastPosition && indexInParent >= 0 && indexInParent < parent.children.size) {
            parent.children.add(indexInParent, this@UIComponent)
        } else {
            parent.children.add(this@UIComponent)
        }

        animate {
            for (animation in afterUnhideAnimations)
                this.animation()
        }
    }

    fun animateBeforeHide(animation: AnimatingConstraints.() -> Unit) = apply {
        beforeHideAnimations.add(animation)
    }

    fun animateAfterUnhide(animation: AnimatingConstraints.() -> Unit) = apply {
        afterUnhideAnimations.add(animation)
    }

    /**
     * Focus API
     */

    fun grabWindowFocus() {
        Window.of(this).focus(this)
    }

    fun onFocus(listener: UIComponent.() -> Unit) = apply {
        onFocusActions.add(listener)
    }

    fun focus() {
        for (listener in onFocusActions)
            this.listener()
    }

    fun releaseWindowFocus() {
        Window.of(this).unfocus()
    }

    fun onFocusLost(listener: UIComponent.() -> Unit) = apply {
        onFocusLostActions.add(listener)
    }

    fun loseFocus() {
        for (listener in onFocusLostActions)
            this.listener()
    }

    fun hasFocus(): Boolean = Window.of(this).focusedComponent == this

    /**
     * Floating API
     */

    fun setFloating(floating: Boolean) {
        isFloating = floating

        if (floating) {
            Window.of(this).addFloatingComponent(this)
        } else {
            Window.of(this).removeFloatingComponent(this)
        }
    }

    /**
     * Field animation API
     */

    fun KMutableProperty0<Int>.animate(strategy: AnimationStrategy, time: Float, newValue: Int, delay: Float = 0f) {
        if (!validateAnimationFields(time, delay))
            return

        if (time == 0f) {
            this.set(newValue)
            return
        }

        val totalFrames = (time * Window.of(this@UIComponent).animationFPS).toInt()
        val totalDelay = (delay * Window.of(this@UIComponent).animationFPS).toInt()

        fieldAnimationQueue.removeIf { it.field == this }
        fieldAnimationQueue.addFirst(
            IntFieldAnimationComponent(
                this,
                strategy,
                totalFrames,
                this.get(),
                newValue,
                totalDelay
            )
        )
    }

    fun KMutableProperty0<Float>.animate(strategy: AnimationStrategy, time: Float, newValue: Float, delay: Float = 0f) {
        if (!validateAnimationFields(time, delay))
            return

        if (time == 0f) {
            this.set(newValue)
            return
        }

        val totalFrames = (time * Window.of(this@UIComponent).animationFPS).toInt()
        val totalDelay = (delay * Window.of(this@UIComponent).animationFPS).toInt()

        fieldAnimationQueue.removeIf { it.field == this }
        fieldAnimationQueue.addFirst(
            FloatFieldAnimationComponent(
                this,
                strategy,
                totalFrames,
                this.get(),
                newValue,
                totalDelay
            )
        )
    }

    fun KMutableProperty0<Long>.animate(strategy: AnimationStrategy, time: Float, newValue: Long, delay: Float = 0f) {
        if (!validateAnimationFields(time, delay))
            return

        if (time == 0f) {
            this.set(newValue)
            return
        }

        val totalFrames = (time * Window.of(this@UIComponent).animationFPS).toInt()
        val totalDelay = (delay * Window.of(this@UIComponent).animationFPS).toInt()

        fieldAnimationQueue.removeIf { it.field == this }
        fieldAnimationQueue.addFirst(
            LongFieldAnimationComponent(
                this,
                strategy,
                totalFrames,
                this.get(),
                newValue,
                totalDelay
            )
        )
    }

    fun KMutableProperty0<Double>.animate(
        strategy: AnimationStrategy,
        time: Float,
        newValue: Double,
        delay: Float = 0f
    ) {
        if (!validateAnimationFields(time, delay))
            return

        if (time == 0f) {
            this.set(newValue)
            return
        }

        val totalFrames = (time * Window.of(this@UIComponent).animationFPS).toInt()
        val totalDelay = (delay * Window.of(this@UIComponent).animationFPS).toInt()

        fieldAnimationQueue.removeIf { it.field == this }
        fieldAnimationQueue.addFirst(
            DoubleFieldAnimationComponent(
                this,
                strategy,
                totalFrames,
                this.get(),
                newValue,
                totalDelay
            )
        )
    }

    fun KMutableProperty0<Color>.animate(strategy: AnimationStrategy, time: Float, newValue: Color, delay: Float = 0f) {
        if (!validateAnimationFields(time, delay))
            return

        if (time == 0f) {
            this.set(newValue)
            return
        }

        val totalFrames = (time * Window.of(this@UIComponent).animationFPS).toInt()
        val totalDelay = (delay * Window.of(this@UIComponent).animationFPS).toInt()

        fieldAnimationQueue.removeIf { it.field == this }
        fieldAnimationQueue.addFirst(
            ColorFieldAnimationComponent(
                this,
                strategy,
                totalFrames,
                this.get(),
                newValue,
                totalDelay
            )
        )
    }

    fun KMutableProperty0<*>.stopAnimating() {
        fieldAnimationQueue.removeIf { it.field == this }
    }

    private fun validateAnimationFields(time: Float, delay: Float): Boolean {
        if (time < 0f) {
            println("time parameter of field animation call cannot be less than 0")
            return false
        }
        if (delay < 0f) {
            println("delay parameter of field animation call cannot be less than 0")
            return false
        }
        return true
    }

    private fun isComponentInParentChain(target: UIComponent): Boolean {
        var component: UIComponent = this
        while (component.hasParent && component !is Window) {
            component = component.parent
            if (component == target)
                return true
        }

        return false
    }

    /**
     * Timer API
     */

    fun startTimer(interval: Long, delay: Long = 0, callback: (Int) -> Unit): Int {
        val id = nextTimerId++
        activeTimers[id] = Timer(delay, interval, callback)
        return id
    }

    fun stopTimer(id: Int) = stoppedTimers.add(id)

    fun timer(interval: Long, delay: Long = 0, callback: (Int) -> Unit): () -> Unit {
        val id = startTimer(interval, delay, callback)
        return { stopTimer(id) }
    }

    fun startDelay(delay: Long, callback: () -> Unit): Int {
        return startTimer(delay) {
            callback()
            stopTimer(it)
        }
    }

    fun stopDelay(id: Int) = stopTimer(id)

    fun delay(delay: Long, callback: () -> Unit): () -> Unit {
        val id = startDelay(delay, callback)
        return { stopDelay(id) }
    }

    private class Timer(delay: Long, val interval: Long, val callback: (Int) -> Unit) {
        var hasDelayed = false
        var timeLeft = delay
        var lastTime = System.currentTimeMillis()

        init {
            if (delay == 0L) {
                hasDelayed = true
                timeLeft = interval
            }
        }
    }

    override fun holdOnto(listener: Any): () -> Unit {
        heldReferences.add(listener)
        return { heldReferences.remove(listener) }
    }

    companion object {
        // Default value for componentName used as marker for lazy init.
        private val defaultComponentName = String()

        val DEBUG_OUTLINE_WIDTH = System.getProperty("elementa.debug.width")?.toDoubleOrNull() ?: 2.0

        /**
         * Draws a colored outline around a given area
         */
        internal fun drawDebugOutline(matrixStack: UMatrixStack,left: Double, top: Double, right: Double, bottom: Double, component: UIComponent) {
            if (ScissorEffect.currentScissorState != null) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST)
            }

            val color = getDebugColor(component.depth(), (component.parent.hashCode() / PI) % PI)

            // Top outline block
            UIBlock.drawBlock(
                matrixStack,
                color,
                left - DEBUG_OUTLINE_WIDTH,
                top - DEBUG_OUTLINE_WIDTH,
                right + DEBUG_OUTLINE_WIDTH,
                top
            )

            // Right outline block
            UIBlock.drawBlock(matrixStack, color, right, top, right + DEBUG_OUTLINE_WIDTH, bottom)

            // Bottom outline block
            UIBlock.drawBlock(
                matrixStack,
                color,
                left - DEBUG_OUTLINE_WIDTH,
                bottom,
                right + DEBUG_OUTLINE_WIDTH,
                bottom + DEBUG_OUTLINE_WIDTH
            )

            // Left outline block
            UIBlock.drawBlock(matrixStack, color, left - DEBUG_OUTLINE_WIDTH, top, left, bottom)

            if (ScissorEffect.currentScissorState != null) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST)
            }
        }

        private fun getDebugColor(depth: Int, offset: Double): Color {
            val step = depth.toDouble() / PI + offset

            val red = ((sin((step)) + 0.75) * 170).toInt().coerceIn(0..255)
            val green = ((sin(step + 2 * Math.PI / 3) + 0.75) * 170).toInt().coerceIn(0..255)
            val blue = ((sin(step + 4 * Math.PI / 3) + 0.75) * 170).toInt().coerceIn(0..255)
            return Color(red, green, blue, 255)
        }

        /**
         * Hints a number with respect to the current GUI scale.
         */
        fun guiHint(number: Float, roundDown: Boolean): Float {
            val factor = UResolution.scaleFactor.toFloat()
            return (number * factor).let {
                if (roundDown) floor(it) else ceil(it)
            } / factor
        }

        /**
         * Hints a number with respect to the current GUI scale.
         */
        fun guiHint(number: Double, roundDown: Boolean): Double {
            val factor = UResolution.scaleFactor
            return (number * factor).let {
                if (roundDown) floor(it) else ceil(it)
            } / factor
        }

        internal fun getMouseX(): Float {
            return UMouse.Scaled.x.toFloat()
        }

        internal fun getMouseY(): Float {
            return UMouse.Scaled.y.toFloat()
        }
    }
}
