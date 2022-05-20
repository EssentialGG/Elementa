package gg.essential.elementa

import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.font.FontProvider
import gg.essential.elementa.utils.roundToRealPixels
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import java.awt.Color

class VanillaFontRenderer : FontProvider {
    override var cachedValue: FontProvider = this
    override var recalculate: Boolean = false
    override var constrainTo: UIComponent? = null

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
    }

    override fun getStringWidth(string: String, pointSize: Float): Float =
        UGraphics.getStringWidth(string).toFloat()

    override fun getStringHeight(string: String, pointSize: Float): Float =
        UGraphics.getFontHeight().toFloat()

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
        val scaledX = x.roundToRealPixels() / scale
        val scaledY = y.roundToRealPixels() / scale

        matrixStack.scale(scale, scale, 1f)
        if (shadowColor == null) {
            UGraphics.drawString(matrixStack, string, scaledX, scaledY, color.rgb, shadow)
        } else {
            UGraphics.drawString(matrixStack, string, scaledX, scaledY, color.rgb, shadowColor.rgb)
        }
        matrixStack.scale(1 / scale, 1 / scale, 1f)
    }

    override fun getBaseLineHeight(): Float {
        return BASE_CHAR_HEIGHT
    }

    override fun getShadowHeight(): Float {
        return SHADOW_HEIGHT;
    }

    override fun getBelowLineHeight(): Float {
        return BELOW_LINE_HEIGHT;
    }

    companion object {
        /** Most (English) capital letters have this height, so this is what we use to center "the line". */
        internal const val BASE_CHAR_HEIGHT = 7f

        /**
         * Some letters have a few extra pixels below the visually centered line (gjpqy).
         * To accommodate these, we need to add extra height at the bottom and the top (to keep the original line
         * centered). This needs special consideration because the font renderer does not consider it, so we need to
         * adjust the position we give to it accordingly.
         * Additionally, adding the space on top make top-alignment difficult, whereas not adding it makes centering
         * difficult, so we use a simple heuristic to determine which one it is we're most likely looking for and then
         * either add just the bottom one or the top one as well.
         */
        internal const val BELOW_LINE_HEIGHT = 1f

        /** Extra height if shadows are enabled. */
        const val SHADOW_HEIGHT = 1f
    }
}