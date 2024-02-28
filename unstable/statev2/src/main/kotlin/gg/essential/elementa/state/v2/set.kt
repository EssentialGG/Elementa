package gg.essential.elementa.state.v2

import gg.essential.elementa.state.v2.collections.*

typealias SetState<T> = State<TrackedSet<T>>
typealias MutableSetState<T> = MutableState<MutableTrackedSet<T>>

fun <T> State<Set<T>>.toSetState(): SetState<T> {
    return derivedState(MutableTrackedSet(get().toMutableSet())) { owner, derivedState ->
        onSetValue(owner) { newSet ->
            derivedState.set { it.applyChanges(TrackedSet.Change.estimate(it, newSet)) }
        }
    }
}

fun <T, U> SetState<T>.mapChanges(init: (TrackedSet<T>) -> U, update: (old: U, changes: Sequence<TrackedSet.Change<T>>) -> U): State<U> {
    var oldSet = get()
    return derivedState(init(oldSet)) { owner, derivedState ->
        onSetValue(owner) { newSet ->
            val changes = newSet.getChangesSince(oldSet).also { oldSet = newSet }
            derivedState.set { update(it, changes) }
        }
    }
}

fun <T, U> SetState<T>.mapChange(init: (TrackedSet<T>) -> U, update: (old: U, change: TrackedSet.Change<T>) -> U): State<U> =
    mapChanges(init) { old, changes -> changes.fold(old, update) }

fun <T> mutableSetState(vararg elements: T): MutableSetState<T> =
    mutableStateOf(MutableTrackedSet(mutableSetOf(*elements)))

fun <T> MutableSetState<T>.add(element: T) = set { it.add(element) }
fun <T> MutableSetState<T>.addAll(toAdd: Collection<T>) = set { it.addAll(toAdd) }
fun <T> MutableSetState<T>.setAll(newSet: Set<T>) = set { it.applyChanges(TrackedSet.Change.estimate(it, newSet)) }
fun <T> MutableSetState<T>.remove(element: T) = set { it.remove(element) }
fun <T> MutableSetState<T>.clear() = set { it.clear() }
