package gg.essential.elementa.font

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.font.data.Font
import gg.essential.elementa.font.data.Glyph
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import java.awt.Color
import kotlin.math.max

class BasicFontRenderer(
    private val regularFont: Font
) : FontProvider {

    /* Required by Elementa but unused for this type of constraint */
    override var cachedValue: FontProvider = this
    override var recalculate: Boolean = false
    override var constrainTo: UIComponent? = null


    override fun getStringWidth(string: String, pointSize: Float): Float {
        return getStringDimensions(string, pointSize).first
    }

    override fun getStringHeight(string: String, pointSize: Float): Float {
        return getStringDimensions(string, pointSize).second
    }

    private fun getStringDimensions(string: String, pointSize: Float): Pair<Float, Float> {
        var width = 0f
        var height = 0f

        /*
            10 point font is the default used in Elementa.
            Adjust the point size based on this font's size.
         */
        val currentPointSize = pointSize / 10 * regularFont.fontInfo.atlas.size

        var i = 0
        while (i < string.length) {
            val char = string[i]

            //Ignore formatting codes for purpose of calculating string dimensions
            if (char == '\u00a7' && i + 1 < string.length) {
                //not handled by this font renderer
                i += 2
                continue
            }

            val glyph = regularFont.fontInfo.glyphs[char.code]
            if (glyph == null) {
                i++
                continue
            }
            val planeBounds = glyph.planeBounds

            if (planeBounds != null) {
                height = max((planeBounds.top - planeBounds.bottom) * currentPointSize, height)
            }

            width += (glyph.advance * currentPointSize)
            i++
        }
        return Pair(width, height)
    }

    fun getLineHeight(pointSize: Float): Float {
        return regularFont.fontInfo.metrics.lineHeight * pointSize
    }

    override fun drawString(
        matrixStack: UMatrixStack,
        string: String,
        color: Color,
        x: Float,
        y: Float,
        originalPointSize: Float,
        scale: Float,
        shadow: Boolean,
        shadowColor: Color?
    ) {
        /*
            10 point font is the default used in Elementa.
            Adjust the point size based on this font's size.
         */
        val scaledPointSize = originalPointSize / 10 * regularFont.fontInfo.atlas.size

        /*
            Moved one pixel up so that the main body of the text is in
            the top left of the component. This change keeps text location
            in the same location as the vanilla font renderer relative to
            a UIText component.
         */
        drawStringNow(
            matrixStack,
            string,
            color,
            x,
            y - 1,
            scaledPointSize * scale
        )
    }

    override fun getBaseLineHeight(): Float {
        return regularFont.fontInfo.atlas.baseCharHeight
    }

    override fun getShadowHeight(): Float {
        return regularFont.fontInfo.atlas.shadowHeight
    }

    override fun getBelowLineHeight(): Float {
        return regularFont.fontInfo.atlas.belowLineHeight
    }

    private fun drawStringNow(
        matrixStack: UMatrixStack,
        string: String,
        color: Color,
        x: Float,
        y: Float,
        originalPointSize: Float
    ) {
        UGraphics.bindTexture(0, regularFont.getTexture().glTextureId)

        var currentX = x
        var i = 0
        while (i < string.length) {
            val char = string[i]

            // Ignore color code characters in this font renderer
            if (char == '\u00a7' && i + 1 < string.length) {
                i += 2
                continue
            }


            val glyph = regularFont.fontInfo.glyphs[char.code]
            if (glyph == null) {
                i++
                continue
            }

            val planeBounds = glyph.planeBounds

            if (planeBounds != null) {
                val width = (planeBounds.right - planeBounds.left) * originalPointSize
                val height = (planeBounds.top - planeBounds.bottom) * originalPointSize

                drawGlyph(
                    matrixStack,
                    glyph,
                    color,
                    currentX,
                    y,
                    width,
                    height
                )
            }

            currentX += (glyph.advance) * originalPointSize
            i++
        }

    }


    private fun drawGlyph(
        matrixStack: UMatrixStack,
        glyph: Glyph,
        color: Color,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        val atlasBounds = glyph.atlasBounds ?: return
        val atlas = regularFont.fontInfo.atlas
        val textureTop = 1.0 - ((atlasBounds.top) / atlas.height)
        val textureBottom = 1.0 - ((atlasBounds.bottom) / atlas.height)
        val textureLeft = (atlasBounds.left / atlas.width).toDouble()
        val textureRight = (atlasBounds.right / atlas.width).toDouble()

        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.beginWithDefaultShader(UGraphics.DrawMode.QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        val doubleX = x.toDouble()
        val doubleY = y.toDouble()
        worldRenderer.pos(matrixStack, doubleX, doubleY + height, 0.0).tex(textureLeft, textureBottom).color(
            color.red,
            color.green,
            color.blue,
            255
        ).endVertex()
        worldRenderer.pos(matrixStack, doubleX + width, doubleY + height, 0.0).tex(textureRight, textureBottom).color(
            color.red,
            color.green,
            color.blue,
            255
        ).endVertex()
        worldRenderer.pos(matrixStack, doubleX + width, doubleY, 0.0).tex(textureRight, textureTop).color(
            color.red,
            color.green,
            color.blue,
            255
        ).endVertex()
        worldRenderer.pos(matrixStack, doubleX, doubleY, 0.0).tex(textureLeft, textureTop).color(
            color.red,
            color.green,
            color.blue,
            255
        ).endVertex()
        worldRenderer.drawDirect()

    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {

    }
}