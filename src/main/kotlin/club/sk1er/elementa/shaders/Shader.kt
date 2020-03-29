package club.sk1er.elementa.shaders

import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.GL20

open class Shader(private val name: String) {
    protected var vertShader: Int = 0
    protected var fragShader: Int = 0
    protected var program: Int = 0
    var usable = false

    init {
        createShader()
    }

    fun bindIfUsable() {
        if (usable) {
            UniversalGraphicsHandler.glUseProgram(program)
            GlStateManager.disableBlend()
        }
    }

    fun unbindIfUsable() {
        if (usable) {
            UniversalGraphicsHandler.glUseProgram(0)
        }
    }

    private fun createShader() {
        program = UniversalGraphicsHandler.glCreateProgram()

        vertShader = UniversalGraphicsHandler.glCreateShader(GL20.GL_VERTEX_SHADER)
        if (Shaders.newShaders)
            GL20.glShaderSource(vertShader, readShader("vsh"))
        else
            ARBShaderObjects.glShaderSourceARB(vertShader, readShader("vsh"))
        UniversalGraphicsHandler.glCompileShader(vertShader)

        if (UniversalGraphicsHandler.glGetShaderi(vertShader, GL20.GL_COMPILE_STATUS) != 1) {
            println(UniversalGraphicsHandler.glGetShaderInfoLog(vertShader, 32768))
            return
        }

        fragShader = UniversalGraphicsHandler.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        if (Shaders.newShaders)
            GL20.glShaderSource(fragShader, readShader("fsh"))
        else
            ARBShaderObjects.glShaderSourceARB(fragShader, readShader("fsh"))
        UniversalGraphicsHandler.glCompileShader(fragShader)

        if (UniversalGraphicsHandler.glGetShaderi(fragShader, GL20.GL_COMPILE_STATUS) != 1) {
            println(UniversalGraphicsHandler.glGetShaderInfoLog(fragShader, 32768))
            return
        }

        UniversalGraphicsHandler.glAttachShader(program, vertShader)
        UniversalGraphicsHandler.glAttachShader(program, fragShader)

        UniversalGraphicsHandler.glLinkProgram(program)

        if (UniversalGraphicsHandler.glGetProgrami(program, GL20.GL_LINK_STATUS) != 1) {
            println(UniversalGraphicsHandler.glGetProgramInfoLog(program, 32768))
            return
        }

        if (Shaders.newShaders) GL20.glValidateProgram(program) else ARBShaderObjects.glValidateProgramARB(program)

        if (UniversalGraphicsHandler.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) != 1) {
            println(UniversalGraphicsHandler.glGetProgramInfoLog(program, 32768))
            return
        }

        usable = true
    }

    private fun readShader(ext: String) =
        this::class.java.getResource("/shaders/$name.$ext").readText()
}