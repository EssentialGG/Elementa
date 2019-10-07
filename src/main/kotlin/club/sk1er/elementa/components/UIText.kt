package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.constraints.PixelConstraint
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class UIText @JvmOverloads constructor(private val text: String, private val color: Int = 0xffffffff.toInt(), private val shadow: Boolean = true) : UIComponent() {

    private val textWidth: Float = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text).toFloat()

    init {
        super.getConstraints().setWidth(PixelConstraint(textWidth))
        super.getConstraints().setHeight(PixelConstraint(9f))
    }

    override fun draw() {
        val x = this.getLeft()
        val y = this.getTop()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()

        GlStateManager.scale(width / textWidth, height / 9.0, 1.0)
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color, shadow)

        super.draw()
    }
}