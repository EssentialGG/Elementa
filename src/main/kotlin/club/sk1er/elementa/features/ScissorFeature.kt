package club.sk1er.elementa.features

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.Window
import org.lwjgl.opengl.GL11.*

class ScissorFeature : Feature {
    override fun beforeDraw(component: UIComponent) {
        val res = Window.of(component).scaledResolution

        glEnable(GL_SCISSOR_TEST)
        glScissor(
            component.getLeft().toInt() * res.scaleFactor,
            (res.scaledHeight * res.scaleFactor) - (component.getBottom().toInt() * res.scaleFactor),
            component.getWidth().toInt() * res.scaleFactor,
            component.getHeight().toInt() * res.scaleFactor
        )
    }

    override fun afterDraw(component: UIComponent) {
        glDisable(GL_SCISSOR_TEST)
    }
}