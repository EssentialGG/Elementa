package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.concurrent.thread

open class UIImage(private val imageFuture: () -> BufferedImage) : UIComponent() {
    private var loadedImage: BufferedImage? = null
    private lateinit var texture: DynamicTexture
    private var fetching = false

    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (loadingImage != null) {
            loadingTexture = DynamicTexture(loadingImage)
            loadingImage = null
        }

        val textureToUse: DynamicTexture

        if (loadedImage != null) {
            texture = DynamicTexture(loadedImage)
            textureToUse = texture
            loadedImage = null
        } else {
            if (!fetching && Window.of(this).isAreaVisible(x, y, x + width, y + width)) {
                fetching = true
                thread {
                    loadedImage = imageFuture()
                }
            }

            textureToUse = loadingTexture
        }

        if (color.alpha == 0) {
            return
        }

        GL11.glPushMatrix()

        GlStateManager.enableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.color(
            color.red / 255f, color.green / 255f,
            color.blue / 255f, color.alpha / 255f
        )
        GlStateManager.scale(1f, 1f, 50f)
        GlStateManager.bindTexture(textureToUse.glTextureId)
        GlStateManager.enableTexture2D()

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)

        worldRenderer.pos(x, y + height, 0.0).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(x + width, y + height, 0.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(x + width, y, 0.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()

        GL11.glPopMatrix()

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
    }
}