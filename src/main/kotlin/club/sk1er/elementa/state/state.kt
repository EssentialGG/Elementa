package club.sk1er.elementa.state

import java.util.function.Consumer
import java.util.function.Function

abstract class State<T> {
    protected val listeners = mutableListOf<(T) -> Unit>()

    abstract fun get(): T

    open fun set(value: T) {
        listeners.forEach { it(value) }
    }

    fun set(mapper: (T) -> T) = set(mapper(get()))

    fun onSetValue(listener: (T) -> Unit): () -> Unit {
        listeners.add(listener)
        return { listeners.remove(listener) }
    }

    fun onSetValue(listener: Consumer<T>) = onSetValue { listener.accept(it) }

    fun getOrDefault(defaultValue: T) = get() ?: defaultValue

    fun getOrElse(defaultProvider: () -> T) = get() ?: defaultProvider()

    fun <U> map(mapper: (T) -> U) = MappedState(this, mapper)

    fun <U> map(mapper: Function<T, U>): State<U> = map { mapper.apply(it) }

    fun <U> zip(otherState: State<U>): State<Pair<T, U>> = ZippedState(this, otherState)
}

open class BasicState<T>(protected var valueBacker: T) : State<T>() {
    override fun get() = valueBacker

    override fun set(value: T) {
        valueBacker = value
        super.set(value)
    }
}

open class MappedState<T, U>(initialState: State<T>, private val mapper: (T) -> U) : BasicState<U>(mapper(initialState.get())) {
    private var removeListener = initialState.onSetValue {
        set(mapper(it))
    }

    fun rebind(newState: State<T>) {
        removeListener()
        removeListener = newState.onSetValue {
            set(mapper(it))
        }
        set(mapper(newState.get()))
    }
}

class ZippedState<T, U>(
    firstState: State<T>,
    secondState: State<U>
) : BasicState<Pair<T, U>>(firstState.get() to secondState.get()) {
    private var removeFirstListener = firstState.onSetValue {
        set(it to get().second)
    }
    private var removeSecondListener = secondState.onSetValue {
        set(get().first to it)
    }

    fun rebindFirst(newState: State<T>) {
        removeFirstListener()
        removeFirstListener = newState.onSetValue {
            set(it to get().second)
        }
        set(newState.get() to get().second)
    }

    fun rebindSecond(newState: State<U>) {
        removeSecondListener()
        removeSecondListener = newState.onSetValue {
            set(get().first to it)
        }
        set(get().first to newState.get())
    }
}
