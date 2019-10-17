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
        val x = this.getLeft()
        val y = this.getTop()
        val width = this.getWidth() / textWidth
        val height = this.getHeight() / 9f
        val color = this.getColor()

        GlStateManager.pushMatrix()

        GlStateManager.color(color.red.toFloat() / 255f, color.green.toFloat() / 255f, color.blue.toFloat() / 255f, color.alpha.toFloat() / 255f)

        GlStateManager.scale(width.toDouble(), height.toDouble(), 1.0)
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x / width, y / height, 0xffffff, shadow)

        GlStateManager.popMatrix()

        super.draw()
    }
}