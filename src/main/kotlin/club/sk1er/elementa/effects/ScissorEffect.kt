package club.sk1er.elementa.effects

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.Window
import org.lwjgl.opengl.GL11.*

/**
 * Enables GL Scissoring to restrict all drawing done by
 * the component where this is enabled to be only inside of that component's bounds.
 *
 * By proxy, this restricts all of said component's children drawing to inside of the same bounds.
 */
class ScissorEffect : Effect {
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