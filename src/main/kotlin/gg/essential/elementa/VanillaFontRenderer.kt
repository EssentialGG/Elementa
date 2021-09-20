package gg.essential.elementa

import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.font.FontProvider
import gg.essential.elementa.utils.roundToRealPixels
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft.getFontRenderer
import java.awt.Color

class VanillaFontRenderer : FontProvider {
    override var cachedValue: FontProvider = this
    override var recalculate: Boolean = false
    override var constrainTo: UIComponent? = null

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
    }

    override fun getStringWidth(string: String, pointSize: Float): Float =
        getFontRenderer().getStringWidth(string).toFloat()
    
    override fun getStringHeight(string: String, pointSize: Float): Float =
        getFontRenderer().FONT_HEIGHT.toFloat()

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
}