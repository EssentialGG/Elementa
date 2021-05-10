package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.image.DefaultLoadingImage
import club.sk1er.elementa.components.image.ImageCache
import club.sk1er.elementa.components.image.ImageProvider
import club.sk1er.elementa.svg.SVGParser
import club.sk1er.elementa.utils.ResourceCache
import club.sk1er.elementa.utils.drawTexture
import club.sk1er.mods.core.universal.UGraphics
import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import javax.imageio.ImageIO

open class UIImage @JvmOverloads constructor(
    private val imageFuture: CompletableFuture<BufferedImage>,
    private val loadingImage: ImageProvider = DefaultLoadingImage,
    private val failureImage: ImageProvider = SVGComponent(failureSVG)
) : UIComponent(), ImageProvider {
    private var texture: DynamicTexture? = null

    private val waiting = ConcurrentLinkedQueue<UIImage>()
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

            Window.enqueueRenderOperation {
                texture = UGraphics.getTexture(it)
                while (waiting.isEmpty().not())
                    waiting.poll().texture = texture
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

    fun supply(uiImage: UIImage) {
        if (texture != null) {
            uiImage.texture = texture
            return
        }
        waiting.add(uiImage)
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
            return UIImage(CompletableFuture.supplyAsync { ImageIO.read(url) })
        }

        @JvmStatic
        fun ofURL(url: URL, cache: ImageCache): UIImage {
            return UIImage(CompletableFuture.supplyAsync {
                return@supplyAsync cache[url] ?: ImageIO.read(url).also {
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
            return resourceCache.get(path)
        }


    }


}
