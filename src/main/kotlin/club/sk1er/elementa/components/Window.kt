package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

object Window : UIComponent() {
    const val ANIMATION_FPS = 244

    private var systemTime = -1L
    var scaledResolution = ScaledResolution(Minecraft.getMinecraft())

    init {
        super.parent = this
    }

    override fun draw() {
        scaledResolution = ScaledResolution(Minecraft.getMinecraft())

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
        return scaledResolution.scaledWidth.toFloat()
    }

    override fun getHeight(): Float {
        return scaledResolution.scaledHeight.toFloat()
    }

    override fun getRight() = getWidth()
    override fun getBottom() = getHeight()
}