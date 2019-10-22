package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ConstantColorConstraint
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

// feeling cute, might delete this class later
open class UIShape : UIComponent() {
    private var vertexes = mutableListOf<List<Double>>()

    init {
        this.setColor(ConstantColorConstraint(Color(0, 0, 0, 0)))
    }

    fun addVertex(vertex: List<Double>) {
        vertexes.add(vertex)
    }

    override fun draw() {
        beforeDraw()

        val color = this.getColor()
        if (color.alpha == 0) return super.draw()

        GL11.glPushMatrix()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f, color.blue.toFloat() / 255f, color.alpha.toFloat() / 255f)

        worldRenderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION)
        vertexes.forEach {
            worldRenderer.pos(it[0], it[1], 0.0).endVertex()
        }
        tessellator.draw()

        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        GL11.glPopMatrix()

        super.draw()
    }
}