package club.sk1er.elementa.effects

import club.sk1er.elementa.UIComponent
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11.*

/**
 * Allows for arbitrary scissoring of any shaped component.
 *
 * In order to use, you must call [enableStencil] in mod initialization.
 */
class StencilEffect : Effect {
    override fun beforeDraw(component: UIComponent) {
        glEnable(GL_STENCIL_TEST)
        // commented to make component still draw
        //glColorMask ( false, false, false, false)
        glStencilFunc(GL_ALWAYS, 2, 0xffffffff.toInt())
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE)
    }

    override fun beforeChildrenDraw(component: UIComponent) {
        //glColorMask (true, true, true, true)
        glStencilFunc(GL_EQUAL, 2, 0xffffffff.toInt())
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP)
    }

    override fun afterDraw(component: UIComponent) {
        glDisable(GL_STENCIL_TEST)
    }

    companion object {
        /**
         * Must be called in mod initialization to use [StencilEffect]
         */
        @JvmStatic fun enableStencil() { //TODO wait for 1.15 to impl
            //#if MC<11500
            if (!Minecraft.getMinecraft().framebuffer.isStencilEnabled) {
                Minecraft.getMinecraft().framebuffer.enableStencil()
            }
            //#endif
        }
    }
}