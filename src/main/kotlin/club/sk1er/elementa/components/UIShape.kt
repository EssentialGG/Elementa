package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

// feeling cute, might delete this class later
open class UIShape @JvmOverloads constructor(color: Color = Color(0, 0, 0, 0)) : UIComponent() {
    private var vertexes = mutableListOf<UIPoint>()
    var drawMode = GL11.GL_POLYGON

    init {
        setColor(color.asConstraint())
    }

    fun addVertex(point: UIPoint) {
        this.parent.addChild(point)
        vertexes.add(point)
    }

    fun getVertices() = vertexes

    override fun draw() {
        beforeDraw()

        val color = this.getColor()
        if (color.alpha == 0) return super.draw()

        UniversalGraphicsHandler.pushMatrix()
        UniversalGraphicsHandler.enableBlend()
        UniversalGraphicsHandler.disableTexture2D()
        val red = color.red.toFloat() / 255f
        val green = color.green.toFloat() / 255f
        val blue = color.blue.toFloat() / 255f
        val alpha = color.alpha.toFloat() / 255f

        val worldRenderer = UniversalGraphicsHandler.getFromTessellator()
        UniversalGraphicsHandler.tryBlendFuncSeparate(770, 771, 1, 0)


        worldRenderer.begin(drawMode, DefaultVertexFormats.POSITION_COLOR)
        vertexes.forEach {
            worldRenderer.pos(it.getX().toDouble(), it.getY().toDouble(), 0.0).color(red, green, blue, alpha).endVertex()
        }
        UniversalGraphicsHandler.draw()


        UniversalGraphicsHandler.enableTexture2D()
        UniversalGraphicsHandler.disableBlend()
        UniversalGraphicsHandler.popMatrix()

        super.draw()
    }
}