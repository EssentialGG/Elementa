package gg.essential.elementa.manager

import gg.essential.universal.UKeyboard

/**
 * A keyboard manager that provides its values from [UKeyboard]
 */
internal object DefaultKeyboardManager : KeyboardManager {

    override fun isKeyDown(key: Int): Boolean = UKeyboard.isKeyDown(key)

    override fun allowRepeatEvents(enabled: Boolean) = UKeyboard.allowRepeatEvents(enabled)

    override fun getModifiers(): UKeyboard.Modifiers = UKeyboard.getModifiers()
}