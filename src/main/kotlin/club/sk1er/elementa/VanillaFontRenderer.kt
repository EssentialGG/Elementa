package club.sk1er.elementa

import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.elementa.font.FontProvider
import club.sk1er.mods.core.universal.UGraphics
import club.sk1er.mods.core.universal.UMinecraft.getFontRenderer
import java.awt.Color

class VanillaFontRenderer : FontProvider {
    override var cachedValue: FontProvider = this
    override var recalculate: Boolean = false
    override var constrainTo: UIComponent? = null

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {

    }

    override fun getStringWidth(string: String, pointSize: Float): Float {
        return getFontRenderer().getStringWidth(string).toFloat()
    }

    override fun drawString(
        string: String,
        color: Color,
        x: Float,
        y: Float,
        originalPointSize: Float,
        scale: Float,
        shadow: Boolean,
        shadowColor: Color?
    ) {
        UGraphics.scale(scale, scale, 1f)
        if (shadowColor == null) {
            UGraphics.drawString(string, x / scale, y / scale, color.rgb, shadow)
        } else {
            UGraphics.drawString(string, x / scale, y / scale, color.rgb, shadowColor.rgb)
        }
        UGraphics.scale(1 / scale, 1 / scale, 1f)

    }
}