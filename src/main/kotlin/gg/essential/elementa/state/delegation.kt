package gg.essential.elementa.state

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * Allows Kotlin property delegation to and from a State.
 *
 * The entry point for this class is generally the top-level
 * utility methods provided in this file. See those functions
 * for example usage.
 *
 * @see gg.essential.elementa.state.state
 * @see gg.essential.elementa.state.map
 * @see gg.essential.elementa.state.zip
 */
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

/**
 * A top-level utility function allowing simple state delegation.
 *
 * Example usage:
 *
 *     object Test {
 *         private var foo by state(1)
 *
 *         fun main() {
 *             println(foo) // prints 1
 *             foo = 10
 *             println(foo) // prints 10
 *         }
 *     }
 */
fun <T> state(target: T) = StateDelegator(BasicState(target))

/**
 * A top-level utility function allowing simple mapped state delegation.
 *
 * Example usage:
 *
 *     object Test {
 *         private var foo by state(1)
 *         private val bar by map(::foo) { it * 2 }
 *
 *         fun main() {
 *             println(bar) // prints 2
 *             foo = 10
 *             println(bar) // prints 20
 *         }
 *     }
 */
fun <T, U> map(property: KProperty0<T>, mapper: (T) -> U): MappedStateDelegator<T, U> {
    return MappedStateDelegator(getDelegate(property).state, mapper)
}

/**
 * A top-level utility function allowing simple zipped state delegation.
 *
 * This function accepts two Kotlin properties and returns a delegator which
 * zips their values together into a [Pair].
 *
 * Example usage:
 *
 *     object Test {
 *         private var foo by state(1)
 *         private var bar by state(2)
 *         private val baz by zip(::foo, ::bar)
 *
 *         fun main() {
 *             println(baz) // prints (1, 2)
 *             foo = 10
 *             println(baz) // prints (10, 2)
 *         }
 *     }
 */
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
