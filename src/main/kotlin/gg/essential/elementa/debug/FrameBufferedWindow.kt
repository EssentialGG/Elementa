package gg.essential.elementa.debug

import gg.essential.elementa.components.Window
import gg.essential.elementa.impl.ExternalInspectorDisplay
import gg.essential.elementa.impl.Platform.Companion.platform
import gg.essential.elementa.manager.ResolutionManager
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import org.jetbrains.annotations.ApiStatus
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import java.nio.ByteBuffer

@ApiStatus.Internal
class FrameBufferedWindow(
    private val wrappedWindow: Window,
    private val externalDisplay: ExternalInspectorDisplay,
) {

    // Frame buffer properties
    private var frameBuffer = -1
    private var frameBufferTexture = -1
    private var frameBufferWidth = -1
    private var frameBufferHeight = -1


    /**
     * Called on the Minecraft thread update the buffer
     */
    fun updateFrameBuffer(resolutionManager: ResolutionManager) {

        val frameWidth = externalDisplay.getWidth()
        val frameHeight = externalDisplay.getHeight()

        // check if the frame width or height changed
        if (frameWidth != frameBufferWidth || frameHeight != frameBufferHeight || frameBuffer == -1 || frameBufferTexture == -1) {
            frameBufferWidth = frameWidth
            frameBufferHeight = frameHeight
            reconfigureFrameBuffer(frameWidth, frameHeight)
            wrappedWindow.onWindowResize()
        }

        withFrameBuffer(frameBuffer) {

            // Prepare frame buffer
            val scissorState = glGetBoolean(GL_SCISSOR_TEST)
            glDisable(GL_SCISSOR_TEST)
            glClearColor(0f, 0f, 0f, 0f)
            glClearDepth(1.0)
            glClearStencil(0)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
            glViewport(0, 0, frameWidth, frameHeight)

            // Undo MC's scaling and the distortion caused by different viewport size with same projection matrix
            val stack = UMatrixStack()
            val scale = wrappedWindow.resolutionManager.scaleFactor / resolutionManager.scaleFactor
            stack.scale(
                scale * resolutionManager.viewportWidth / frameWidth,
                scale * resolutionManager.viewportHeight / frameHeight,
                1.0,
            )

            // Rendering
            wrappedWindow.draw(stack)

            glViewport(0, 0, resolutionManager.viewportWidth, resolutionManager.viewportHeight)


            if (scissorState) glEnable(GL_SCISSOR_TEST)
        }
    }


    /**
     * Sets up the frame buffer with the supplied [width] and [height].
     * If the frame buffer already exits, it will be deleted and recreated with the new size.
     */
    private fun reconfigureFrameBuffer(width: Int, height: Int) {
        if (frameBuffer != -1) {
            platform.deleteFramebuffers(frameBuffer)
            frameBuffer = -1
        }
        if (frameBufferTexture != -1) {
            glDeleteTextures(frameBufferTexture)
            frameBufferTexture = -1
        }
        frameBuffer = platform.genFrameBuffers()
        frameBufferTexture = glGenTextures()
        UGraphics.configureTexture(frameBufferTexture) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                width,
                height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                null as ByteBuffer?
            )
        }
        withFrameBuffer(frameBuffer) {
            platform.framebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D,
                frameBufferTexture,
                0
            )
        }
    }

    /**
     * Renders the contents of the frame buffer
     */
    fun renderDirect() {
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_CULL_FACE)
        glBindTexture(GL_TEXTURE_2D, frameBufferTexture)
        glBegin(GL_QUADS)

        val vertexData = arrayOf(
            0f to 0f,
            frameBufferWidth.toFloat() to 0f,
            frameBufferWidth.toFloat() to frameBufferHeight.toFloat(),
            0f to frameBufferHeight.toFloat()
        )
        val textureData = arrayOf(
            0f to 1f,
            1f to 1f,
            1f to 0f,
            0f to 0f
        )

        vertexData.forEachIndexed { index, pair ->
            val (x, y) = pair
            val (u, v) = textureData[index]
            glTexCoord2f(u, v)
            glVertex2f(x, y)
        }

        glEnd()
    }

    /**
     * Runs the supplied [block] with the frame buffer bound
     */
    private fun withFrameBuffer(glId: Int, block: Runnable) {
        val prevReadFrameBufferBinding = glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING)
        val prevDrawFrameBufferBinding = glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING)

        platform.bindFramebuffer(GL30.GL_FRAMEBUFFER, glId)
        block.run()

        platform.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFrameBufferBinding)
        platform.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFrameBufferBinding)
    }

    /**
     * Deletes the frame buffer
     */
    fun deleteFrameBuffer() {
        if (frameBufferTexture != -1) {
            glDeleteTextures(frameBufferTexture)
            frameBufferTexture = -1
        }

        if (frameBuffer != -1) {
            platform.deleteFramebuffers(frameBuffer)
            frameBuffer = -1
        }
    }
}