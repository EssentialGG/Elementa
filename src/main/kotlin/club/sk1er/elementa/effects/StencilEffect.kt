package club.sk1er.elementa.effects

import club.sk1er.elementa.UIComponent
import org.lwjgl.opengl.GL11.*

class StencilEffect @JvmOverloads constructor(private val customBoundingBox: UIComponent? = null) : Effect {
    override fun beforeDraw(component: UIComponent) {
        glEnable(GL_STENCIL_TEST)

        glColorMask ( false, false, false, false)
        glStencilFunc(GL_ALWAYS, 2, 0xffffffff.toInt())
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE)
    }

    override fun beforeChildrenDraw(component: UIComponent) {
        glColorMask (true, true, true, true)
        glStencilFunc(GL_EQUAL, 2, 0xffffffff.toInt())
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP)
    }

    override fun afterDraw(component: UIComponent) {
        glDisable(GL_STENCIL_TEST)
    }
}