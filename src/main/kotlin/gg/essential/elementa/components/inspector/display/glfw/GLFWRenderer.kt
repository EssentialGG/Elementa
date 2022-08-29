package gg.essential.elementa.components.inspector.display.glfw

import gg.essential.elementa.debug.FrameBufferedWindow
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class GLFWRenderer(
    private val frameBuffer: FrameBufferedWindow,
    private val window: Long,
    private val display: GLFWDisplay,
) {
    private val workerPool = Executors.newSingleThreadScheduledExecutor()
    private var contextCurrent = false

    init {
        schedule(1000 / 30, TimeUnit.MILLISECONDS) {
            if (!display.visible) {
                return@schedule
            }
            if (!contextCurrent) {
                GLFW.glfwMakeContextCurrent(window)
                GL.createCapabilities()
            }
            val width = display.getWidth()
            val height = display.getHeight()
            GL11.glViewport(0, 0, width, height)
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glLoadIdentity()
            GL11.glOrtho(0.0, width.toDouble(), height.toDouble(), 0.0, -1.0, 1.0)
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPushMatrix()
            GL11.glColor3f(1f, 1f, 1f)

            frameBuffer.renderDirect()

            GL11.glPopMatrix()
            GLFW.glfwSwapBuffers(window)
        }
    }

    private fun schedule(period: Long, unit: TimeUnit, runnable: () -> Unit) {
        workerPool.scheduleAtFixedRate(runnable, 0, period, unit)
    }
}