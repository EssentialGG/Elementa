package gg.essential.elementa.debug

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.inspector.CompactToggle
import gg.essential.elementa.components.inspector.state.CompactSelector
import gg.essential.elementa.components.inspector.state.StateTextInput
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
    class OfString(val state: State<String>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class OfBoolean(val state: State<Boolean>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class OfColor(val state: State<Color>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class OfColorOrNull(val state: State<Color?>, name: String, mutable: Boolean) :
        ManagedState(name, mutable)

    @ApiStatus.Internal
    class OfInt(val state: State<Int>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class OfFloat(val state: State<Float>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class OfDouble(val state: State<Double>, name: String, mutable: Boolean) : ManagedState(name, mutable)

    @ApiStatus.Internal
    class OfEnum<E : Enum<E>>(val state: State<E>, name: String, mutable: Boolean) :
        ManagedState(name, mutable) {

            fun createSelector(): UIComponent {
                val values = state.get().javaClass.enumConstants.toList()
                return CompactSelector(values, state) {
                    it.name
                }
            }
        }

    @ApiStatus.Internal
    class OfEnumerable<T>(
        val state: State<T>,
        val allValues: List<T>,
        val displayName: (T) -> String,
        name: String,
        mutable: Boolean,
    ) : ManagedState(name, mutable) {

        fun createSelector(): UIComponent {
            return CompactSelector(allValues, state) {
                displayName(it)
            }
        }
    }

}

@ApiStatus.Internal
object StateRegistryComponentFactory {

    fun createInspectorComponent(managedState: ManagedState): UIComponent {
        return when (managedState) {
            is ManagedState.OfFloat -> {
                createInputComponent(managedState.state, managedState.mutable, { "%.2f".format(it) }) {
                    try {
                        it.toFloat()
                    } catch (e: NumberFormatException) {
                        throw StateTextInput.ParseException()
                    }
                }
            }
            is ManagedState.OfDouble -> {
                createInputComponent(managedState.state, managedState.mutable, { "%.2f".format(it) }) {
                    try {
                        it.toDouble()
                    } catch (e: NumberFormatException) {
                        throw StateTextInput.ParseException()
                    }
                }
            }
            is ManagedState.OfBoolean -> {
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
            is ManagedState.OfInt -> {
                createInputComponent(managedState.state, managedState.mutable, { it.toString() }) {
                    try {
                        it.toInt()
                    } catch (e: NumberFormatException) {
                        throw StateTextInput.ParseException()
                    }
                }
            }
            is ManagedState.OfString -> {
                createInputComponent(managedState.state, managedState.mutable, { it }) {
                    it
                }
            }
            is ManagedState.OfColorOrNull -> {
                createInputComponent(
                    managedState.state,
                    managedState.mutable,
                    { if (it == null) "null" else Integer.toHexString(it.rgb) }) {
                    if (it == "null" || it.isEmpty()) {
                        return@createInputComponent null
                    }
                    parseStringToColor(it)
                }
            }
            is ManagedState.OfColor -> {
                createInputComponent(
                    managedState.state,
                    managedState.mutable,
                    { Integer.toHexString(it.rgb) }) {
                    parseStringToColor(it)
                }
            }
            is ManagedState.OfEnum<*> -> {
                managedState.createSelector()
            }
            is ManagedState.OfEnumerable<*> -> {
                managedState.createSelector()
            }
        }
    }

    private fun parseStringToColor(string: String): Color {
        return try {
            Color(string.lowercase().toInt(16))
        } catch (e: NumberFormatException) {
            throw StateTextInput.ParseException()
        }
    }

    private fun <T> createInputComponent(
        state: State<T>,
        mutable: Boolean,
        formatToText: (T) -> String,
        parse: (String) -> T,
    ): UIComponent {
        return StateTextInput(state, mutable, formatToText = formatToText, parse = parse)
    }
}
