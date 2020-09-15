package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.image.ImageProvider
import club.sk1er.elementa.svg.SVGParser
import club.sk1er.elementa.svg.data.SVG
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import java.awt.Color
import kotlin.math.sqrt

class SVGComponent(private var svg: SVG) : UIComponent(), ImageProvider {
    private var vboID = -1
    private lateinit var vboData: List<VBOData>
    private var needsReload = false

    fun setSVG(svg: SVG) {
        this.svg = svg
        needsReload = true
    }

    override fun drawImage(x: Double, y: Double, width: Double, height: Double, color: Color) {
        if (!::vboData.isInitialized || needsReload) {
            generateVBOData()
            needsReload = false
        }

        val xScale = svg.width?.let { width / it } ?: 1.0
        val yScale = svg.height?.let { height / it } ?: 1.0
        val strokeWidth = (xScale * svg.strokeWidth).toFloat().coerceAtMost(10f)

        UniversalGraphicsHandler.pushMatrix()

        UniversalGraphicsHandler.enableBlend()
        UniversalGraphicsHandler.disableTexture2D()

        GL11.glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, 1f)
        UniversalGraphicsHandler.translate(x, y, 0.0)
        UniversalGraphicsHandler.scale(xScale, yScale, 0.0)

        GL11.glPointSize(strokeWidth)
        GL11.glLineWidth(strokeWidth)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY)
        GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0)

        vboData.forEach { (drawType, startIndex, vertexCount, drawPoints) ->
            GL11.glDrawArrays(drawType, startIndex, vertexCount)

            if (drawPoints && svg.roundLineJoins) {
                GL11.glDrawArrays(GL11.GL_POINTS, startIndex, vertexCount)
            }
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_POINT_SMOOTH)
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

        UniversalGraphicsHandler.enableTexture2D()

        UniversalGraphicsHandler.popMatrix()
    }

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

        drawImage(x, y, width, height, color)

        super.draw()
    }

    private fun generateVBOData() {
        vboID = GL15.glGenBuffers()

        val totalVertexCount = svg.elements.sumBy { it.getVertexCount() }
        val vertexBuffer = BufferUtils.createFloatBuffer(totalVertexCount * 2)

        var currPos = 0
        vboData = svg.elements.map { el ->
            val vertexCount = el.getVertexCount()

            VBOData(el.createBuffer(vertexBuffer), currPos, vertexCount, el.drawSmoothPoints())
                .also { currPos += vertexCount }
        }

        vertexBuffer.rewind()

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    fun finalize() {
        GL15.glDeleteBuffers(vboID)
    }

    companion object {
        fun ofResource(resourcePath: String): SVGComponent {
            return SVGComponent(SVGParser.parseFromResource(resourcePath))
        }

        private data class VBOData(val drawType: Int, val startIndex: Int, val count: Int, val drawPoints: Boolean)
    }
}