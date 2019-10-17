package club.sk1er.elementa.shaders

import net.minecraft.client.renderer.OpenGlHelper.areShadersSupported
import org.lwjgl.opengl.GLContext

object Shaders {
    val newShaders = areShadersSupported() && GLContext.getCapabilities().OpenGL21
}