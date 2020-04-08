package club.sk1er.elementa.components.image

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIImage
import club.sk1er.elementa.utils.decodeBlurHash
import club.sk1er.elementa.utils.drawTexture
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.math.abs

open class BlurHashImage(private val hash: String) : UIComponent(), ImageProvider {
    private lateinit var texture: DynamicTexture
    private var dimensions = BASE_WIDTH to BASE_HEIGHT

    override fun getTexture(preferredWidth: Int, preferredHeight: Int): AbstractTexture {
        if (::texture.isInitialized) {
            if (preferredWidth > 0 && preferredHeight > 0) {
                val sizeDifference = abs(dimensions.first * dimensions.second - preferredWidth * preferredHeight)

                if (sizeDifference > SIZE_THRESHOLD) {
                    dimensions = preferredWidth to preferredHeight
                    texture = generateTexture()
                }
            }

            return texture
        }

        texture = generateTexture()

        return texture
    }

    private fun generateTexture(): DynamicTexture {
        return decodeBlurHash(hash, dimensions.first, dimensions.second)?.let {
            UniversalGraphicsHandler.getTexture(it)
        } ?: run {
            // We encountered an issue decoding the blur hash, it's probably invalid.
            UniversalGraphicsHandler.getEmptyTexture()
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

        drawTexture(getTexture(width.toInt(), height.toInt()), color, x, y, width, height)

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
        const val BASE_WIDTH = 50
        const val BASE_HEIGHT = 50
        const val SIZE_THRESHOLD = 2000

        /**
         * Creates a [UIImage] component that will be backed by a [BlurHashImage] until it is fully
         * loaded.
         */
        @JvmStatic
        fun ofFile(hash: String, file: File): UIImage {
            return UIImage(CompletableFuture.supplyAsync { ImageIO.read(file) }, BlurHashImage(hash))
        }

        /**
         * Creates a [UIImage] component that will be backed by a [BlurHashImage] until it is fully
         * loaded.
         */
        @JvmStatic
        fun ofURL(hash: String, url: URL): UIImage {
            return UIImage(CompletableFuture.supplyAsync { ImageIO.read(url) }, BlurHashImage(hash))
        }

        /**
         * Creates a [UIImage] component that will be backed by a [BlurHashImage] until it is fully
         * loaded.
         */
        @JvmStatic
        fun ofResource(hash: String, path: String): UIImage {
            return UIImage(CompletableFuture.supplyAsync {
                ImageIO.read(this::class.java.getResourceAsStream(path))
            }, BlurHashImage(hash))
        }
    }
}