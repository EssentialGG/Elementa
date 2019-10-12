package club.sk1er.elementa.features

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.Window
import org.lwjgl.opengl.GL11.*
import kotlin.math.roundToInt

class ScissorFeature : Feature {
    override fun beforeDraw(component: UIComponent) {
        val res = Window.of(component).scaledResolution

        glEnable(GL_SCISSOR_TEST)
        glScissor(
            component.getLeft().roundToInt() * res.scaleFactor,
            component.getTop().roundToInt() * res.scaleFactor,
            component.getWidth().roundToInt() * res.scaleFactor,
            component.getHeight().roundToInt() * res.scaleFactor
        )
    }

    override fun afterDraw(component: UIComponent) {
        glDisable(GL_SCISSOR_TEST)
    }
}