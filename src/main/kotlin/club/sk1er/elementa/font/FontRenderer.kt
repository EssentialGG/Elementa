package club.sk1er.elementa.font

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.font.data.Font
import club.sk1er.elementa.font.data.Glyph
import club.sk1er.elementa.shaders.*
import club.sk1er.elementa.utils.Vector2f
import club.sk1er.elementa.utils.Vector4f
import club.sk1er.mods.core.universal.UGraphics
import club.sk1er.mods.core.universal.UMinecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import java.awt.Color
import kotlin.math.ceil
import kotlin.math.floor

class FontRenderer(private val font: Font) {
    private var underline: Boolean = false
    private var strikethrough: Boolean = false
    private var bold: Boolean = false
    private var italics: Boolean = false
    private var obfuscated: Boolean = false
    private var textColor: Color? = null
    private var shadowColor: Color? = null

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

    @JvmOverloads
    fun drawString(string: String, color: Color, x: Float, y: Float, pointSize: Float, shadow: Boolean = true) {
        if (!areShadersInitialized())
            return

        val fontTexture = font.getTexture()

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        UGraphics.bindTexture(fontTexture.glTextureId)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        UGraphics.enableBlend()

        shader.bindIfUsable()
        samplerUniform.setValue(0)
        //shadowColorUniform.setValue(Vector4f(0.1f, 0.1f, 0.1f, 1f))
        sdfTexel.setValue(Vector2f(1f/font.fontInfo.atlas.width, 1f/font.fontInfo.atlas.height))

        hintAmountUniform.setValue(1f)
        subpixelAmountUniform.setValue(1f)

        val guiScale = UMinecraft.getMinecraft().gameSettings.guiScale

        val metrics = font.fontInfo.metrics
        val baseline = y + ((metrics.lineHeight + metrics.descender) * pointSize)

        doffsetUniform.setValue(3.5f / pointSize)

        val hintedBaseline = floor(baseline * guiScale) / guiScale

        var currentX = x
        var i = 0
        while (i < string.length) {
        //for (i in string.indices) {
        //string.forEach { char ->
            val char = string[i]

            // parse formatting codes
            if (char == '\u00a7' && i + 1 < string.length) {
                val j = "0123456789abcdefklmnor".indexOf(string[i + 1])
                when {
                    j < 16 -> {
                        obfuscated = false
                        bold = false
                        italics = false
                        strikethrough = false
                        underline = false
                        if (j < 0) {
                            textColor = colors[15]
                            shadowColor = colors[31]
                        } else {
                            textColor = colors[j]
                            shadowColor = colors[j + 16]
                        }
                    }
                    j == 16 -> obfuscated = true
                    j == 17 -> bold = true
                    j == 18 -> strikethrough = true
                    j == 19 -> underline = true
                    j == 20 -> italics = true
                    else -> {
                        obfuscated = false
                        bold = false
                        italics = false
                        strikethrough = false
                        underline = false
                        textColor = null
                        shadowColor = null
                    }
                }
                i += 2
                continue
            }

            val c = if (obfuscated && char != ' ') "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".random() else char

            val glyph = font.fontInfo.glyphs[c.toInt()]
            if (glyph == null) {
                i++
                continue
            }
            val planeBounds = glyph.planeBounds

            if (planeBounds != null) {
                val width = (planeBounds.right - planeBounds.left) * pointSize
                val height = (planeBounds.top - planeBounds.bottom) * pointSize

                val hintedHeight = ceil(height * guiScale) / guiScale
                val hintedY = hintedBaseline - ceil(planeBounds.top * pointSize * guiScale) / guiScale

                drawGlyph(
                    glyph,
                    color,
                    currentX,
                    hintedY,
                    width,
                    hintedHeight
                )
            }

            currentX += (glyph.advance * pointSize)
            i++
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

        val drawColor = textColor ?: color
        fgColorUniform.setValue(Vector4f(drawColor.red / 255f, drawColor.green / 255f, drawColor.blue / 255f, drawColor.alpha / 255f))

        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        val doubleX = x.toDouble()
        val doubleY = y.toDouble()
        worldRenderer.pos(doubleX, doubleY + height, 0.0).tex(textureLeft, textureBottom).endVertex()
        worldRenderer.pos(doubleX + width, doubleY + height, 0.0).tex(textureRight, textureBottom).endVertex()
        worldRenderer.pos(doubleX + width, doubleY, 0.0).tex(textureRight, textureTop).endVertex()
        worldRenderer.pos(doubleX, doubleY, 0.0).tex(textureLeft, textureTop).endVertex()
        UGraphics.draw()

        // TODO: all of these
        if (strikethrough) {
            TODO()
        }
        if (underline) {
            TODO()
        }
        if (bold) {
            TODO()
        }
        if (italics) {
            TODO()
        }
    }

    companion object {
        // trusting these are right... thank you minecraft wiki
        private val colors: Map<Int, Color> = mapOf(
            0 to Color.BLACK,
            1 to Color(0, 0, 170),
            2 to Color(0, 170, 0),
            3 to Color(0, 170, 170),
            4 to Color(170, 0 ,0),
            5 to Color(170, 0, 170),
            6 to Color(255, 170, 0),
            7 to Color(170, 170, 170),
            8 to Color(85, 85, 85),
            9 to Color(85, 85, 255),
            10 to Color(85, 255, 85),
            11 to Color(85, 255, 255),
            12 to Color(255, 85, 85),
            13 to Color(255, 85, 255),
            14 to Color(255, 255, 85),
            15 to Color(255, 255, 255),
            // shadows for (i - 16)
            16 to Color.BLACK,
            17 to Color(0, 0, 42),
            18 to Color(0, 42, 0),
            19 to Color(0, 42, 42),
            20 to Color(42, 0, 0),
            21 to Color(42, 0, 42),
            22 to Color(42, 42, 0),
            23 to Color(42, 42, 42),
            24 to Color(21, 21, 21),
            25 to Color(21, 21, 63),
            26 to Color(21, 63, 21),
            27 to Color(21, 63, 63),
            28 to Color(63, 21, 21),
            29 to Color(63, 21, 63),
            30 to Color(63, 63, 21),
            31 to Color(63, 63, 63)
        )

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
