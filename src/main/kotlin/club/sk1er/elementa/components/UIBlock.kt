package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

class UIBlock(private var color: Color) : UIComponent() {
    fun getColor() = color
    fun setColor(color: Color) = apply {
        this.color = color
    }

    override fun draw() {
        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()

        GL11.glPushMatrix()

        val pos = mutableListOf(x, y, x + width, y + height)
        if (pos[0] > pos[2])
            Collections.swap(pos, 0, 2)
        if (pos[1] > pos[3])
            Collections.swap(pos, 1, 3)

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f, color.blue.toFloat() / 255f, color.alpha.toFloat() / 255f)

        worldRenderer.begin(7, DefaultVertexFormats.POSITION)
        worldRenderer.pos(pos[0], pos[3], 0.0).endVertex()
        worldRenderer.pos(pos[2], pos[3], 0.0).endVertex()
        worldRenderer.pos(pos[2], pos[1], 0.0).endVertex()
        worldRenderer.pos(pos[0], pos[1], 0.0).endVertex()
        tessellator.draw()

        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        GL11.glPopMatrix()

        super.draw()
    }

    fun copy() = UIBlock(color).getConstraints()
}