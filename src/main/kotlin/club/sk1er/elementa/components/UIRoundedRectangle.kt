package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ConstantColorConstraint
import club.sk1er.elementa.dsl.pixels
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
        setColor(ConstantColorConstraint(Color(0, 0, 0, 0)))
        setRadius(radius.pixels())
    }

    override fun draw() {
        beforeDraw()

        val x = getLeft().toDouble()
        val y = getTop().toDouble()
        val x2 = getRight().toDouble()
        val y2 = getBottom().toDouble()
        val radius = getRadius()

        val color = getColor()
        if (color.alpha == 0) return super.draw()


        GL11.glPushMatrix()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f, color.blue.toFloat() / 255f, color.alpha.toFloat() / 255f)

        worldRenderer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION)

        var theta = 0.0
        for (i in 0 until steps) {
            worldRenderer.pos(cos(theta) * radius + x2 - radius,  -sin(theta) * radius + y + radius, 0.0).endVertex()
            theta += (PI / 2) / steps
        }

        worldRenderer.pos(x2 - radius, y, 0.0).endVertex()
        worldRenderer.pos(x + radius, y, 0.0).endVertex()

        for (i in 0 until steps) {
            worldRenderer.pos(cos(theta) * radius + x + radius,  -sin(theta) * radius + y + radius, 0.0).endVertex()
            theta += (PI / 2) / steps
        }

        worldRenderer.pos(x, y + radius, 0.0).endVertex()
        worldRenderer.pos(x, y2 - radius, 0.0).endVertex()

        for (i in 0 until steps) {
            worldRenderer.pos(cos(theta) * radius + x + radius,  -sin(theta) * radius + y2 - radius, 0.0).endVertex()
            theta += (PI / 2) / steps
        }

        worldRenderer.pos(x + radius, y2, 0.0).endVertex()
        worldRenderer.pos(x2 - radius, y2, 0.0).endVertex()

        for (i in 0 until steps) {
            worldRenderer.pos(cos(theta) * radius + x2 - radius,  -sin(theta) * radius + y2 - radius, 0.0).endVertex()
            theta += (PI / 2) / steps
        }

        worldRenderer.pos(x2, y2 - radius, 0.0).endVertex()
        worldRenderer.pos(x2, y + radius, 0.0).endVertex()

        tessellator.draw()

        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        GL11.glPopMatrix()

        super.draw()
    }
}