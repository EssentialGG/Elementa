package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ColorConstraint
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.mods.core.universal.UGraphics
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Extremely simple component that simply draws a colored rectangle.
 */
open class UIBlock(colorConstraint: ColorConstraint = Color.WHITE.asConstraint()) : UIComponent() {
    constructor(color: Color) : this(color.asConstraint())

    init {
        setColor(colorConstraint)
    }

    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val x2 = this.getRight().toDouble()
        val y2 = this.getBottom().toDouble()

        val color = getColor()
        if (color.alpha == 0) return super.draw()

        UGraphics.pushMatrix()

        drawBlock(color, x, y, x2, y2)

        UGraphics.popMatrix()

        super.draw()
    }

    companion object {
        fun drawBlock(color: Color, x1: Double, y1: Double, x2: Double, y2: Double) {
            UGraphics.enableBlend()
            UGraphics.disableTexture2D()
            UGraphics.tryBlendFuncSeparate(770, 771, 1, 0)

            val worldRenderer = UGraphics.getFromTessellator()

            val red = color.red.toFloat() / 255f
            val green = color.green.toFloat() / 255f
            val blue = color.blue.toFloat() / 255f
            val alpha = color.alpha.toFloat() / 255f

            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
            worldRenderer.pos(x1, y2, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x2, y2, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x2, y1, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x1, y1, 0.0).color(red, green, blue, alpha).endVertex()
            UGraphics.draw()


            UGraphics.enableTexture2D()
            UGraphics.disableBlend()
        }
    }
}
