package gg.essential.elementa.effects

import gg.essential.elementa.effects.Effect
import gg.essential.elementa.state.State
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UResolution
import gg.essential.universal.shader.BlendState
import gg.essential.universal.shader.SamplerUniform
import gg.essential.universal.shader.UShader
import org.lwjgl.opengl.GL11
import java.io.Closeable
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Applies an alpha value to a component. This is done by snapshotting the framebuffer behind the component,
 * rendering the component, then rendering the snapshot with the inverse of the desired alpha.
 */
class AlphaEffect(private val alphaState: State<Float>) : Effect() {
    private val resources = Resources(this)
    private var textureWidth = -1
    private var textureHeight = -1

    override fun setup() {
        initShader()
        Resources.drainCleanupQueue()
        resources.textureId = GL11.glGenTextures()
    }

    override fun beforeDraw(matrixStack: UMatrixStack) {
        if (resources.textureId == -1) error("AlphaEffect has not yet been setup or has already been cleaned up! ElementaVersion.V4 or newer is required for proper operation!")

        val scale = UResolution.scaleFactor

        // Get the coordinates of the component within the bounds of the screen in real pixels
        val left = (boundComponent.getLeft() * scale).toInt().coerceIn(0..UResolution.viewportWidth)
        val right = (boundComponent.getRight() * scale).toInt().coerceIn(0..UResolution.viewportWidth)
        val top = (boundComponent.getTop() * scale).toInt().coerceIn(0..UResolution.viewportHeight)
        val bottom = (boundComponent.getBottom() * scale).toInt().coerceIn(0..UResolution.viewportHeight)

        val x = left
        val y = UResolution.viewportHeight - bottom // OpenGL screen coordinates start in the bottom left
        val width = right - left
        val height = bottom - top

        if (width == 0 || height == 0 || !shader.usable) {
            return
        }

        UGraphics.configureTexture(resources.textureId) {
            if (width != textureWidth || height != textureHeight) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null as ByteBuffer?)
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
                textureWidth = width
                textureHeight = height
            }

            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, x, y, width, height)
        }
    }

    override fun afterDraw(matrixStack: UMatrixStack) {
        // Get the coordinates of the component within the bounds of the screen in fractional MC pixels
        val left = boundComponent.getLeft().toDouble().coerceIn(0.0..UResolution.viewportWidth / UResolution.scaleFactor)
        val right = boundComponent.getRight().toDouble().coerceIn(0.0..UResolution.viewportWidth / UResolution.scaleFactor)
        val top = boundComponent.getTop().toDouble().coerceIn(0.0..UResolution.viewportHeight / UResolution.scaleFactor)
        val bottom = boundComponent.getBottom().toDouble().coerceIn(0.0..UResolution.viewportHeight / UResolution.scaleFactor)

        val x = left
        val y = top
        val width = right - left
        val height = bottom - top

        if (width == 0.0 || height == 0.0 || !shader.usable) {
            return
        }

        val red = 1f
        val green = 1f
        val blue = 1f
        val alpha = 1f - alphaState.get()

        var prevAlphaTestFunc = 0
        var prevAlphaTestRef = 0f
        if (!UGraphics.isCoreProfile()) {
            prevAlphaTestFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC)
            prevAlphaTestRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF)
            UGraphics.alphaFunc(GL11.GL_ALWAYS, 0f)
        }

        shader.bind()
        textureUniform.setValue(resources.textureId)

        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.beginWithActiveShader(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR)
        worldRenderer.pos(matrixStack, x, y + height, 0.0).tex(0.0, 0.0).color(red, green, blue, alpha).endVertex()
        worldRenderer.pos(matrixStack, x + width, y + height, 0.0).tex(1.0, 0.0).color(red, green, blue, alpha).endVertex()
        worldRenderer.pos(matrixStack, x + width, y, 0.0).tex(1.0, 1.0).color(red, green, blue, alpha).endVertex()
        worldRenderer.pos(matrixStack, x, y, 0.0).tex(0.0, 1.0).color(red, green, blue, alpha).endVertex()
        worldRenderer.drawDirect()

        shader.unbind()

        if (!UGraphics.isCoreProfile()) {
            UGraphics.alphaFunc(prevAlphaTestFunc, prevAlphaTestRef)
        }
    }

    fun cleanup() {
        resources.close()
    }

    private class Resources(effect: AlphaEffect) : PhantomReference<AlphaEffect>(effect, referenceQueue), Closeable {
        var textureId = -1

        init {
            toBeCleanedUp.add(this)
        }

        override fun close() {
            toBeCleanedUp.remove(this)

            if (textureId != -1) {
                GL11.glDeleteTextures(textureId)
                textureId = -1
            }
        }

        companion object {
            val referenceQueue = ReferenceQueue<AlphaEffect>()
            val toBeCleanedUp: MutableSet<Resources> = Collections.newSetFromMap(ConcurrentHashMap())

            fun drainCleanupQueue() {
                while (true) {
                    ((referenceQueue.poll() ?: break) as Resources).close()
                }
            }
        }
    }

    companion object {
        private lateinit var shader: UShader
        private lateinit var textureUniform: SamplerUniform

        private fun initShader() {
            if (::shader.isInitialized) return

            shader = UShader.fromLegacyShader("""
                #version 110
    
                varying vec2 f_Position;
                varying vec2 f_TexCoord;
    
                void main() {
                    f_Position = gl_Vertex.xy;
                    f_TexCoord = gl_MultiTexCoord0.st;
    
                    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
                    gl_FrontColor = gl_Color;
                }
            """.trimIndent(), """
                #version 110
    
                uniform sampler2D u_Texture;
    
                varying vec2 f_Position;
                varying vec2 f_TexCoord;
    
                void main() {
                    gl_FragColor = gl_Color * vec4(texture2D(u_Texture, f_TexCoord).rgb, 1.0);
                }
            """.trimIndent(), BlendState.NORMAL, UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR)

            if (!shader.usable) {
                println("Failed to load AlphaEffect shader")
                return
            }

            textureUniform = shader.getSamplerUniform("u_Texture")
        }
    }
}