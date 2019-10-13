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

        val x = this.getLeft()
        val y = this.getTop()
        val width = this.getWidth() / textWidth
        val height = this.getHeight() / 9f

        GlStateManager.pushMatrix()

        GlStateManager.scale(width.toDouble(), height.toDouble(), 1.0)
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x / width, y / height, this.getColor().rgb, shadow)

        GlStateManager.popMatrix()

        super.draw()
    }
}