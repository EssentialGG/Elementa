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

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        if (drawDefaultBackground) {
            drawDefaultBackground()
        }

        // Now, we need to hook up Elementa to this GuiScreen. In practice, Elementa
        // is not constrained to being used solely inside of a GuiScreen, all the programmer
        // needs to do is call the [Window] events when appropriate, whenever that may be.
        // In our example, it is in the overridden [GuiScreen#drawScreen] method.
        window.draw()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        // We also need to pass along clicks
        window.mouseClick(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)

        // We also need to pass along mouse releases
        window.mouseRelease()
    }

    override fun onMouseScroll(delta: Int) {
        super.onMouseScroll(delta)

        // We also need to pass along scrolling
        window.mouseScroll(delta.coerceIn(-1, 1))
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        // We also need to pass along typed keys
        window.keyType(typedChar, keyCode)
    }

    override fun initGui() {
        super.initGui()

        // Since we want our users to be able to hold a key
        // to type. This is a wrapper around a base LWJGL function.
        // - Keyboard.enableRepeatEvents in <= 1.12.2
        if (enableRepeatKeys) {
            UniversalKeyboard.enableRepeatEvents(true)
        }
    }

    override fun onGuiClosed() {
        super.onGuiClosed()

        // We need to disable repeat events when leaving the gui.
        if (enableRepeatKeys) {
            UniversalKeyboard.enableRepeatEvents(false)
        }
    }

    protected fun defaultKeyBehavior(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
    }
}