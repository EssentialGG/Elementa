package gg.essential.elementa.manager

import gg.essential.universal.UKeyboard
import gg.essential.universal.UMinecraft
import org.jetbrains.annotations.ApiStatus

/**
 * Provides a non-global way to access keyboard states.
 */
@ApiStatus.Internal
interface KeyboardManager {

    /**
     * returns true if the supplied key with the supplied keycode is currently pressed.
     */
    fun isKeyDown(key: Int): Boolean

    /**
     * Sets whether repeat key events are enabled or not.
     */
    fun allowRepeatEvents(enabled: Boolean)

    /**
     * Gets the current state of current modifier keys
     */
    fun getModifiers(): UKeyboard.Modifiers

    /* Default utility functions that may be overridden */

    @ApiStatus.Internal
    fun isShiftKeyDown(): Boolean = isKeyDown(UKeyboard.KEY_LSHIFT) || isKeyDown(UKeyboard.KEY_RSHIFT)

    @ApiStatus.Internal
    fun isAltKeyDown(): Boolean = isKeyDown(UKeyboard.KEY_LMENU) || isKeyDown(UKeyboard.KEY_RMENU)

    @ApiStatus.Internal
    fun isCtrlKeyDown(): Boolean = if (UMinecraft.isRunningOnMac) {
        isKeyDown(UKeyboard.KEY_LMETA) || isKeyDown(UKeyboard.KEY_RMETA)
    } else isKeyDown(UKeyboard.KEY_LCONTROL) || isKeyDown(UKeyboard.KEY_RCONTROL)

    @ApiStatus.Internal
    fun isKeyComboCtrlA(key: Int): Boolean =
        key == UKeyboard.KEY_A && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

    @ApiStatus.Internal
    fun isKeyComboCtrlC(key: Int): Boolean =
        key == UKeyboard.KEY_C && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

    @ApiStatus.Internal
    fun isKeyComboCtrlV(key: Int): Boolean =
        key == UKeyboard.KEY_V && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

    @ApiStatus.Internal
    fun isKeyComboCtrlX(key: Int): Boolean =
        key == UKeyboard.KEY_X && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

    @ApiStatus.Internal
    fun isKeyComboCtrlY(key: Int): Boolean =
        key == UKeyboard.KEY_Y && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

    @ApiStatus.Internal
    fun isKeyComboCtrlZ(key: Int): Boolean =
        key == UKeyboard.KEY_Z && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

    @ApiStatus.Internal
    fun isKeyComboCtrlShiftZ(key: Int): Boolean =
        key == UKeyboard.KEY_Z && isCtrlKeyDown() && isShiftKeyDown() && !isAltKeyDown()
}