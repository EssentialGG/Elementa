package gg.essential.elementa.components.inspector.display.awt

import gg.essential.elementa.debug.FrameBufferedWindow
import org.lwjgl.opengl.AWTGLCanvas
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.PixelFormat
import org.lwjgl.opengl.SharedDrawable
import org.lwjgl.util.glu.GLU.gluOrtho2D
import java.awt.GraphicsEnvironment

/**
 * A Java AWT component that renders the contents of the supplied [bufferedWindow]
 */
internal class AwtFrameBufferCanvas(
    private val bufferedWindow: FrameBufferedWindow,
) : AWTGLCanvas(
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice,
    PixelFormat(),
    SharedDrawable(Display.getDrawable()),
) {


    override fun paintGL() {
        glViewport(0, 0, width, height)
        glClear(GL_COLOR_BUFFER_BIT)
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        gluOrtho2D(0.0f, width.toFloat(), height.toFloat(), 0.0f)
        glMatrixMode(GL_MODELVIEW)
        glPushMatrix()
        glColor3f(1f, 1f, 1f)

        bufferedWindow.renderFrameBufferTexture()

        glPopMatrix()
        swapBuffers()
        repaint()
    }
}