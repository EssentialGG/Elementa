package club.sk1er.elementa.state

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

open class StateDelegator<T>(val state: State<T>) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = state.get()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        state.set(value)
    }
}

class MappedStateDelegator<T, U>(state: State<T>, mapper: (T) -> U) :
    StateDelegator<U>(MappedState(state, mapper))

class ZippedStateDelegator<T, U>(firstState: State<T>, secondState: State<U>) :
    StateDelegator<Pair<T, U>>(ZippedState(firstState, secondState))

fun <T> state(target: T) = StateDelegator(BasicState(target))

fun <T, U> map(property: KProperty0<T>, mapper: (T) -> U): MappedStateDelegator<T, U> {
    return MappedStateDelegator(getDelegate(property).state, mapper)
}

fun <T, U> zip(property1: KProperty0<T>, property2: KProperty0<U>): ZippedStateDelegator<T, U> {
    return ZippedStateDelegator(getDelegate(property1).state, getDelegate(property2).state)
}

private fun <T> getDelegate(property: KProperty0<T>): StateDelegator<T> {
    property.isAccessible = true
    val delegate = property.getDelegate() ?:
        throw IllegalArgumentException("map cannot be used on a non-delegated property")
    @Suppress("UNCHECKED_CAST")
    return (delegate as? StateDelegator<T>) ?:
        throw IllegalArgumentException("map can only be used on StateDelegator<T> properties")
}
