package club.sk1er.elementa.shaders

import net.minecraft.client.renderer.OpenGlHelper.glGetAttribLocation

class BlurShader : Shader("blur") {
    private var positionAttrib: Int = 0

    init {
        positionAttrib = glGetAttribLocation(program,"Position")
    }
}