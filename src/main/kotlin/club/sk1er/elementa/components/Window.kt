package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

object Window : UIComponent() {
    const val ANIMATION_FPS = 60

    private var systemTime = -1L

    init {
        super.parent = this
    }

    override fun draw() {
        if (systemTime == -1L) systemTime = System.currentTimeMillis()

        while (this.systemTime < System.currentTimeMillis() + 1000 / ANIMATION_FPS) {
            animationFrame()

            this.systemTime += 1000 / ANIMATION_FPS;
        }

        super.draw()
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