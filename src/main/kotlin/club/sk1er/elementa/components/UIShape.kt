package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.mods.core.universal.UGraphics
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

// feeling cute, might delete this class later
open class UIShape @JvmOverloads constructor(color: Color = Color.WHITE) : UIComponent() {
    private var vertices = mutableListOf<UIPoint>()
    var drawMode = GL11.GL_POLYGON

    init {
        setColor(color.asConstraint())
    }

    fun addVertex(point: UIPoint) = apply {
        this.parent.addChild(point)
        vertices.add(point)
    }

    fun addVertices(vararg points: UIPoint) = apply {
        parent.addChildren(*points)
        vertices.addAll(points)
    }

    fun getVertices() = vertices

    override fun draw() {
        beforeDraw()

        val color = this.getColor()
        if (color.alpha == 0) return super.draw()

        UGraphics.pushMatrix()
        UGraphics.enableBlend()
        UGraphics.disableTexture2D()
        val red = color.red.toFloat() / 255f
        val green = color.green.toFloat() / 255f
        val blue = color.blue.toFloat() / 255f
        val alpha = color.alpha.toFloat() / 255f

        val worldRenderer = UGraphics.getFromTessellator()
        UGraphics.tryBlendFuncSeparate(770, 771, 1, 0)

        worldRenderer.begin(drawMode, DefaultVertexFormats.POSITION_COLOR)
        vertices.forEach {
            worldRenderer
                .pos(it.absoluteX.toDouble(), it.absoluteY.toDouble(), 0.0)
                .color(red, green, blue, alpha)
                .endVertex()
        }
        UGraphics.draw()

        UGraphics.enableTexture2D()
        UGraphics.disableBlend()
        UGraphics.popMatrix()

        super.draw()
    }
}
