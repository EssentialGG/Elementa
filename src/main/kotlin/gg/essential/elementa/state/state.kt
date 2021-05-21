package gg.essential.elementa.state

import java.util.function.Consumer
import java.util.function.Function

/**
 * The base for all Elementa State objects.
 *
 * State objects are essentially just a wrapper around a value.
 * However, the ability to be deeply integrated into an
 * Elementa component allows some nice functionality.
 *
 * The primary advantage of using state is that a single state
 * object can be shared between multiple components or
 * constraints. This allows one value update to be seen
 * by multiple components or constraints. For example, if a
 * component has many text children, and they all share the
 * same color state variable, then whenever the value of the
 * state object is updated, all of the text components will
 * instantly change color.
 *
 * Another advantage arises when using Kotlin, as States
 * can be delegated to. For more information, see
 * delegation.kt.
 *
 * @see StateDelegator
 */
abstract class State<T> {
    protected val listeners = mutableListOf<(T) -> Unit>()

    /**
     * Get the value of this State object
     */
    abstract fun get(): T

    /**
     * Set the value of this State object
     *
     * This method also notifies all of the listeners of this
     * State object.
     */
    open fun set(value: T) {
        listeners.forEach { it(value) }
    }

    /**
     * Like [set], but accepts a lambda which takes the
     * current value of this State object
     */
    fun set(mapper: (T) -> T) = set(mapper(get()))

    /**
     * Register a listener which will be called whenever the
     * value of this State object changes
     *
     * @return A callback which, when invoked, removes this listener
     */
    fun onSetValue(listener: (T) -> Unit): () -> Unit {
        listeners.add(listener)
        return { listeners.remove(listener) }
    }

    /**
     * Register a listener which will be called whenever the
     * value of this State object changes
     *
     * @return A callback which, when invoked, removes this listener
     */
    fun onSetValue(listener: Consumer<T>) = onSetValue { listener.accept(it) }

    fun getOrDefault(defaultValue: T) = get() ?: defaultValue

    fun getOrElse(defaultProvider: () -> T) = get() ?: defaultProvider()

    /**
     * Maps this state into a new state
     *
     * @see MappedState
     */
    fun <U> map(mapper: (T) -> U) = MappedState(this, mapper)

    /**
     * Maps this state into a new state
     *
     * @see MappedState
     */
    fun <U> map(mapper: Function<T, U>): State<U> = map { mapper.apply(it) }

    /**
     * Zips this state with another state
     *
     * @see ZippedState
     */
    fun <U> zip(otherState: State<U>): State<Pair<T, U>> = ZippedState(this, otherState)
}

/**
 * A simple implementation of [State], containing only a
 * backing field
 */
open class BasicState<T>(protected var valueBacker: T) : State<T>() {
    override fun get() = valueBacker

    override fun set(value: T) {
        if (value == valueBacker)
            return

        valueBacker = value
        super.set(value)
    }
}

/**
 * A state which maps another state using the provided
 * mapping function.
 *
 * This should primarily be used via the [State.map] or
 * [gg.essential.elementa.state.map] methods.
 */
open class MappedState<T, U>(initialState: State<T>, private val mapper: (T) -> U) : BasicState<U>(mapper(initialState.get())) {
    private var removeListener = initialState.onSetValue {
        set(mapper(it))
    }

    /**
     * Changes the state that this state maps from.
     *
     * This method calls [State.set], and will trigger
     * all of its listeners.
     */
    fun rebind(newState: State<T>) {
        removeListener()
        removeListener = newState.onSetValue {
            set(mapper(it))
        }
        set(mapper(newState.get()))
    }
}

/**
 * A state which combines two other states into a [Pair].
 *
 * This should primarily be used via the [State.zip] or
 * [gg.essential.elementa.state.zip] methods.
 */
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

    /**
     * Changes the first state that this state uses.
     *
     * This methods calls [State.set], and will trigger
     * all of its listeners.
     */
    fun rebindFirst(newState: State<T>) {
        removeFirstListener()
        removeFirstListener = newState.onSetValue {
            set(it to get().second)
        }
        set(newState.get() to get().second)
    }

    /**
     * Changes the second state that this state uses.
     *
     * This methods calls [State.set], and will trigger
     * all of its listeners.
     */
    fun rebindSecond(newState: State<U>) {
        removeSecondListener()
        removeSecondListener = newState.onSetValue {
            set(get().first to it)
        }
        set(get().first to newState.get())
    }
}
