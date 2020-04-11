package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.svg.SVG
import club.sk1er.elementa.svg.SVGCircle
import club.sk1er.elementa.svg.SVGLine
import club.sk1er.elementa.svg.SVGParser
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SVGComponent(private val svg: SVG) : UIComponent() {
    private var vboID = -1
    private lateinit var vboData: List<VBOData>

    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (color.alpha == 0) {
            return super.draw()
        }

        if (!::vboData.isInitialized) {
            generateVBOData()
        }

        val xScale = width / svg.width
        val yScale = height / svg.height
        val strokeWidth = xScale * svg.strokeWidth

        UniversalGraphicsHandler.pushMatrix()

        UniversalGraphicsHandler.enableBlend()
        UniversalGraphicsHandler.disableTexture2D()

        GlStateManager.color(color.red / 255f, color.green / 255f, color.blue / 255f)
        UniversalGraphicsHandler.translate(x, y, 0.0)
        UniversalGraphicsHandler.scale(xScale, yScale, 0.0)

        GL11.glLineWidth(strokeWidth.toFloat())
        GL11.glEnable(GL11.GL_LINE_SMOOTH)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0)

        vboData.forEach { (drawType, startIndex, vertexCount) ->
            GL11.glDrawArrays(drawType, startIndex, vertexCount)
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

        UniversalGraphicsHandler.popMatrix()

        super.draw()
    }

    private fun generateVBOData() {
        val dupe = duplicateMap[svg]
        if (dupe != null) {
            vboData = dupe
            return
        }

        vboID = GL15.glGenBuffers()

        val totalVertexCount = svg.elements.sumBy { it.getVertexCount() }
        val vertexBuffer = BufferUtils.createFloatBuffer(totalVertexCount * 2)

        var currPos = 0
        vboData = svg.elements.map { el ->
            val vertexCount = el.getVertexCount()

            VBOData(el.createBuffer(vertexBuffer), currPos, vertexCount)
                .also { currPos += vertexCount }
        }

        vertexBuffer.rewind()

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    companion object {
        fun ofResource(resourcePath: String): SVGComponent {
            return SVGComponent(SVGParser.parseFromResource(resourcePath))
        }

        private data class VBOData(val drawType: Int, val startIndex: Int, val count: Int)

        private val duplicateMap = mutableMapOf<SVG, List<VBOData>>()
    }
}