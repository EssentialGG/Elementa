package club.sk1er.elementa

//#if MC>=11602
//$$ import club.sk1er.mods.core.universal.UGraphics
//#endif

import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.animation.*
import club.sk1er.mods.core.universal.UKeyboard
import club.sk1er.mods.core.universal.UScreen

import java.awt.Color
import kotlin.reflect.KMutableProperty0

abstract class WindowScreen(
    private val enableRepeatKeys: Boolean = true,
    private val drawDefaultBackground: Boolean = true,
    restoreCurrentGuiOnClose: Boolean = false,
    newGuiScale: Int = -1
) : UScreen(restoreCurrentGuiOnClose, newGuiScale) {
    val window = Window()
    private var isInitialized = false

    init {
        window.onKeyType { typedChar, keyCode ->
            defaultKeyBehavior(typedChar, keyCode)
        }
    }

    open fun afterInitialization() { }

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!isInitialized) {
            isInitialized = true
            afterInitialization()
        }

        super.onDrawScreen(mouseX, mouseY, partialTicks)

        //#if MC>=11602
        //$$ UGraphics.setStack(getMatrixStack())
        //#endif

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

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        // We also need to pass along typed keys
        window.keyType(typedChar, keyCode)
    }

    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)

        // Since we want our users to be able to hold a key
        // to type. This is a wrapper around a base LWJGL function.
        // - Keyboard.enableRepeatEvents in <= 1.12.2
        if (enableRepeatKeys)
            UKeyboard.allowRepeatEvents(true)
    }

    override fun onScreenClose() {
        super.onScreenClose()

        // We need to disable repeat events when leaving the gui.
        if (enableRepeatKeys)
            UKeyboard.allowRepeatEvents(false)
    }

    fun defaultKeyBehavior(typedChar: Char, keyCode: Int) {
        super.onKeyPressed(keyCode, typedChar, UKeyboard.getModifiers())
    }

    /**
     * Field animation API
     */

    fun KMutableProperty0<Int>.animate(strategy: AnimationStrategy, time: Float, newValue: Int, delay: Float = 0f) {
        window.apply { this@animate.animate(strategy, time, newValue, delay) }
    }

    fun KMutableProperty0<Float>.animate(strategy: AnimationStrategy, time: Float, newValue: Float, delay: Float = 0f) {
        window.apply { this@animate.animate(strategy, time, newValue, delay) }
    }

    fun KMutableProperty0<Long>.animate(strategy: AnimationStrategy, time: Float, newValue: Long, delay: Float = 0f) {
        window.apply { this@animate.animate(strategy, time, newValue, delay) }
    }

    fun KMutableProperty0<Double>.animate(strategy: AnimationStrategy, time: Float, newValue: Double, delay: Float = 0f) {
        window.apply { this@animate.animate(strategy, time, newValue, delay) }
    }

    fun KMutableProperty0<Color>.animate(strategy: AnimationStrategy, time: Float, newValue: Color, delay: Float = 0f) {
        window.apply { this@animate.animate(strategy, time, newValue, delay) }
    }

    fun KMutableProperty0<*>.stopAnimating() {
        window.apply { this@stopAnimating.stopAnimating() }
    }
}
