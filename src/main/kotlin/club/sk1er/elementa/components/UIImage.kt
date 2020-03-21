package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.concurrent.thread

open class UIImage(private val imageFuture: () -> BufferedImage) : UIComponent() {
    private var image: BufferedImage? = null
    private lateinit var texture: DynamicTexture
    private var fetching = false
    var imageWidth = 1f
    var imageHeight = 1f

    @Throws(Throwable::class)
    protected fun finalize() {
        val glTextureId = texture.glTextureId
        if (glTextureId != 0 && glTextureId != -1) {
            UniversalGraphicsHandler.deleteTexture(glTextureId);
        }
    }

    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (!Window.of(this).isAreaVisible(x, y, x + width, y + width)) {
            return super.draw()
        }

        createLoadingTexture()

        val textureToUse: DynamicTexture = when {
            image != null -> {
                texture = DynamicTexture(image)
                imageWidth = image!!.width.toFloat()
                imageHeight = image!!.height.toFloat()
                image = null

                texture
            }
            ::texture.isInitialized -> {
                texture
            }
            else -> {
                if (!fetching) {
                    fetching = true
                    thread {
                        image = imageFuture()
                    }
                }

                loadingTexture
            }
        }

        if (color.alpha == 0) {
            return super.draw()
        }

        drawTexture(textureToUse, color, x, y, width, height)

        super.draw()
    }

    companion object {
        @JvmStatic
        fun ofFile(file: File): UIImage {
            return UIImage { ImageIO.read(file) }
        }

        @JvmStatic
        fun ofURL(url: URL): UIImage {
            return UIImage { ImageIO.read(url) }
        }

        @JvmStatic
        fun ofResource(path: String): UIImage {
            return UIImage { ImageIO.read(this::class.java.getResourceAsStream(path)) }
        }

        private var loadingImage = ImageIO.read(this::class.java.getResourceAsStream("/loading.png"))
        private lateinit var loadingTexture: DynamicTexture

        private fun createLoadingTexture() {
            if (loadingImage != null) {
                loadingTexture = DynamicTexture(loadingImage)
                loadingImage = null
            }
        }

        internal fun drawTexture(
            texture: AbstractTexture,
            color: Color,
            x: Double,
            y: Double,
            width: Double,
            height: Double
        ) {
            UniversalGraphicsHandler.pushMatrix()

            UniversalGraphicsHandler.enableBlend()
            UniversalGraphicsHandler.enableAlpha()
            UniversalGraphicsHandler.scale(1f, 1f, 50f)
            UniversalGraphicsHandler.bindTexture(texture.glTextureId)
            UniversalGraphicsHandler.enableTexture2D()
            val red = color.red.toFloat() / 255f
            val green = color.green.toFloat() / 255f
            val blue = color.blue.toFloat() / 255f
            val alpha = color.alpha.toFloat() / 255f
            val worldRenderer = UniversalGraphicsHandler.getFromTessellator()

            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)

            worldRenderer.pos(x, y + height, 0.0).tex(0.0, 1.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x + width, y + height, 0.0).tex(1.0, 1.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x + width, y, 0.0).tex(1.0, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x, y, 0.0).tex(0.0, 0.0).color(red, green, blue, alpha).endVertex()
            UniversalGraphicsHandler.draw()

            UniversalGraphicsHandler.popMatrix()
        }
    }
}