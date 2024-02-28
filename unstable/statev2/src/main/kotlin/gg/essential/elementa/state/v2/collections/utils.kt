package gg.essential.elementa.state.v2.collections

import gg.essential.elementa.state.v2.ReferenceHolder
import gg.essential.elementa.state.v2.ListState

// FIXME this is assuming there are no duplicate keys (good enough for now)
fun <T, K, V> ListState<T>.asMap(owner: ReferenceHolder, block: (T) -> Pair<K, V>): Map<K, V> {
    var oldList = get()
    val map = oldList.associateTo(mutableMapOf(), block)
    val keys = map.keys.toMutableList()
    onSetValue(owner) { newList ->
        val changes = newList.getChangesSince(oldList).also { oldList = newList }
        for (change in changes) {
            when (change) {
                is TrackedList.Add -> {
                    val (k, v) = block(change.element.value)
                    keys.add(change.element.index, k)
                    map[k] = v
                }
                is TrackedList.Remove -> {
                    map.remove(keys.removeAt(change.element.index))
                }
                is TrackedList.Clear -> {
                    map.clear()
                    keys.clear()
                }
            }
        }
    }
    return map
}
