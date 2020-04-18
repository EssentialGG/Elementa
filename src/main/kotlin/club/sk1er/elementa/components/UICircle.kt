package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalMouse
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class UICircle @JvmOverloads constructor(radius: Float = 0f, color: Color = Color.WHITE, var steps: Int = 40) : UIComponent() {
    init {
        setColor(color.asConstraint())
        setRadius(radius.pixels())
    }

    override fun getLeft(): Float {
        return getConstraints().getX() - getRadius()
    }

    override fun getTop(): Float {
        return getConstraints().getY() - getRadius()
    }

    override fun getWidth(): Float {
        return getRadius() * 2
    }

    override fun getHeight(): Float {
        return getRadius() * 2
    }

    override fun draw() {
        beforeDraw()

        val x = getConstraints().getX().toDouble()
        val y = getConstraints().getY().toDouble()
        val r = getRadius().toDouble()

        val color = getColor()
        if (color.alpha == 0) return super.draw()


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
            worldRenderer.pos(cos(theta) * r + x, -sin(theta) * r + y, 0.0).color(red, green, blue, alpha).endVertex()
            theta += (2 * PI) / steps
        }

        UniversalGraphicsHandler.draw()


        UniversalGraphicsHandler.enableTexture2D()
        UniversalGraphicsHandler.disableBlend()

        UniversalGraphicsHandler.popMatrix()

        super.draw()
    }
}