package club.sk1er.elementa.utils

import java.util.*

class ObservableList<T>(private val wrapped: MutableList<T>) : MutableList<T> by wrapped, Observable() {
    private fun update() {
        setChanged()
        notifyObservers()
    }

    override fun add(element: T): Boolean {
        if (wrapped.add(element)) {
            update()
            return true
        }
        return false
    }

    override fun remove(element: T): Boolean {
        if (wrapped.remove(element)) {
            update()
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if (wrapped.addAll(elements)) {
            update()
            return true
        }
        return false
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (wrapped.addAll(index, elements)) {
            update()
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        if (wrapped.removeAll(elements)) {
            update()
            return true
        }
        return false
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        if (wrapped.retainAll(elements)) {
            update()
            return true
        }
        return false
    }

    override fun clear() {
        wrapped.clear()
        update()
    }

    override operator fun set(index: Int, element: T): T {
        return wrapped.set(index, element).also { update() }
    }

    override fun add(index: Int, element: T) {
        wrapped.add(index, element)
        update()
    }

    override fun removeAt(index: Int): T {
        return wrapped.removeAt(index).also { update() }
    }
}

fun <T> MutableList<T>.observable() = ObservableList(this)