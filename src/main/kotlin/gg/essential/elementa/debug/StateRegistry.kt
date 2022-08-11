package gg.essential.elementa.debug

import gg.essential.elementa.state.State
import java.awt.Color

/**
 * Components and constraints can implement this interface and provide managed states through
 * [getManagedStates]. Any managed state will have its value display in the [gg.essential.elementa.components.inspector.Inspector]
 * and have its value configurable if it is mutable.
 */
internal interface StateRegistry {

    /**
     * Returns a list of managed states that govern some aspect of this component or constraint.
     */
    fun getManagedStates(): List<ManagedState>
}

/**
 * Managed states are used to display and configure the value of a state inside the [gg.essential.elementa.components.inspector.Inspector].
 */
sealed class ManagedState(
    val name: String,
    val mutable: Boolean,
) {

    class ManagedStringState(val state: State<String>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    class ManagedBooleanState(val state: State<Boolean>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    class ManagedColorState(val state: State<Color>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    class ManagedColorStateNullable(val state: State<Color?>, name: String, mutable: Boolean) :
        ManagedState(name, mutable)

    class ManagedIntState(val state: State<Int>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    class ManagedFloatState(val state: State<Float>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    class ManagedDoubleState(val state: State<Double>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    class ManagedEnumState<E : Enum<E>>(val state: State<E>, name: String, mutable: Boolean) :
        ManagedState(name, mutable)

    class ManagedObjectState<T : InspectorDisplay>(
        val state: State<T>,
        val allValues: List<T>,
        name: String,
        mutable: Boolean,
    ) : ManagedState(name, mutable)

}

/**
 * Implemented by an object that is used as a valid entry for [ManagedState.ManagedObjectState].
 */
interface InspectorDisplay {

    /**
     * The display name in the [gg.essential.elementa.components.inspector.Inspector] for this value
     */
    fun inspectorDisplayName(): String = toString()

}

