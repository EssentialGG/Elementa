package club.sk1er.elementa.font

import club.sk1er.elementa.font.data.Font
import club.sk1er.elementa.font.data.Glyph
import club.sk1er.elementa.shaders.*
import club.sk1er.elementa.utils.Vector2f
import club.sk1er.elementa.utils.Vector4f
import club.sk1er.mods.core.universal.UGraphics
import club.sk1er.mods.core.universal.UMinecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL14
import java.awt.Color
import kotlin.math.max
import kotlin.math.roundToInt

class FontRenderer(private val font: Font) {
    fun getStringWidth(string: String, pointSize: Float): Float {
        var width = 0f
        string.forEach { char ->
            val glyph = font.fontInfo.glyphs[char.toInt()] ?: return@forEach

            width += (glyph.advance * pointSize)
        }
        return width
    }

    fun getStringHeight(string: String, pointSize: Float): Float {
        var maxHeight = 0f
        string.forEach { char ->
            val glyph = font.fontInfo.glyphs[char.toInt()] ?: return@forEach
            val planeBounds = glyph.planeBounds ?: return@forEach

            val height = (planeBounds.top - planeBounds.bottom) * pointSize
            if (height > maxHeight)
                maxHeight = height
        }
        return maxHeight
    }

    fun getLineHeight(pointSize: Float): Float {
        return font.fontInfo.metrics.lineHeight * pointSize
    }

    fun drawString(string: String, color: Color, x: Float, y: Float, pointSize: Float) {
        if (!areShadersInitialized())
            return

        val fontTexture = font.getTexture()

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager.bindTexture(fontTexture.glTextureId)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)

        GlStateManager.enableBlend()

        shader.bindIfUsable()
        samplerUniform.setValue(0)
        // distanceFactorUniform.setValue(font.fontInfo.atlas.distanceRange * pointSize)
//         pxRangeUniform.setValue(font.fontInfo.atlas.distanceRange)
//         widthMultiplierUniform.setValue(1.8f)

        val guiScale = UMinecraft.getMinecraft().gameSettings.guiScale

        val metrics = font.fontInfo.metrics
        val baseline = y + ((metrics.lineHeight - metrics.ascender) * pointSize)

        var currentX = x
        string.forEach { char ->
            val glyph = font.fontInfo.glyphs[char.toInt()] ?: return@forEach
            val planeBounds = glyph.planeBounds ?: return@forEach

            val width = (planeBounds.right - planeBounds.left) * pointSize
            val height = (planeBounds.top - planeBounds.bottom) * pointSize
            val adjustedY = baseline - (planeBounds.top * pointSize)
            val hintedY = (adjustedY * guiScale).roundToInt().toFloat() / guiScale

            drawGlyph(
                glyph,
                color,
                currentX,
                hintedY,
                width,
                height
            )

            currentX += (glyph.advance * pointSize)
            currentX = (currentX * guiScale).roundToInt().toFloat() / guiScale
        }

        shader.unbindIfUsable()
    }

    private fun drawGlyph(glyph: Glyph, color: Color, x: Float, y: Float, width: Float, height: Float) {
        val atlasBounds = glyph.atlasBounds ?: return
        val atlas = font.fontInfo.atlas
        val textureTop = 1.0 - (atlasBounds.top / atlas.height)
        val textureBottom = 1.0 - (atlasBounds.bottom / atlas.height)
        val textureLeft = (atlasBounds.left / atlas.width).toDouble()
        val textureRight = (atlasBounds.right / atlas.width).toDouble()

        fgColorUniform.setValue(Vector4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f))
//         glyphSizeUniform.setValue(Vector2f(
// //            width, height
//             atlasBounds.right - atlasBounds.left,
//             atlasBounds.top - atlasBounds.bottom
// //         64f, 64f
//         ))
//         distanceFactorUniform.setValue()
        val atlasWidth = (atlasBounds.right - atlasBounds.left)
        val distanceFactor = font.fontInfo.atlas.distanceRange * (width / atlasWidth)
        distanceFactorUniform.setValue((distanceFactor * 1.2f).coerceAtLeast(5f))

        GlStateManager.enableBlend()
        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val doubleX = x.toDouble()
        val doubleY = y.toDouble()
        worldRenderer.pos(doubleX, doubleY + height, 0.0).tex(textureLeft, textureBottom).endVertex()
        worldRenderer.pos(doubleX + width, doubleY + height, 0.0).tex(textureRight, textureBottom).endVertex()
        worldRenderer.pos(doubleX + width, doubleY, 0.0).tex(textureRight, textureTop).endVertex()
        worldRenderer.pos(doubleX, doubleY, 0.0).tex(textureLeft, textureTop).endVertex()
        UGraphics.draw()
    }

    companion object {
        private lateinit var shader: Shader
        private lateinit var samplerUniform: IntUniform
        private lateinit var distanceFactorUniform: FloatUniform
//         private lateinit var pxRangeUniform: FloatUniform
//         private lateinit var widthMultiplierUniform: FloatUniform
//         private lateinit var glyphSizeUniform: Vec2Uniform
        private lateinit var fgColorUniform: Vec4Uniform

        fun areShadersInitialized() = ::shader.isInitialized

        fun initShaders() {
            if (areShadersInitialized())
                return

            shader = Shader("font", "font")
            samplerUniform = IntUniform(shader.getUniformLocation("msdf"))
            // pxRangeUniform = FloatUniform(shader.getUniformLocation("pxRange"))
            // widthMultiplierUniform = FloatUniform(shader.getUniformLocation("widthMultiplier"))
            // glyphSizeUniform = Vec2Uniform(shader.getUniformLocation("glyphSize"))
            distanceFactorUniform = FloatUniform(shader.getUniformLocation("distanceFactor"))
            fgColorUniform = Vec4Uniform(shader.getUniformLocation("fgColor"))
        }
    }
}
