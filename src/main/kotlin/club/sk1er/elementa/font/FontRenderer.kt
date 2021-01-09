package club.sk1er.elementa.font

import club.sk1er.elementa.font.data.Font
import club.sk1er.elementa.font.data.Glyph
import club.sk1er.elementa.shaders.*
import club.sk1er.elementa.utils.Vector2f
import club.sk1er.elementa.utils.Vector4f
import club.sk1er.mods.core.universal.UGraphics
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import java.awt.Color

class FontRenderer(private val font: Font) {
    fun drawString(string: String, color: Color, x: Float, y: Float, pointSize: Float) {
        if (!areShadersInitialized())
            return

        val fontTexture = font.getTexture()

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager.bindTexture(fontTexture.glTextureId)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)

        shader.bindIfUsable()
        samplerUniform.setValue(0)
        pxRangeUniform.setValue(font.fontInfo.atlas.distanceRange)

        var currentX = x

        string.forEach { char ->
            val glyph = font.fontInfo.glyphs[char.toInt()] ?: return@forEach
            val planeBounds = glyph.planeBounds ?: return@forEach

            drawGlyph(
                glyph,
                color,
                currentX,
                y,
                (planeBounds.right - planeBounds.left) * pointSize,
                (planeBounds.top - planeBounds.bottom) * pointSize
            )

            currentX += (glyph.advance * pointSize)
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
        glyphSizeUniform.setValue(
            Vector2f(
                atlasBounds.right - atlasBounds.left,
                atlasBounds.top - atlasBounds.bottom
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
    }

    companion object {
        private lateinit var shader: Shader
        private lateinit var samplerUniform: IntUniform
        private lateinit var pxRangeUniform: FloatUniform
        private lateinit var fgColorUniform: Vec4Uniform
        private lateinit var glyphSizeUniform: Vec2Uniform

        fun areShadersInitialized() = ::shader.isInitialized

        fun initShaders() {
            if (areShadersInitialized())
                return

            shader = Shader("font", "font")
            samplerUniform = IntUniform(shader.getUniformLocation("msdf"))
            pxRangeUniform = FloatUniform(shader.getUniformLocation("pxRange"))
            fgColorUniform = Vec4Uniform(shader.getUniformLocation("fgColor"))
            glyphSizeUniform = Vec2Uniform(shader.getUniformLocation("glyphSize"))
        }
    }
}
