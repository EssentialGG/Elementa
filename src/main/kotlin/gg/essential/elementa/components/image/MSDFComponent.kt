package gg.essential.elementa.components.image

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.Window
import gg.essential.elementa.shaders.*
import gg.essential.elementa.svg.SVGParser
import gg.essential.elementa.utils.ResourceCache
import gg.essential.elementa.utils.Vector2f
import gg.essential.elementa.utils.Vector4f
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.utils.ReleasedDynamicTexture
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

open class MSDFComponent constructor(
    private val imageFuture: CompletableFuture<BufferedImage>
) : UIComponent(), CacheableImage {
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

            Window.enqueueRenderOperation {
                texture = UGraphics.getTexture(it)
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


    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (color.alpha == 0) {
            return super.draw(matrixStack)
        }
        val tex = texture ?: return super.draw(matrixStack)
        while (waiting.isEmpty().not())
            waiting.poll().applyTexture(texture)

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

        val current = getColor()
        subpixelAmountUniform.setValue(0f)
        hintAmountUniform.setValue(0f)


        val textureTop = 0.0
        val textureBottom = 1.0
        val textureLeft = (0).toDouble()
        val textureRight = (1).toDouble()

        fgColorUniform.setValue(
            Vector4f(
                current.red / 255F,
                current.green / 255F,
                current.blue / 255F,
                1f
            )
        )
        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.beginWithActiveShader(UGraphics.DrawMode.QUADS, DefaultVertexFormats.POSITION_TEX)
        val doubleX = x.toDouble()
        val doubleY = y.toDouble()
        worldRenderer.pos(matrixStack, doubleX, doubleY + height, 0.0).tex(textureLeft, textureBottom).endVertex()
        worldRenderer.pos(matrixStack, doubleX + width, doubleY + height, 0.0).tex(textureRight, textureBottom).endVertex()
        worldRenderer.pos(matrixStack, doubleX + width, doubleY, 0.0).tex(textureRight, textureTop).endVertex()
        worldRenderer.pos(matrixStack, doubleX, doubleY, 0.0).tex(textureLeft, textureTop).endVertex()
        worldRenderer.drawDirect()

        shader.unbindIfUsable()
        super.draw(matrixStack)

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
        this.texture=texture;
    }

    companion object {

        @JvmStatic
        fun ofFile(file: File): MSDFComponent {
            return MSDFComponent(CompletableFuture.supplyAsync { ImageIO.read(file) })
        }

        @JvmStatic
        fun ofURL(url: URL): MSDFComponent {
            return MSDFComponent(CompletableFuture.supplyAsync { UIImage.get(url) })
        }

        @JvmStatic
        fun ofURL(url: URL, cache: ImageCache): MSDFComponent {
            return MSDFComponent(CompletableFuture.supplyAsync {
                return@supplyAsync cache[url] ?: UIImage.get(url).also {
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
        fun ofResourceCached(path: String): MSDFComponent {
            return ofResourceCached(path, UIImage.defaultResourceCache)
        }

        @JvmStatic
        fun ofResourceCached(path: String, resourceCache: ResourceCache): MSDFComponent {
            return resourceCache.getMSDFComponent(path)
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
