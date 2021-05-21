package gg.essential.elementa.components.image

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.utils.decodeBlurHash
import gg.essential.elementa.utils.drawTexture
import gg.essential.universal.UGraphics
import gg.essential.universal.utils.ReleasedDynamicTexture
import java.awt.Color
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.math.abs

open class BlurHashImage(private val hash: String) : UIComponent(), ImageProvider {
    private lateinit var texture: ReleasedDynamicTexture
    private var dimensions = BASE_WIDTH to BASE_HEIGHT

    private fun generateTexture(): ReleasedDynamicTexture {
        return decodeBlurHash(hash, dimensions.first.toInt(), dimensions.second.toInt())?.let {
            UGraphics.getTexture(it)
        } ?: run {
            // We encountered an issue decoding the blur hash, it's probably invalid.
            UGraphics.getEmptyTexture()
        }
    }

    override fun drawImage(x: Double, y: Double, width: Double, height: Double, color: Color) {
        if (::texture.isInitialized) {
            if (width > 0 && height > 0) {
                val sizeDifference = abs(dimensions.first * dimensions.second - width * height)

                if (sizeDifference > SIZE_THRESHOLD) {
                    dimensions = width to height
                    texture = generateTexture()
                }
            }
        } else {
            texture = generateTexture()
        }


        drawTexture(texture, color, x, y, width, height)
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
            UGraphics.deleteTexture(glTextureId)
        }
    }

    companion object {
        const val BASE_WIDTH = 50.0
        const val BASE_HEIGHT = 50.0
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
