package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ConstantColorConstraint
import com.sun.javafx.geom.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

open class UIShape : UIComponent() {
    private var vertexes = mutableListOf<Vec2d>()

    init {
        this.setColor(ConstantColorConstraint(Color(0, 0, 0, 0)))
    }

    fun addVertex(vertex: Vec2d) {
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
            worldRenderer.pos(it.x, it.y, 0.0).endVertex()
        }
        tessellator.draw()

        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        GL11.glPopMatrix()

        super.draw()
    }
}