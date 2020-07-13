package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.image.DefaultLoadingImage
import club.sk1er.elementa.components.image.ImageProvider
import club.sk1er.elementa.utils.drawTexture
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

open class UIImage @JvmOverloads constructor(
    private val imageFuture: CompletableFuture<BufferedImage>,
    private val loadingImage: ImageProvider = DefaultLoadingImage,
    private val failureImage: ImageProvider = SVGComponent.ofResource("/failure.svg")
) : UIComponent(), ImageProvider {
    private lateinit var texture: DynamicTexture

    var imageWidth = 1f
    var imageHeight = 1f

    @Deprecated(
        "Please provide a completable future instead",
        ReplaceWith("CompletableFuture.supplyAsync(imageFunction)", "java.util.concurrent.CompletableFuture"),
        level = DeprecationLevel.ERROR
    )
    constructor(imageFunction: () -> BufferedImage) : this(CompletableFuture.supplyAsync(imageFunction))

    override fun drawImage(x: Double, y: Double, width: Double, height: Double, color: Color) {
        if (::texture.isInitialized) {
            drawTexture(texture, color, x, y, width, height)
        } else if (imageFuture.isDone) {
            if (imageFuture.isCompletedExceptionally) {
                failureImage.drawImage(x, y, width, height, color)
            } else {
                val image = imageFuture.get()

                imageWidth = image.width.toFloat()
                imageHeight = image.height.toFloat()
                texture = UniversalGraphicsHandler.getTexture(image)

                imageFuture.obtrudeValue(null)
            }
        } else {
            loadingImage.drawImage(x, y, width, height, color)
        }
    }

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

        drawImage(x, y, width, height, color)

        super.draw()
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