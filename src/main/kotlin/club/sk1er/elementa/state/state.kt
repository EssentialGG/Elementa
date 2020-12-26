package club.sk1er.elementa.state

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ColorConstraint
import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.MasterConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import java.awt.Color
import java.util.function.Consumer
import java.util.function.Function
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

class StateDelegateProvider<T>(var state: State<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = state.getValue()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = state.setValue(value)

    fun rebind(newState: State<T>) {
        state = newState
    }
}

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

    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>) = StateDelegateProvider(this)

    fun getOrDefault(defaultValue: T) = getValue() ?: defaultValue

    fun getOrElse(defaultProvider: () -> T) = getValue() ?: defaultProvider()

    fun <U> map(mapper: (T) -> U) = MappedState(this, mapper)

    fun <U> map(mapper: Function<T, U>): State<U> = map { mapper.apply(it) }

    fun <U> zip(otherState: State<U>): State<Pair<T, U>> = ZippedState(this, otherState)

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <U> setDelegate(property: KProperty0<*>, newState: State<U>) {
            property.isAccessible = true
            val delegateProvider = (property.getDelegate() as? StateDelegateProvider<U>) ?:
                throw IllegalArgumentException("Cannot redelegate a property which is not delegated")
            delegateProvider.rebind(newState)
        }
    }
}

open class BasicState<T>(private var value: T) : State<T>() {
    override fun getValue() = value

    override fun setValue(value: T) {
        this.value = value
        super.setValue(value)
    }
}

open class MappedState<T, U>(initialState: State<T>, private val mapper: (T) -> U) : BasicState<U>(mapper(initialState.getValue())) {
    private var cachedValue = mapper(initialState.getValue())
    private var removeListener = initialState.onSetValue {
        cachedValue = mapper(it)
    }

    fun rebind(newState: State<T>) {
        removeListener()
        removeListener = newState.onSetValue {
            cachedValue = mapper(it)
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
