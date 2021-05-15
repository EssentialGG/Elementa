package club.sk1er.elementa.components.input

import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.state.BasicState
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
    private var protected: BasicState<Boolean> = BasicState(true)
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
        protected = protectedState
    }

    fun isProtected(): Boolean = protected.get()
    fun setProtection(protected: Boolean): Unit = this.protected.set(protected)

    fun getProtectedString(text: String): String = passwordCharAsString.repeat(text.length)

    companion object {
        @JvmStatic
        fun getProtectedString(text: String, char: Char): String = char.toString().repeat(text.length)
    }
}