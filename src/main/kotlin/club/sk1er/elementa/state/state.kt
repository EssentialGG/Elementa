package club.sk1er.elementa.state

import java.lang.invoke.MethodHandles
import java.util.function.Consumer
import java.util.function.Function
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.javaField

abstract class State<T> {
    protected val listeners = mutableListOf<(T) -> Unit>()

    abstract fun getValue(): T

    open fun setValue(value: T) {
        listeners.forEach { it(value) }
    }

    fun setValue(mapper: (T) -> T) = setValue(mapper(getValue()))

    fun onSetValue(listener: (T) -> Unit): () -> Unit {
        listeners.add(listener)
        return { listeners.remove(listener) }
    }

    fun onSetValue(listener: Consumer<T>) = onSetValue { listener.accept(it) }

    fun getOrDefault(defaultValue: T) = getValue() ?: defaultValue

    fun getOrElse(defaultProvider: () -> T) = getValue() ?: defaultProvider()

    fun <U> map(mapper: (T) -> U) = MappedState(this, mapper)

    fun <U> map(mapper: Function<T, U>): State<U> = map { mapper.apply(it) }

    fun <U> zip(otherState: State<U>): State<Pair<T, U>> = ZippedState(this, otherState)

    companion object {
        fun <U> setDelegate(property: KProperty0<*>, newState: State<U>) {
            val delegate = MethodHandles.lookup().unreflectSetter(property.javaField.also {
                it?.isAccessible = true
            })
            delegate(newState)
        }
    }
}

open class BasicState<T>(protected var valueBacker: T) : State<T>() {
    override fun getValue() = valueBacker

    override fun setValue(value: T) {
        valueBacker = value
        super.setValue(value)
    }
}

open class MappedState<T, U>(initialState: State<T>, private val mapper: (T) -> U) : BasicState<U>(mapper(initialState.getValue())) {
    private var removeListener = initialState.onSetValue {
        valueBacker = mapper(it)
    }

    fun rebind(newState: State<T>) {
        removeListener()
        removeListener = newState.onSetValue {
            valueBacker = mapper(it)
        }
        setValue(mapper(newState.getValue()))
    }
}

class ZippedState<T, U>(firstState: State<T>, secondState: State<U>) : State<Pair<T, U>>() {
    private var firstCachedValue = firstState.getValue()
    private var secondCachedValue = secondState.getValue()

    init {
        firstState.onSetValue {
            firstCachedValue = it
        }
        secondState.onSetValue {
            secondCachedValue = it
        }
    }

    override fun getValue(): Pair<T, U> = firstCachedValue to secondCachedValue
}

open class ListState<T>(private var list: List<T>) : State<List<T>>() {
    override fun getValue() = list

    override fun setValue(value: List<T>) {
        this.list = value
        super.setValue(value)
    }

    operator fun get(index: Int) = list[index]
}

class MutableListState<T>(private var list: MutableList<T>) : State<MutableList<T>>() {
    override fun getValue() = list

    override fun setValue(value: MutableList<T>) {
        this.list = value
        super.setValue(value)
    }

    operator fun get(index: Int) = list[index]

    operator fun set(index: Int, value: T) {
        list[index] = value
        listeners.forEach { it(list) }
    }
}
