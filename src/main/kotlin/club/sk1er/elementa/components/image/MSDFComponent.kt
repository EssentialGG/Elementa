package club.sk1er.elementa.components.image

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIImage
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.shaders.*
import club.sk1er.elementa.svg.SVGParser
import club.sk1er.elementa.utils.ResourceCache
import club.sk1er.elementa.utils.Vector2f
import club.sk1er.elementa.utils.Vector4f
import club.sk1er.mods.core.universal.UGraphics
import club.sk1er.mods.core.universal.utils.ReleasedDynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import javax.imageio.ImageIO

open class MSDFComponent @JvmOverloads constructor(
    private val imageFuture: CompletableFuture<BufferedImage>
) : UIComponent() {
    private var texture: ReleasedDynamicTexture? = null

    private val waiting = ConcurrentLinkedQueue<MSDFComponent>()
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
        val tex = texture ?: return super.draw();
        initShaders()
        UGraphics.enableBlend()
        UGraphics.tryBlendFuncSeparate(
            GL11.GL_SRC_ALPHA,
            GL11.GL_ONE_MINUS_SRC_ALPHA,
            GL11.GL_SRC_ALPHA,
            GL11.GL_ONE_MINUS_SRC_ALPHA
        )
        shader.bindIfUsable()

        GL13.glActiveTexture(GL13.GL_TEXTURE0)

        UGraphics.bindTexture(tex.glTextureId)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        sdfTexel.setValue(Vector2f(1f / 128, 1f / 128))
        samplerUniform.setValue(0)
        doffsetUniform.setValue((3.5f / height).toFloat())

        val current = Color(255, 255, 255, 255)
        val amt = Color.RGBtoHSB(current.red, current.green, current.blue, null)[2]
        hintAmountUniform.setValue(amt)
        subpixelAmountUniform.setValue(amt)


        val textureTop = 0.0
        val textureBottom = 1.0
        val textureLeft = (0).toDouble()
        val textureRight = (1).toDouble()

        fgColorUniform.setValue(
            Vector4f(
                1f,
                1f,
                1f,
                1f
            )
        )
        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val doubleX = x.toDouble()
        val doubleY = y.toDouble()
        worldRenderer.pos(doubleX, doubleY + height, 0.0).tex(textureLeft, textureBottom).endVertex()
        worldRenderer.pos(doubleX + width, doubleY + height, 0.0).tex(textureRight, textureBottom).endVertex()
        worldRenderer.pos(doubleX + width, doubleY, 0.0).tex(textureRight, textureTop).endVertex()
        worldRenderer.pos(doubleX, doubleY, 0.0).tex(textureLeft, textureTop).endVertex()
        UGraphics.draw()

        shader.unbindIfUsable()
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

    fun supply(uiImage: MSDFComponent) {
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
        fun ofFile(file: File): MSDFComponent {
            return MSDFComponent(CompletableFuture.supplyAsync { ImageIO.read(file) })
        }

        @JvmStatic
        fun ofURL(url: URL): MSDFComponent {
            return MSDFComponent(CompletableFuture.supplyAsync { ImageIO.read(url) })
        }

        @JvmStatic
        fun ofURL(url: URL, cache: ImageCache): MSDFComponent {
            return MSDFComponent(CompletableFuture.supplyAsync {
                return@supplyAsync cache[url] ?: ImageIO.read(url).also {
                    cache[url] = it
                }
            })
        }

        @JvmStatic
        fun ofResource(path: String): MSDFComponent {
            return MSDFComponent(CompletableFuture.supplyAsync {
                ImageIO.read(this::class.java.getResourceAsStream(path))
            })
        }

        @JvmStatic
        fun ofResourceCached(path: String): UIImage {
            return UIImage.ofResourceCached(path, UIImage.defaultResourceCache)
        }

        @JvmStatic
        fun ofResourceCached(path: String, resourceCache: ResourceCache): UIImage {
            return resourceCache.get(path)
        }

        private lateinit var shader: Shader
        private lateinit var samplerUniform: IntUniform
        private lateinit var doffsetUniform: FloatUniform
        private lateinit var hintAmountUniform: FloatUniform
        private lateinit var subpixelAmountUniform: FloatUniform
        private lateinit var sdfTexel: Vec2Uniform
        private lateinit var fgColorUniform: Vec4Uniform
        //private lateinit var shadowColorUniform: Vec4Uniform

        fun areShadersInitialized() = ::shader.isInitialized

        fun initShaders() {
            if (areShadersInitialized())
                return

            shader = Shader("font", "font")
            samplerUniform = IntUniform(shader.getUniformLocation("msdf"))
            doffsetUniform = FloatUniform(shader.getUniformLocation("doffset"))
            hintAmountUniform = FloatUniform(shader.getUniformLocation("hint_amount"))
            subpixelAmountUniform = FloatUniform(shader.getUniformLocation("subpixel_amount"))
            sdfTexel = Vec2Uniform(shader.getUniformLocation("sdf_texel"))
            fgColorUniform = Vec4Uniform(shader.getUniformLocation("fgColor"))
            //shadowColorUniform = Vec4Uniform(shader.getUniformLocation("shadowColor"))
        }


    }


}
