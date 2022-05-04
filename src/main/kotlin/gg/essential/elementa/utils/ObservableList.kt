package gg.essential.elementa.utils

import java.util.*

class ObservableList<T>(private val wrapped: MutableList<T>) : MutableList<T> by wrapped, Observable() {
    private fun update(event: ObservableListEvent<T>) {
        setChanged()
        notifyObservers(event)
    }

    override fun add(element: T): Boolean {
        if (wrapped.add(element)) {
            update(ObservableAddEvent(element withIndex wrapped.lastIndex))
            return true
        }
        return false
    }

    override fun add(index: Int, element: T) {
        wrapped.add(index, element)
        update(ObservableAddEvent(element withIndex index))
    }

    override fun remove(element: T): Boolean {
        val index = wrapped.indexOf(element)
        if (index != -1) {
            wrapped.removeAt(index)
            update(ObservableRemoveEvent(element withIndex index))
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val size = wrapped.size
        if (wrapped.addAll(elements)) {
            elements.forEachIndexed { i, element ->
                update(ObservableAddEvent(element withIndex i + size))
            }
            return true
        }
        return false
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (wrapped.addAll(index, elements)) {
            elements.forEachIndexed { i, element ->
                update(ObservableAddEvent(element withIndex i + index))
            }
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return elements.fold(false) { acc, element ->
            val index = wrapped.indexOf(element)
            if (index != -1) {
                wrapped.removeAt(index)
                update(ObservableRemoveEvent(element withIndex index))
            }

            acc || index != -1
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return removeAll(wrapped.filter { it !in elements })
    }

    override fun sort(c: Comparator<in T>?) {
        Collections.sort(wrapped, c)
    }

    override fun clear() {
        val oldChildren = wrapped.toList()
        wrapped.clear()
        update(ObservableClearEvent(oldChildren))
    }

    override operator fun set(index: Int, element: T): T {
        return wrapped.set(index, element).also {
            update(ObservableRemoveEvent(it withIndex index))
            update(ObservableAddEvent(element withIndex index))
        }
    }

    override fun removeAt(index: Int): T {
        return wrapped.removeAt(index).also {
            update(ObservableRemoveEvent(it withIndex index))
        }
    }
}

sealed class ObservableListEvent<T>

class ObservableAddEvent<T>(val element: IndexedValue<T>) : ObservableListEvent<T>()

class ObservableRemoveEvent<T>(val element: IndexedValue<T>) : ObservableListEvent<T>()

class ObservableClearEvent<T>(val oldChildren: List<T>) : ObservableListEvent<T>() {
    constructor() : this(listOf())
}

infix fun <T> T.withIndex(index: Int) = IndexedValue(index, this)

fun <T> MutableList<T>.observable() = ObservableList(this)