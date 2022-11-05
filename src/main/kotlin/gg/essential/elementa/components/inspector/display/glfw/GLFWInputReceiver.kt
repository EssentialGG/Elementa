package gg.essential.elementa.components.inspector.display.glfw

import gg.essential.elementa.components.Window
import gg.essential.elementa.impl.Platform
import gg.essential.elementa.manager.KeyboardManager
import gg.essential.elementa.manager.MousePositionManager
import gg.essential.elementa.manager.ResolutionManager
import gg.essential.universal.UKeyboard
import org.lwjgl.glfw.GLFW


internal class GLFWInputReceiver(
    private val windowPointer: Long,
    private val resolutionManager: ResolutionManager,
    private val window: Window,
) : MousePositionManager, KeyboardManager {


    override var rawX: Double = -1.0

    override var rawY: Double = -1.0

    override val scaledX: Double
        get() = rawX / resolutionManager.scaleFactor

    override val scaledY: Double
        get() = rawY / resolutionManager.scaleFactor

    private var allowRepeatEvents = true

    private fun runOnMinecraftThread(runnable: () -> Unit) {
        Platform.platform.runOnMinecraftThread(runnable)
    }


    init {
        GLFW.glfwSetCursorPosCallback(windowPointer) { _: Long, x: Double, y: Double ->
            runOnMinecraftThread {
                this.rawX = x
                this.rawY = y
            }
        }
        GLFW.glfwSetMouseButtonCallback(windowPointer) { _: Long, button: Int, action: Int, _: Int ->
            runOnMinecraftThread {
                if (action == GLFW.GLFW_PRESS) {
                    window.mouseClick(
                        rawX / resolutionManager.scaleFactor,
                        rawY / resolutionManager.scaleFactor,
                        button
                    )
                } else if (action == GLFW.GLFW_RELEASE) {
                    window.mouseRelease()
                }
            }
        }
        GLFW.glfwSetScrollCallback(windowPointer) { _: Long, _: Double, vertical: Double ->
            runOnMinecraftThread {
                window.mouseScroll(vertical)
            }
        }
        GLFW.glfwSetKeyCallback(windowPointer) { _: Long, key: Int, _: Int, action: Int, _: Int ->
            runOnMinecraftThread {
                if (key == 0) {
                    return@runOnMinecraftThread
                }

                if (action == GLFW.GLFW_PRESS || (action == GLFW.GLFW_REPEAT && allowRepeatEvents)) {
                    window.keyType(0.toChar(), key)
                }
            }
        }
        GLFW.glfwSetCharModsCallback(windowPointer) { _: Long, keyInt: Int, _: Int ->
            runOnMinecraftThread {
                if (Character.charCount(keyInt) == 1) {
                    window.keyType(keyInt.toChar(), 0)
                } else {
                    val chars = Character.toChars(keyInt)
                    for (char in chars) {
                        window.keyType(char, 0)
                    }
                }
            }
        }
    }

    override fun isKeyDown(key: Int): Boolean {
        val state = if (key < 20) GLFW.glfwGetMouseButton(windowPointer, key) else GLFW.glfwGetKey(windowPointer, key)
        return state == GLFW.GLFW_PRESS
    }

    override fun allowRepeatEvents(enabled: Boolean) {
        allowRepeatEvents = enabled
    }

    override fun getModifiers(): UKeyboard.Modifiers = UKeyboard.Modifiers(
        UKeyboard.isCtrlKeyDown(),
        UKeyboard.isShiftKeyDown(),
        UKeyboard.isAltKeyDown(),
    )

}