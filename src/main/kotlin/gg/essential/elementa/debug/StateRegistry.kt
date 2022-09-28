package gg.essential.elementa.debug

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.inspector.CompactToggle
import gg.essential.elementa.components.inspector.state.CompactSelector
import gg.essential.elementa.components.inspector.state.MappedTextInput
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.onLeftClick
import org.jetbrains.annotations.ApiStatus
import gg.essential.elementa.components.inspector.Inspector
import java.awt.Color

/**
 * Components and constraints can implement this interface and provide managed states through
 * [managedStates]. Any managed state will have its value display in the [Inspector]
 * and have its value configurable if it is mutable.
 */
internal interface StateRegistry {

    /**
     * Returns a list of managed states that govern some aspect of this component or constraint.
     */
    val managedStates: List<ManagedState>
}

/**
 * Managed states are used to display and configure the value of a state inside the [Inspector].
 */
@ApiStatus.Internal
sealed class ManagedState(
    val name: String,
    val mutable: Boolean,
) {

    @ApiStatus.Internal
    class ManagedStringState(val state: State<String>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class ManagedBooleanState(val state: State<Boolean>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class ManagedColorState(val state: State<Color>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class ManagedColorStateNullable(val state: State<Color?>, name: String, mutable: Boolean) :
        ManagedState(name, mutable)

    @ApiStatus.Internal
    class ManagedIntState(val state: State<Int>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class ManagedFloatState(val state: State<Float>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class ManagedDoubleState(val state: State<Double>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class ManagedEnumState<E : Enum<E>>(val state: State<E>, name: String, mutable: Boolean) :
        ManagedState(name, mutable) {

            fun createSelector(): UIComponent {
                val values = state.get().javaClass.enumConstants.toList()
                return CompactSelector(values, state) {
                    it.name
                }
            }
        }

    @ApiStatus.Internal
    class ManagedObjectState<T : InspectorDisplay>(
        val state: State<T>,
        val allValues: List<T>,
        name: String,
        mutable: Boolean,
    ) : ManagedState(name, mutable) {

        fun createSelector(): UIComponent {
            return CompactSelector(allValues, state) {
                it.inspectorDisplayName()
            }
        }
    }

}

/**
 * Implemented by an object that is used as a valid entry for [ManagedState.ManagedObjectState].
 */
@ApiStatus.Internal
interface InspectorDisplay {

    /**
     * The display name in the [Inspector] for this value
     */
    fun inspectorDisplayName(): String = toString()

}

@ApiStatus.Internal
object StateRegistryComponentFactory {

    fun createInspectorComponent(managedState: ManagedState): UIComponent {
        return when (managedState) {
            is ManagedState.ManagedFloatState -> {
                createInputComponent(managedState.state, managedState.mutable) {
                    try {
                        it.toFloat()
                    } catch (e: NumberFormatException) {
                        throw MappedTextInput.ParseException()
                    }
                }
            }
            is ManagedState.ManagedDoubleState -> {
                createInputComponent(managedState.state, managedState.mutable) {
                    try {
                        it.toDouble()
                    } catch (e: NumberFormatException) {
                        throw MappedTextInput.ParseException()
                    }
                }
            }
            is ManagedState.ManagedBooleanState -> {
                CompactToggle(managedState.state).apply {
                    if (!managedState.mutable) {
                        mouseClickListeners.clear() // Disables the toggle
                        onLeftClick {
                            this effect OutlineEffect(Color.RED, 2f)
                            delay(250) {
                                this.effects.clear()
                            }
                        }
                    }
                }
            }
            is ManagedState.ManagedIntState -> {
                createInputComponent(managedState.state, managedState.mutable) {
                    try {
                        it.toInt()
                    } catch (e: NumberFormatException) {
                        throw MappedTextInput.ParseException()
                    }
                }
            }
            is ManagedState.ManagedStringState -> {
                createInputComponent(managedState.state, managedState.mutable) {
                    it
                }
            }
            is ManagedState.ManagedColorStateNullable -> {
                createInputComponent(managedState.state, managedState.mutable) {
                    if (it.isEmpty()) {
                        return@createInputComponent null
                    }
                    try {
                        Color(it.lowercase().toInt(16) or 0xFF000000.toInt())
                    } catch (e: NumberFormatException) {
                        throw MappedTextInput.ParseException()
                    }
                }
            }
            is ManagedState.ManagedColorState -> {
                createInputComponent(managedState.state, managedState.mutable) {
                    try {
                        Color(it.lowercase().toInt(16) or 0xFF000000.toInt())
                    } catch (e: NumberFormatException) {
                        throw MappedTextInput.ParseException()
                    }
                }
            }
            is ManagedState.ManagedEnumState<*> -> {
               managedState.createSelector()
            }
            is ManagedState.ManagedObjectState<*> -> {
                managedState.createSelector()
            }
        }
    }

    private fun <T> createInputComponent(state: State<T>, mutable: Boolean, mapper: (String) -> T): UIComponent {
        return MappedTextInput(state, mutable, mapper)
    }
}
