package gg.essential.elementa.components.inspector.state

import gg.essential.elementa.components.input.v2.UITextInput
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.onLeftClick
import gg.essential.elementa.utils.onSetValueAndNow
import org.jetbrains.annotations.ApiStatus
import java.awt.Color

/**
 * Simple text input that sets [state] on enter and focus lost. The text is converted to `T` using [parse].
 * If the input is not valid (that is, [parse] throws a [ParseException]), an error animation is shown.
 */
class StateTextInput<T>(
    private val state: State<T>,
    mutable: Boolean,
    textPadding: Float = 2f,
    private val formatToText: (T) -> String,
    private val parse: (String) -> T,
) : UITextInput() {

    init {
        if (mutable) {
            onLeftClick {
                grabWindowFocus()
            }
        }
        constrain {
            width = basicWidthConstraint {
                getText().width() + textPadding + if (active) 1f else 0f
            }
            color = Color(0xAAAAAA).toConstraint()
        }

        onFocusLost {
            if (!updateState()) {
                cloneStateToInput()
            }
        }
        state.onSetValueAndNow {
            cloneStateToInput()
        }
    }

    /**
     * Sets the value of the input to the current value of the state
     */
    private fun cloneStateToInput() {
        setText(formatToText(state.get()))
    }

    override fun onEnterPressed() {
        if (updateState()) {
            cloneStateToInput()
        }
    }

    /**
     * Tries to update the state based on the current value of the text input.
     * Returns true if the state was updated.
     */
    private fun updateState(): Boolean {
        val mappedValue = try {
            parse(getText())
        } catch (e: ParseException) {
            animateError()
            return false
        }
        state.set(mappedValue)
        return true
    }

    /**
     * Plays an animation indicating that the value is invalid.
     */
    private fun animateError() {
        // Already animating
        if (constraints is AnimatingConstraints) {
            return
        }
        val oldSelectionForegroundColor = selectionForegroundColor
        val oldInactiveSelectionForegroundColor = inactiveSelectionForegroundColor
        val oldColor = getColor()
        val oldX = constraints.x
        selectionForegroundColor = Color.RED
        inactiveSelectionForegroundColor = Color.RED
        setColor(Color.RED)
        animate {
            setXAnimation(Animations.IN_BOUNCE, .25f, oldX + 3.pixels)
            onComplete {
                setX(oldX)
                setColor(oldColor)
                selectionForegroundColor = oldSelectionForegroundColor
                inactiveSelectionForegroundColor = oldInactiveSelectionForegroundColor
            }
        }
    }

    /**
     * Thrown to show that the current value of the text input is not valid.
     */
    @ApiStatus.Internal
    class ParseException : Exception()
}
