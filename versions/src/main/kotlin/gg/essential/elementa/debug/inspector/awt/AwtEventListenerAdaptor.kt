package gg.essential.elementa.debug.inspector.awt

import gg.essential.elementa.components.Window
import gg.essential.elementa.debug.ExternalResolutionManager
import gg.essential.elementa.manager.KeyboardManager
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMinecraft
import org.jetbrains.annotations.ApiStatus
import java.awt.event.*

//#if MC<=11202
/**
 * A [KeyboardManager] implementation that receives its values from a Java AWT
 */
@ApiStatus.Internal
class AwtEventListenerAdaptor(
    private val resolutionManager: ExternalResolutionManager,
    private val window: Window,
) : MouseListener, KeyListener, MouseWheelListener, KeyboardManager {

    private val currentlyPressedKeys = mutableSetOf<Int>()
    private var modifiers = UKeyboard.Modifiers(isCtrl = false, isShift = false, isAlt = false)

    private fun runOnMinecraftThread(runnable: () -> Unit) {
        UMinecraft.getMinecraft().addScheduledTask(runnable)
    }

    override fun mouseReleased(e: MouseEvent) = runOnMinecraftThread { window.mouseRelease() }

    override fun keyPressed(e: KeyEvent) = runOnMinecraftThread {
        awtToGlKeyMap[e.keyCode]?.let { keyCode ->
            currentlyPressedKeys.add(keyCode)
            modifiers = UKeyboard.Modifiers(
                isCtrl = e.isControlDown,
                isShift = e.isShiftDown,
                isAlt = e.isAltDown
            )
            if (keyCode == UKeyboard.KEY_UP) {
                resolutionManager.scaleFactor++
            } else if (keyCode == UKeyboard.KEY_DOWN && resolutionManager.scaleFactor > 1) {
                resolutionManager.scaleFactor++
            } else {
                window.keyType(if (e.keyChar.code == 65535) 0.toChar() else e.keyChar, keyCode)
            }
        }
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) = runOnMinecraftThread {
        window.mouseScroll(-e.preciseWheelRotation)
    }

    override fun keyReleased(e: KeyEvent) = runOnMinecraftThread {
        currentlyPressedKeys.remove(awtToGlKeyMap[e.keyCode] ?: return@runOnMinecraftThread)
    }

    override fun isKeyDown(key: Int): Boolean {
        return key in currentlyPressedKeys
    }

    override fun allowRepeatEvents(enabled: Boolean) {}

    override fun getModifiers(): UKeyboard.Modifiers {
        return modifiers
    }

    override fun mousePressed(e: MouseEvent) = runOnMinecraftThread {
        // Convert from Java AWT buttons to LWJGL buttons
        val mappedButton = when (e.button) {
            MouseEvent.BUTTON1 -> 0
            MouseEvent.BUTTON2 -> 2
            MouseEvent.BUTTON3 -> 1
            else -> return@runOnMinecraftThread
        }
        val mouseX = e.x.toDouble() / resolutionManager.scaleFactor
        val mouseY = e.y.toDouble() / resolutionManager.scaleFactor
        window.mouseClick(mouseX, mouseY, mappedButton)
    }

    /** Not needed **/
    override fun mouseClicked(e: MouseEvent) {}

    override fun keyTyped(e: KeyEvent) {}

    override fun mouseEntered(e: MouseEvent) {}

    override fun mouseExited(e: MouseEvent) {}

    @ApiStatus.Internal
    companion object {

        // Adapted from https://stackoverflow.com/questions/26617817/converting-between-different-keycodes
        private val awtToGlKeyMap = mapOf(
            8 to 14,
            32 to 57,
            9 to 15,
            13 to 28,
            10 to 28,
            16 to 42,
            17 to 29,
            18 to 56,
            19 to 197,
            20 to 58,
            27 to 1,
            33 to 201,
            34 to 209,
            35 to 207,
            36 to 199,
            37 to 203,
            38 to 200,
            39 to 205,
            40 to 208,
            155 to 210,
            127 to 211,
            48 to 11,
            49 to 2,
            50 to 3,
            51 to 4,
            5 to 5,
            53 to 6,
            54 to 7,
            55 to 8,
            56 to 9,
            57 to 10,
            65 to 30,
            66 to 48,
            67 to 46,
            68 to 32,
            69 to 18,
            70 to 33,
            71 to 34,
            72 to 35,
            73 to 23,
            74 to 36,
            75 to 37,
            76 to 38,
            77 to 50,
            78 to 49,
            79 to 24,
            80 to 25,
            81 to 16,
            82 to 19,
            83 to 31,
            84 to 20,
            85 to 22,
            86 to 47,
            87 to 17,
            88 to 45,
            89 to 21,
            90 to 44,
            16777413 to 27,
            16777412 to 40,
            16777430 to 41,
            91 to 219,
            92 to 220,
            524 to 219,
            93 to 221,
            96 to 82,
            97 to 79,
            98 to 80,
            99 to 81,
            100 to 75,
            101 to 76,
            102 to 77,
            103 to 71,
            104 to 72,
            105 to 73,
            106 to 55,
            107 to 78,
            109 to 74,
            110 to 83,
            111 to 181,
            112 to 59,
            113 to 60,
            114 to 61,
            115 to 62,
            116 to 63,
            117 to 64,
            118 to 65,
            119 to 66,
            120 to 67,
            121 to 68,
            122 to 87,
            123 to 88,
            124 to 100,
            125 to 101,
            126 to 102,
            144 to 69,
            145 to 70,
            186 to 39,
            187 to 13,
            188 to 51,
            44 to 51,
            189 to 12,
            190 to 52,
            46 to 52,
            191 to 53,
            192 to 41,
            219 to 26,
            220 to 43,
            221 to 27,
            222 to 40,
            16777383 to 43,
            521 to 13,
            45 to 12,
            135 to 144,
        )
    }


}
//#endif