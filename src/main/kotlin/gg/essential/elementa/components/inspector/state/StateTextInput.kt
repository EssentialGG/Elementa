package gg.essential.elementa.components.inspector.state

import gg.essential.elementa.components.input.v2.UITextInput
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.onLeftClick
import org.jetbrains.annotations.ApiStatus
import java.awt.Color

/**
 * Simple text input that maps to [state] using [map] on enter and focus lost.
 * If input is not valid, an error animation is shown.
 */
@ApiStatus.Internal
class StateTextInput<T>(
    private val state: State<T>,
    mutable: Boolean,
    private val map: (String) -> T,
) : UITextInput() {

    init {
        if (mutable) {
            onLeftClick {
                grabWindowFocus()
            }
        }
        constrain {
            width = basicWidthConstraint {
                getText().width() + 2
            }
            color = Color(0xAAAAAA).toConstraint()
        }

        onFocusLost {
            if (!updateState()) {
                cloneStateToInput()
            }
        }
        cloneStateToInput()
    }

    /**
     * Sets the value of the input to the current value of1 the state
     */
    private fun cloneStateToInput() {
        when (val current = state.get()) {
            is Float, Double -> {
                setText("%.2f".format(current))
            }
            is Color -> {
                setText(Integer.toHexString(current.rgb and 0xFFFFFF))
            }
            else -> {
                setText(current.toString())
            }
        }
    }

    override fun onEnterPressed() {
        if (updateState()) {
            releaseWindowFocus()
            cloneStateToInput()
        }
    }

    /**
     * Tries to update the state based on the current value of the text input.
     * Returns true if the state was updated.
     */
    private fun updateState(): Boolean {
        return try {
            state.set(map(getText()))
            true
        } catch (e: ParseException) {
            grabWindowFocus()
            animateError()
            false
        }
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
        val color = getColor()
        val oldX = constraints.x
        selectionForegroundColor = Color.RED
        inactiveSelectionForegroundColor = Color.RED
        setColor(Color.RED)
        delay(250) {
            selectionForegroundColor = oldSelectionForegroundColor
            inactiveSelectionForegroundColor = oldInactiveSelectionForegroundColor
        }
        animate {
            setXAnimation(Animations.IN_BOUNCE, .25f, oldX + 3.pixels)
            onComplete {
                setX(oldX)
                setColor(color)
            }
        }
    }

    /**
     * Thrown to show that the current value of the text input is not valid.
     */
    @ApiStatus.Internal
    class ParseException : Exception()
}

