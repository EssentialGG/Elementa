package club.sk1er.elementa.state

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible
import kotlin.system.measureTimeMillis

class StateDelegator<T>(val state: State<T>) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = state.getValue()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        state.setValue(value)
    }
}

class MappedStateDelegator<T, U>(val state: State<T>, val mapper: (T) -> U) : ReadWriteProperty<Any?, U> {
    private var cachedValue = mapper(state.getValue())

    init {
        state.onSetValue {
            cachedValue = mapper(it)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = cachedValue

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: U) { }
}

fun <T> state(target: T) = StateDelegator(BasicState(target))

@Suppress("UNCHECKED_CAST")
fun <T, U> map(property: KProperty0<T>, mapper: (T) -> U): MappedStateDelegator<T, U> {
    property.isAccessible = true
    return MappedStateDelegator((property.getDelegate() as StateDelegator<T>).state, mapper)
}
