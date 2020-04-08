package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.utils.decodeBlurHash
import club.sk1er.elementa.utils.drawTexture
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.texture.DynamicTexture
import kotlin.concurrent.thread
import kotlin.math.abs

open class BlurHashImage(private val hash: String) : UIComponent() {
    private var currentDimensions = 0 to 0
    private lateinit var texture: DynamicTexture

    init {
        generateImage(50, 50)
    }

    private fun generateImage(width: Int = getWidth().toInt(), height: Int = getHeight().toInt()) {
        val image = decodeBlurHash(hash, width, height) ?: run {
            // We encountered an issue decoding the blur hash, it's probably invalid.
            texture = DynamicTexture(0, 0)
            return
        }

        texture = UniversalGraphicsHandler.getTexture(image)

        currentDimensions = width to height
    }


    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        val sizeDifference = abs(width * height - currentDimensions.first * currentDimensions.second)
        if (sizeDifference > 2500) {
            generateImage()
        }

        if (color.alpha == 0) {
            return super.draw()
        }

        drawTexture(texture, color, x, y, width, height)

        super.draw()
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        val glTextureId = texture.glTextureId
        if (glTextureId != 0 && glTextureId != -1) {
            UniversalGraphicsHandler.deleteTexture(glTextureId);
        }
    }
}