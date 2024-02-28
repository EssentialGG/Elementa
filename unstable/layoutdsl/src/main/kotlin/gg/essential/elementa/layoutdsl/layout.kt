package gg.essential.elementa.layoutdsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.state.State
import gg.essential.elementa.common.ListState
import gg.essential.elementa.common.not
import gg.essential.elementa.state.v2.*
import gg.essential.elementa.util.hoveredState
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import gg.essential.elementa.state.v2.ListState as ListStateV2
import gg.essential.elementa.state.v2.State as StateV2

class LayoutScope(
    private val component: UIComponent,
    private val parentScope: LayoutScope?,
) {
    /**
     * You should only use this for calling `toV1`, or any State-related methods that require a [gg.essential.elementa.state.v2.ReferenceHolder].
     */
    val stateScope: UIComponent
        get() = component

    private val childrenScopes = mutableListOf<LayoutScope>()

    operator fun <T : UIComponent> T.invoke(modifier: Modifier = Modifier, block: LayoutScope.() -> Unit = {}): T {
        this@LayoutScope.component.getChildModifier().applyToComponent(this)
        modifier.applyToComponent(this)

        val childScope = LayoutScope(this, this@LayoutScope)
        childrenScopes.add(childScope)

        childScope.block()

        val index = childScope.findNextIndexIn(component) ?: 0
        component.insertChildAt(this, index)

        return this
    }

    operator fun LayoutDslComponent.invoke(modifier: Modifier = Modifier) = layout(modifier)

    @Deprecated("Use Modifier.hoverScope() and Modifier.whenHovered(), instead.")
    fun hoveredState(hitTest: Boolean = true, layoutSafe: Boolean = true) = component.hoveredState(hitTest, layoutSafe)

    @Suppress("FunctionName")
    fun if_(state: State<Boolean>, cache: Boolean = true, block: LayoutScope.() -> Unit): IfDsl {
        forEach(ListState.from(state.map { if (it) listOf(Unit) else emptyList() }), cache) { block() }
        return IfDsl({ !state }, cache)
    }

    fun if_(state: StateV2<Boolean>, cache: Boolean = true, block: LayoutScope.() -> Unit): IfDsl {
        return if_(state.toV1(component), cache, block)
    }

    fun <T> ifNotNull(state: State<T?>, cache: Boolean = false, block: LayoutScope.(T) -> Unit): IfDsl {
        forEach(ListState.from(state.map { listOfNotNull(it) }), cache) { block(it) }
        return IfDsl({ state.map { it == null } }, true)
    }

    fun <T> ifNotNull(state: StateV2<T?>, cache: Boolean = false, block: LayoutScope.(T) -> Unit): IfDsl {
        return ifNotNull(state.toV1(component), cache, block)
    }

    fun if_(condition: StateByScope.() -> Boolean, cache: Boolean = false, block: LayoutScope.() -> Unit): IfDsl {
        return if_(stateBy(condition), cache, block)
    }

    fun <T> ifNotNull(stateBlock: StateByScope.() -> T?, cache: Boolean = false, block: LayoutScope.(T) -> Unit): IfDsl {
        return ifNotNull(stateBy(stateBlock), cache, block)
    }

    class IfDsl(internal val elseState: () -> State<Boolean>, internal var cache: Boolean)

    infix fun IfDsl.`else`(block: LayoutScope.() -> Unit) {
        if_(elseState(), cache, block)
    }

    /** Makes available to the inner scope the value of the given [state]. */
    fun <T> bind(state: State<T>, cache: Boolean = false, block: LayoutScope.(T) -> Unit) {
        forEach(ListState.from(state.map { listOf(it) }), cache) { block(it) }
    }

    /** Makes available to the inner scope the value of the given [state]. */
    fun <T> bind(state: StateV2<T>, cache: Boolean = false, block: LayoutScope.(T) -> Unit) {
        bind(state.toV1(component), cache, block)
    }

    /** Makes available to the inner scope the value derived from the given [stateBlock]. */
    fun <T> bind(stateBlock: StateByScope.() -> T, cache: Boolean = false, block: LayoutScope.(T) -> Unit) {
        bind(stateBy(stateBlock), cache, block)
    }

    /**
     * Repeats the inner block for each element in the given list state.
     * If the list state changes, components from old scopes are removed and new scopes are created and initialized as
     * required.
     * Order relative to other components within the same [layout] call is kept automatically at all times.
     *
     * Note that given old scopes are discarded, care must be taken to not inadvertently leak child components, e.g. via
     * listener subscriptions or other links that cannot be cleaned up automatically.
     * If the space of possible [T] is very limited, [cache] may be set to `true` to retain old scopes after they are
     * removed and to re-use them if their corresponding [T] value is re-introduced at a later time.
     * This requires that [T] be usable as a key in a HashMap.
     */
    fun <T> forEach(state: ListState<T>, cache: Boolean = false, block: LayoutScope.(T) -> Unit) {
        val forEachScope = LayoutScope(component, this@LayoutScope)
        childrenScopes.add(forEachScope)

        val cacheMap =
            if (cache) mutableMapOf<T, MutableList<LayoutScope>>()
            else null
        fun getCacheEntry(key: T) = cacheMap?.getOrPut(key) { mutableListOf() }

        fun add(index: Int, element: T) {
            val cachedScope = getCacheEntry(element)?.removeLastOrNull()
            if (cachedScope != null) {
                forEachScope.childrenScopes.add(index, cachedScope)
                if (forEachScope.isVirtualScopeMounted()) {
                    cachedScope.remount()
                }
            } else {
                val newScope = LayoutScope(component, forEachScope)
                forEachScope.childrenScopes.add(index, newScope)
                newScope.block(element)
                if (!forEachScope.isVirtualScopeMounted()) {
                    newScope.unmount()
                }
            }
        }

        fun remove(index: Int, element: T) {
            val removedScope = forEachScope.childrenScopes.removeAt(index)
            removedScope.unmount()
            getCacheEntry(element)?.add(removedScope)
        }

        fun clear(elements: List<T>) {
            forEachScope.childrenScopes.forEachIndexed { index, layoutScope ->
                layoutScope.unmount()
                getCacheEntry(elements[index])?.add(layoutScope)
            }
            forEachScope.childrenScopes.clear()
        }

        state.get().forEachIndexed(::add)
        state.onAdd(::add)
        state.onRemove(::remove)
        state.onSet { index, element, oldElement ->
            remove(index, oldElement)
            add(index, element)
        }
        state.onClear(::clear)
    }

    /**
     * StateV2 support for forEach
     */
    fun <T> forEach(list: ListStateV2<T>, cache: Boolean = false, block: LayoutScope.(T) -> Unit) =
        forEach(ListState.from(list.toV1(component)), cache, block)

    /** Whether this scope is a virtual "forEach" scope. These share their target component with their parent scope. */
    private fun isVirtual(): Boolean {
        return parentScope?.component == component
    }

    /** Whether this virtual ("forEach") scope is presently (virtually) mounted inside its parent [component]. */
    private fun isVirtualScopeMounted(): Boolean {
        val parent = parentScope ?: return true // if we don't have a parent, we can only assume that we're mounted

        // Check if this scope is currently mounted in its parent scope
        if (this !in parent.childrenScopes) {
            return false
        }

        // If the parent scope is a virtual scope as well, we can only be mounted if it is
        if (parent.isVirtual()) {
            return parent.isVirtualScopeMounted()
        }

        return true
    }

    /** Removes from [component] all components that where added within this scope. */
    private fun unmount() {
        for (childScope in childrenScopes) {
            if (childScope.component == this.component) {
                // This is a forEach scope, recurse down into its children
                childScope.unmount()
            } else {
                component.removeChild(childScope.component)
            }
        }
    }

    /** Inverse of [unmount]. Re-adds to [component] all components that where added within this scope. */
    private fun remount() {
        for (childScope in childrenScopes) {
            if (childScope.component == this.component) {
                // This is a forEach scope, recurse down into its children
                childScope.remount()
            } else {
                val index = childScope.findNextIndexIn(component) ?: 0
                component.insertChildAt(childScope.component, index)
            }
        }
    }

    /**
     * Finds the index in [parent]'s children at which a component should be inserted to end up right after [component].
     * Works even when [component] is not currently present in [parent] by recursively searching the layout tree.
     * If [parent] has no children in the layout tree, `null` is returned.
     */
    private fun findNextIndexIn(parent: UIComponent): Int? {
        /** Searches this subtree for an index. */
        fun LayoutScope.searchSubTree(range: IntProgression = childrenScopes.indices.reversed()): Int? {
            if (component == parent) {
                // This is a node in the subtree belonging to [parent] (e.g. the main scope, or a forEach scope),
                // so we recursively search the children
                for (index in range) {
                    childrenScopes[index].searchSubTree()
                        ?.let { return it }
                }
                return null
            } else {
                // Check if this child is currently present within its parent
                return parent.children.indexOf(component).takeIf { it != -1 }
            }
        }

        /** Searches by recursively traversing upwards the tree if no index can be found in this subtree. */
        fun LayoutScope.search(beforeScope: LayoutScope): Int? {
            val beforeIndex = childrenScopes.indexOf(beforeScope)

            // Check all preceding siblings
            searchSubTree((0 until beforeIndex).reversed())
                ?.let { return it }

            // If we can't find anything there, check the siblings one level up, recursively
            val parentScope = parentScope ?: return null
            // Though once we've found a scope that targets [parent], then we can stop ascending if we find a scope
            // that doesn't target [parent] (i.e. one for parent's parent) because we only want to search all scopes
            // targeting [parent].
            if (component == parent && parentScope.component != parent) {
                return null
            }
            return parentScope.search(this)
        }

        return parentScope?.search(this)?.let { it + 1 }
    }
}

@OptIn(ExperimentalContracts::class)
inline fun UIComponent.layout(modifier: Modifier = Modifier, block: LayoutScope.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    modifier.applyToComponent(this)
    LayoutScope(this, null).block()
}

interface LayoutDslComponent {
    fun LayoutScope.layout(modifier: Modifier = Modifier)
}
