package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

object Window : UIComponent() {
    init {
        super.parent = this
    }

    override fun getLeft(): Float {
        return 0f
    }

    override fun getTop(): Float {
        return 0f
    }

    override fun getWidth(): Float {
        return ScaledResolution(Minecraft.getMinecraft()).scaledWidth.toFloat()
    }

    override fun getHeight(): Float {
        return ScaledResolution(Minecraft.getMinecraft()).scaledHeight.toFloat()
    }

    override fun getRight() = getWidth()
    override fun getBottom() = getHeight()
}