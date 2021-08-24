package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.image.CacheableImage
import gg.essential.elementa.components.image.DefaultLoadingImage
import gg.essential.elementa.components.image.ImageCache
import gg.essential.elementa.components.image.ImageProvider
import gg.essential.elementa.svg.SVGParser
import gg.essential.elementa.utils.ResourceCache
import gg.essential.elementa.utils.drawTexture
import gg.essential.universal.UGraphics
import gg.essential.universal.UMinecraft
import gg.essential.universal.utils.ReleasedDynamicTexture
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import javax.imageio.ImageIO

/**
 * Component for drawing arbitrary images from [BufferedImage].
 *
 * There are companion functions available to get [UIImage]s from other sources,
 * such as URLs: [Companion.ofURL], [Companion.ofFile] and [Companion.ofResource].
 */
open class UIImage @JvmOverloads constructor(
    private val imageFuture: CompletableFuture<BufferedImage>,
    private val loadingImage: ImageProvider = DefaultLoadingImage,
    private val failureImage: ImageProvider = SVGComponent(failureSVG)
) : UIComponent(), ImageProvider, CacheableImage {
    private var texture: ReleasedDynamicTexture? = null

    private val waiting = ConcurrentLinkedQueue<CacheableImage>()
    var imageWidth = 1f
    var imageHeight = 1f
    var destroy = true
    val isLoaded: Boolean
        get() = texture != null

    init {
        imageFuture.thenAcceptAsync {
            if (it == null) {
                destroy = false
                return@thenAcceptAsync
            }
            imageWidth = it.width.toFloat()
            imageHeight = it.height.toFloat()
            imageFuture.obtrudeValue(null)

            //In versions before 1.15, we make the bufferedImage.getRGB call without the upload in the
            // constructor since that takes most of the CPU time and we upload the actual texture during the
            // first call to uploadTexture or getGlTextureId
            //#if MC<11502
            texture = UGraphics.getTexture(it)
            //#endif
            Window.enqueueRenderOperation {
                //#if MC>=11500
                //$$ texture = UGraphics.getTexture(it)
                //#else
                texture?.uploadTexture()
                //#endif
                while (waiting.isEmpty().not())
                    waiting.poll().applyTexture(texture)
            }
        }
    }

    @Deprecated(
        "Please provide a completable future instead",
        ReplaceWith("CompletableFuture.supplyAsync(imageFunction)", "java.util.concurrent.CompletableFuture"),
        level = DeprecationLevel.ERROR
    )
    constructor(imageFunction: () -> BufferedImage) : this(CompletableFuture.supplyAsync(imageFunction))

    override fun drawImage(x: Double, y: Double, width: Double, height: Double, color: Color) {
        when {
            texture != null -> drawTexture(texture!!, color, x, y, width, height)
            imageFuture.isCompletedExceptionally -> failureImage.drawImage(x, y, width, height, color)
            else -> loadingImage.drawImage(x, y, width, height, color)
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
        if (!destroy) return
        val glTextureId = texture?.glTextureId
        if (glTextureId != null && glTextureId != 0 && glTextureId != -1) {
            UGraphics.deleteTexture(glTextureId)
        }
    }

    override fun supply(image: CacheableImage) {
        if (texture != null) {
            image.applyTexture(texture)
            return
        }
        waiting.add(image)
    }

    override fun applyTexture(texture: ReleasedDynamicTexture?) {
        this.texture = texture;
    }

    companion object {
        private val failureSVG = SVGParser.parseFromResource("/svg/failure.svg")
        val defaultResourceCache = ResourceCache(50)

        @JvmStatic
        fun ofFile(file: File): UIImage {
            return UIImage(CompletableFuture.supplyAsync { ImageIO.read(file) })
        }

        @JvmStatic
        fun ofURL(url: URL): UIImage {
            return UIImage(CompletableFuture.supplyAsync { get(url) })
        }

        @JvmStatic
        fun ofURL(url: URL, cache: ImageCache): UIImage {
            return UIImage(CompletableFuture.supplyAsync {
                return@supplyAsync cache[url] ?: get(url).also {
                    cache[url] = it
                }
            })
        }

        @JvmStatic
        fun ofResource(path: String): UIImage {
            return UIImage(CompletableFuture.supplyAsync {
                ImageIO.read(this::class.java.getResourceAsStream(path))
            })
        }

        @JvmStatic
        fun ofResourceCached(path: String): UIImage {
            return ofResourceCached(path, defaultResourceCache)
        }

        @JvmStatic
        fun ofResourceCached(path: String, resourceCache: ResourceCache): UIImage {
            return resourceCache.getUIImage(path) as UIImage
        }

        @JvmStatic
        fun get(url: URL): BufferedImage {
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.useCaches = true
            connection.addRequestProperty("User-Agent", "Mozilla/4.76 (Elementa)")
            connection.doOutput = true

            return ImageIO.read(connection.inputStream)
        }
    }
}
