package gg.essential.elementa.state.v2

import gg.essential.elementa.state.v2.collections.MutableTrackedList
import gg.essential.elementa.state.v2.collections.TrackedList

typealias ListState<T> = State<TrackedList<T>>
typealias MutableListState<T> = MutableState<MutableTrackedList<T>>

fun <T> State<List<T>>.toListState(): ListState<T> {
    return derivedState(MutableTrackedList(get().toMutableList())) { owner, derivedState ->
        onSetValue(owner) { newList ->
            derivedState.set { it.applyChanges(TrackedList.Change.estimate(it, newList)) }
        }
    }
}

fun <T, U> ListState<T>.mapChanges(init: (TrackedList<T>) -> U, update: (old: U, changes: Sequence<TrackedList.Change<T>>) -> U): State<U> {
    var oldList = get()
    return derivedState(init(oldList)) { owner, derivedState ->
        onSetValue(owner) { newList ->
            val changes = newList.getChangesSince(oldList).also { oldList = newList }
            derivedState.set { update(it, changes) }
        }
    }
}

fun <T, U> ListState<T>.mapChange(init: (TrackedList<T>) -> U, update: (old: U, change: TrackedList.Change<T>) -> U): State<U> =
    mapChanges(init) { old, changes -> changes.fold(old, update) }

fun <T> listStateOf(vararg elements: T): ListState<T> =
    stateOf(MutableTrackedList(mutableListOf(*elements)))

fun <T> mutableListStateOf(vararg elements: T): MutableListState<T> =
    mutableStateOf(MutableTrackedList(mutableListOf(*elements)))

fun <T> MutableListState<T>.set(index: Int, element: T) = set { it.set(index, element) }
fun <T> MutableListState<T>.setAll(newList: List<T>) = set { it.applyChanges(TrackedList.Change.estimate(it, newList)) }
fun <T> MutableListState<T>.add(element: T) = set { it.add(element) }
fun <T> MutableListState<T>.add(index: Int, element: T) = set { it.add(index, element) }
fun <T> MutableListState<T>.addAll(elements: List<T>) = set { it.addAll(elements) }
fun <T> MutableListState<T>.remove(element: T) = set { it.remove(element) }
fun <T> MutableListState<T>.removeAt(index: Int) = set { it.removeAt(index) }
fun <T> MutableListState<T>.clear() = set { it.clear() }
