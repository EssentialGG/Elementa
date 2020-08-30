package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a rounded rectangle
 */
open class UIRoundedRectangle @JvmOverloads constructor(radius: Float, var steps: Int = 10) : UIComponent() {
    init {
        setRadius(radius.pixels())
    }

    override fun draw() {
        beforeDraw()

        val x1 = getLeft().toDouble()
        val y1 = getTop().toDouble()
        val x2 = getRight().toDouble()
        val y2 = getBottom().toDouble()
        val radius = getRadius()

        val color = getColor()
        if (color.alpha != 0)
            drawRoundedRectangle(x1, y1, x2, y2, radius, steps, color)

        super.draw()
    }

    companion object {
        fun drawRoundedRectangle(x1: Double, y1: Double, x2: Double, y2: Double, radius: Float, steps: Int, color: Color) {
            UniversalGraphicsHandler.pushMatrix()

            UniversalGraphicsHandler.enableBlend()
            UniversalGraphicsHandler.disableTexture2D()

            val worldRenderer = UniversalGraphicsHandler.getFromTessellator()

            UniversalGraphicsHandler.tryBlendFuncSeparate(770, 771, 1, 0)

            val red = color.red.toFloat() / 255f
            val green = color.green.toFloat() / 255f
            val blue = color.blue.toFloat() / 255f
            val alpha = color.alpha.toFloat() / 255f

            worldRenderer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR)

            var theta = 0.0
            for (i in 0 until steps) {
                worldRenderer.pos(cos(theta) * radius + x2 - radius,  -sin(theta) * radius + y1 + radius, 0.0).color(red, green, blue, alpha).endVertex()
                theta += (PI / 2) / steps
            }

            worldRenderer.pos(x2 - radius, y1, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x1 + radius, y1, 0.0).color(red, green, blue, alpha).endVertex()

            for (i in 0 until steps) {
                worldRenderer.pos(cos(theta) * radius + x1 + radius,  -sin(theta) * radius + y1 + radius, 0.0).color(red, green, blue, alpha).endVertex()
                theta += (PI / 2) / steps
            }

            worldRenderer.pos(x1, y1 + radius, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x1, y2 - radius, 0.0).color(red, green, blue, alpha).endVertex()

            for (i in 0 until steps) {
                worldRenderer.pos(cos(theta) * radius + x1 + radius,  -sin(theta) * radius + y2 - radius, 0.0).color(red, green, blue, alpha).endVertex()
                theta += (PI / 2) / steps
            }

            worldRenderer.pos(x1 + radius, y2, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x2 - radius, y2, 0.0).color(red, green, blue, alpha).endVertex()

            for (i in 0 until steps) {
                worldRenderer.pos(cos(theta) * radius + x2 - radius,  -sin(theta) * radius + y2 - radius, 0.0).color(red, green, blue, alpha).endVertex()
                theta += (PI / 2) / steps
            }

            worldRenderer.pos(x2, y2 - radius, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(x2, y1 + radius, 0.0).color(red, green, blue, alpha).endVertex()

            UniversalGraphicsHandler.draw()

            UniversalGraphicsHandler.enableTexture2D()
            UniversalGraphicsHandler.disableBlend()

            UniversalGraphicsHandler.popMatrix()
        }
    }
}