package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.PixelConstraint
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class UIText @JvmOverloads constructor(private val text: String, private val shadow: Boolean = true) : UIComponent() {

    private val textWidth: Float = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()

    init {
        super.getConstraints().setWidth(PixelConstraint(textWidth))
        super.getConstraints().setHeight(PixelConstraint(9f))
    }

    override fun draw() {
        beforeDraw()

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()

        GlStateManager.pushMatrix()

        GlStateManager.translate(x, y, 0.0)
        GlStateManager.scale(width / textWidth, height / 9.0, 1.0)
        Minecraft.getMinecraft().fontRendererObj.drawString(text, 0f, 0f, this.getColor().rgb, shadow)

        GlStateManager.popMatrix()

        super.draw()
    }
}