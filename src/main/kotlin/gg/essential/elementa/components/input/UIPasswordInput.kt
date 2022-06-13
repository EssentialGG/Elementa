package gg.essential.elementa.components.input

import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.width
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.MappedState
import gg.essential.elementa.state.State
import java.awt.Color

open class UIPasswordInput @JvmOverloads constructor(
    passwordChar: Char = '*',
    placeholder: String = "",
    shadow: Boolean = true,
    selectionBackgroundColor: Color = Color.WHITE,
    selectionForegroundColor: Color = Color(64, 139, 229),
    allowInactiveSelection: Boolean = false,
    inactiveSelectionBackgroundColor: Color = Color(176, 176, 176),
    inactiveSelectionForegroundColor: Color = Color.WHITE,
    cursorColor: Color = Color.WHITE
) : UITextInput(
    placeholder,
    shadow,
    selectionBackgroundColor,
    selectionForegroundColor,
    allowInactiveSelection,
    inactiveSelectionBackgroundColor,
    inactiveSelectionForegroundColor,
    cursorColor
) {
    private val protected: MappedState<Boolean, Boolean> = BasicState(true).map { it }
    private val passwordCharAsString: String = passwordChar.toString()

    override fun getTextForRender(): String = if (protected.get()) getProtectedString(getText()) else getText()

    override fun setCursorPos() {
        if (protected.get()) {
            cursorComponent.unhide()
            val x = passwordCharAsString.repeat(cursor.toVisualPos().column)
                .width(getTextScale()) - horizontalScrollingOffset
            cursorComponent.setX(x.pixels())
        } else {
            super.setCursorPos()
        }
    }

    fun bindProtection(protectedState: BasicState<Boolean>) = apply {
        protected.rebind(protectedState)
    }

    fun isProtected(): Boolean = protected.get()
    fun setProtection(protected: Boolean): Unit = this.protected.set(protected)

    fun getProtectedString(text: String): String = passwordCharAsString.repeat(text.length)

    companion object {
        @JvmStatic
        fun getProtectedString(text: String, char: Char): String = char.toString().repeat(text.length)
    }
}