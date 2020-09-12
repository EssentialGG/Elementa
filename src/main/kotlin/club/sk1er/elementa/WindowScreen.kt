package club.sk1er.elementa

import club.sk1er.elementa.components.Window
import club.sk1er.mods.core.universal.UniversalKeyboard
import club.sk1er.mods.core.universal.UniversalMinecraft
import club.sk1er.mods.core.universal.UniversalScreen

abstract class WindowScreen(
    private val enableRepeatKeys: Boolean = true,
    private val drawDefaultBackground: Boolean = true
) : UniversalScreen() {
    protected val window = Window()

    init {
        window.onKeyType { typedChar, keyCode ->
            defaultKeyBehavior(typedChar, keyCode)
        }
    }

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(mouseX, mouseY, partialTicks)

        if (drawDefaultBackground)
            super.onDrawBackground(0)

        // Now, we need to hook up Elementa to this GuiScreen. In practice, Elementa
        // is not constrained to being used solely inside of a GuiScreen, all the programmer
        // needs to do is call the [Window] events when appropriate, whenever that may be.
        // In our example, it is in the overridden [GuiScreen#drawScreen] method.
        window.draw()
    }

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        super.onMouseClicked(mouseX, mouseY, mouseButton)

        // We also need to pass along clicks
        window.mouseClick(mouseX, mouseY, mouseButton)
    }

    override fun onMouseReleased(mouseX: Double, mouseY: Double, state: Int) {
        super.onMouseReleased(mouseX, mouseY, state)

        // We also need to pass along mouse releases
        window.mouseRelease()
    }

    override fun onMouseScrolled(delta: Double) {
        super.onMouseScrolled(delta)

        // We also need to pass along scrolling
        window.mouseScroll(delta.coerceIn(-1.0, 1.0))
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UniversalKeyboard.Modifier?) {
        // We also need to pass along typed keys
        window.keyType(typedChar, keyCode)
    }

    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)

        // Since we want our users to be able to hold a key
        // to type. This is a wrapper around a base LWJGL function.
        // - Keyboard.enableRepeatEvents in <= 1.12.2
        if (enableRepeatKeys)
            UniversalKeyboard.enableRepeatEvents(true)
    }

    override fun onScreenClose() {
        super.onScreenClose()

        // We need to disable repeat events when leaving the gui.
        if (enableRepeatKeys)
            UniversalKeyboard.enableRepeatEvents(false)
    }

    fun defaultKeyBehavior(typedChar: Char, keyCode: Int) {
        super.onKeyPressed(keyCode, typedChar, UniversalKeyboard.getModifiers())
    }
}