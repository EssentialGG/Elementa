package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.image.DefaultLoadingImage
import club.sk1er.elementa.components.image.ImageProvider
import club.sk1er.elementa.utils.drawTexture
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

open class UIImage(
    private val imageFuture: CompletableFuture<BufferedImage>,
    private val loadingImage: ImageProvider = DefaultLoadingImage
) : UIComponent(), ImageProvider {
    private lateinit var texture: DynamicTexture

    var imageWidth = 0
    var imageHeight = 0

    @Deprecated(
        "Please provide a completable future instead",
        ReplaceWith("CompletableFuture.supplyAsync(imageFunction)", "java.util.concurrent.CompletableFuture"),
        level = DeprecationLevel.ERROR
    )
    constructor(imageFunction: () -> BufferedImage) : this(CompletableFuture.supplyAsync(imageFunction))

    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (color.alpha == 0) {
            return super.draw()
        }

        drawTexture(this.getTexture(width.toInt(), height.toInt()), color, x, y, width, height)

        super.draw()
    }

    override fun getTexture(preferredWidth: Int, preferredHeight: Int): AbstractTexture {
        if (::texture.isInitialized) {
            return texture
        }

        if (imageFuture.isDone) {
            val image = imageFuture.get()

            imageWidth = image.width
            imageHeight = image.height
            texture = UniversalGraphicsHandler.getTexture(image)
        }

        return loadingImage.getTexture(preferredWidth, preferredHeight)
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        val glTextureId = texture.glTextureId
        if (glTextureId != 0 && glTextureId != -1) {
            UniversalGraphicsHandler.deleteTexture(glTextureId);
        }
    }

    companion object {
        @JvmStatic
        fun ofFile(file: File): UIImage {
            return UIImage(CompletableFuture.supplyAsync { ImageIO.read(file) })
        }

        @JvmStatic
        fun ofURL(url: URL): UIImage {
            return UIImage(CompletableFuture.supplyAsync { ImageIO.read(url) })
        }

        @JvmStatic
        fun ofResource(path: String): UIImage {
            return UIImage(CompletableFuture.supplyAsync {
                ImageIO.read(this::class.java.getResourceAsStream(path))
            })
        }
    }
}