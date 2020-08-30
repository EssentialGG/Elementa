package club.sk1er.elementa.font

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.StringUtils
import org.lwjgl.opengl.GL11
import org.newdawn.slick.UnicodeFont
import org.newdawn.slick.font.effects.ColorEffect
import java.awt.Color
import java.awt.Font
import java.util.*
import java.util.regex.Pattern

class FontRenderer private constructor(private val fontSize: Float) {
    private val cachedStringWidth: MutableMap<String, Float> = HashMap()
    private var unicodeFont: UnicodeFont? = null
    private var prevScaleFactor = ScaledResolution(Minecraft.getMinecraft()).scaleFactor

    constructor(font: Font, fontSize: Float) : this(fontSize) {
        setUnicodeFont(font)
    }

    constructor(unicodeFont: UnicodeFont, fontSize: Float) : this(fontSize) {
        setUnicodeFont(unicodeFont)
    }

    constructor(supportedFont: SupportedFont, fontSize: Float) : this(supportedFont.resourcePath, fontSize)

    constructor(fontResourcePath: String, fontSize: Float) : this(fontSize) {
        try {
            setUnicodeFont(Font.createFont(Font.TRUETYPE_FONT, this.javaClass.getResourceAsStream(fontResourcePath)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUnicodeFont(font: Font) {
        setUnicodeFont(UnicodeFont(font.deriveFont(fontSize * prevScaleFactor / 2)))
    }

    private fun setUnicodeFont(unicodeFont: UnicodeFont?) {
        this.unicodeFont = unicodeFont?.apply {
            addAsciiGlyphs()
            effects.add(ColorEffect(Color.WHITE))
            loadGlyphs()
        }
    }

    fun drawStringScaled(text: String, givenX: Int, givenY: Int, color: Int, givenScale: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(givenX.toDouble(), givenY.toDouble(), 0.0)
        GlStateManager.scale(givenScale, givenScale, givenScale)
        drawString(text, 0f, 0f, color)
        GlStateManager.popMatrix()
    }

    fun drawString(text: String, xPos: Float, yPos: Float, color: Int, shadow: Boolean = true) {
        var x = xPos
        var y = yPos

        if (shadow)
            drawString(StringUtils.stripControlCodes(text), x + 0.5f, y + 0.5f, 0x000000, false)

        val resolution = ScaledResolution(Minecraft.getMinecraft())
        try {
            if (resolution.scaleFactor != prevScaleFactor) {
                prevScaleFactor = resolution.scaleFactor
                unicodeFont?.font?.deriveFont(fontSize * prevScaleFactor / 2)?.let(::setUnicodeFont)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (unicodeFont == null)
            return

        GlStateManager.pushMatrix()
        GlStateManager.scale(1f / prevScaleFactor, 1f / prevScaleFactor, 1f / prevScaleFactor)

        x *= prevScaleFactor
        y *= prevScaleFactor
        val originalX = x

        val red = (color shr 16 and 255).toFloat() / 255.0f
        val green = (color shr 8 and 255).toFloat() / 255.0f
        val blue = (color and 255).toFloat() / 255.0f
        val alpha = (color shr 24 and 255).toFloat() / 255.0f
        GlStateManager.color(red, green, blue, alpha)

        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val parts = COLOR_CODE_PATTERN.split(text)
        var index = 0
        var currentColor = color
        val chars = text.toCharArray()

        parts.forEach { part ->
            part.split("\n".toRegex()).forEach { newlinePart ->
                newlinePart.split("\r".toRegex()).forEach { carriagePart ->
                    unicodeFont!!.drawString(x, y, carriagePart, org.newdawn.slick.Color(currentColor))
                    x += unicodeFont!!.getWidth(carriagePart).toFloat()

                    index += carriagePart.length
                    if (index < chars.size && chars[index] == '\r') {
                        x = originalX
                        index++
                    }
                }

                if (index < chars.size && chars[index] == '\n') {
                    x = originalX
                    y += getHeight(newlinePart) * 2
                    index++
                }
            }

            if (index + 1 < chars.size && chars[index] == '§') {
                val colorChar = chars[index + 1]
                val codeIndex = "0123456789abcdef".indexOf(colorChar)
                currentColor = when {
                    codeIndex != -1 -> colorCodes[codeIndex]
                    colorChar != 'r' -> color
                    else -> currentColor
                }
                index += 2
            }
        }

        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.bindTexture(0)
        GlStateManager.popMatrix()
    }

    fun drawCenteredString(text: String, x: Float, y: Float, color: Int) {
        drawString(text, x - getWidth(text).toInt() / 2, y, color)
    }

    fun drawCenteredTextScaled(text: String, givenX: Int, givenY: Int, color: Int, givenScale: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(givenX.toDouble(), givenY.toDouble(), 0.0)
        GlStateManager.scale(givenScale, givenScale, givenScale)
        drawCenteredString(text, 0f, 0f, color)
        GlStateManager.popMatrix()
    }

    fun drawCenteredStringWithShadow(text: String, x: Float, y: Float, color: Int) {
        drawCenteredString(StringUtils.stripControlCodes(text), x + 0.5f, y + 0.5f, color)
        drawCenteredString(text, x, y, color)
    }

    fun getWidth(text: String): Float {
        if (cachedStringWidth.size > 1000)
            cachedStringWidth.clear()
        return cachedStringWidth.computeIfAbsent(text) {
            val stripped = Pattern.compile("(?i)§[0-9A-FK-OR]").matcher(text).replaceAll("")
            unicodeFont!!.getWidth(stripped) / prevScaleFactor.toFloat()
        }
    }

    fun getCharWidth(c: Char): Float {
        return unicodeFont!!.getWidth(c.toString()).toFloat()
    }

    fun getHeight(s: String?): Float {
        return unicodeFont!!.getHeight(s) / 2.0f
    }


    enum class SupportedFont(val resourcePath: String) {
        Menlo("/fonts/Menlo-Regular.ttf")
    }

    companion object {
        private val COLOR_CODE_PATTERN = Pattern.compile("§[0123456789abcdefklmnor]")

        private val colorCodes = listOf(
            0x000000,
            0x0000AA,
            0x00AA00,
            0x00AAAA,
            0xAA0000,
            0xAA00AA,
            0xFFAA00,
            0xAAAAAA,
            0x555555,
            0x5555FF,
            0x55FF55,
            0x55FFFF,
            0xFF5555,
            0xFF55FF,
            0xFFFF55,
            0xFFFFFF
        )
    }
}