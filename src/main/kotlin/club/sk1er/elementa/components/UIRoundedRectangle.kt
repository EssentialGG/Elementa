package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ConstantColorConstraint
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

open class UIRoundedRectangle(radius: Float) : UIComponent() {
    val r = radius.toDouble()
    init {
        this.setColor(ConstantColorConstraint(Color(0, 0, 0, 0)))
    }

    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val x2 = this.getRight().toDouble()
        val y2 = this.getBottom().toDouble()

        val color = this.getColor()
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
        for (i in 0 .. 9) {
            worldRenderer.pos(cos(theta) * r + x2 - r,  -sin(theta) * r + y + r, 0.0).endVertex()
            theta += (PI / 2) / 10
        }

        worldRenderer.pos(x2 - r, y, 0.0).endVertex()
        worldRenderer.pos(x + r, y, 0.0).endVertex()

        for (i in 0 .. 9) {
            worldRenderer.pos(cos(theta) * r + x + r,  -sin(theta) * r + y + r, 0.0).endVertex()
            theta += (PI / 2) / 10
        }

        worldRenderer.pos(x, y + r, 0.0).endVertex()
        worldRenderer.pos(x, y2 - r, 0.0).endVertex()

        for (i in 0 .. 9) {
            worldRenderer.pos(cos(theta) * r + x + r,  -sin(theta) * r + y2 - r, 0.0).endVertex()
            theta += (PI / 2) / 10
        }

        worldRenderer.pos(x + r, y2, 0.0).endVertex()
        worldRenderer.pos(x2 - r, y2, 0.0).endVertex()

        for (i in 0 .. 9) {
            worldRenderer.pos(cos(theta) * r + x2 - r,  -sin(theta) * r + y2 - r, 0.0).endVertex()
            theta += (PI / 2) / 10
        }

        worldRenderer.pos(x2, y2 - r, 0.0).endVertex()
        worldRenderer.pos(x2, y + r, 0.0).endVertex()



        tessellator.draw()

        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        GL11.glPopMatrix()

        super.draw()
    }
}