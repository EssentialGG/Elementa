package club.sk1er.elementa.shaders

import net.minecraft.client.renderer.OpenGlHelper.*
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
        if (usable) glUseProgram(program)
    }

    fun unbindIfUsable() {
        if (usable) glUseProgram(0)
    }

    private fun createShader() {
        program = glCreateProgram()

        vertShader = glCreateShader(GL_VERTEX_SHADER)
        if (Shaders.newShaders)
            GL20.glShaderSource(vertShader, readShader("vsh"))
        else
            ARBShaderObjects.glShaderSourceARB(vertShader, readShader("vsh"))
        glCompileShader(vertShader)

        if (glGetShaderi(vertShader, GL_COMPILE_STATUS) != 1) {
            println(glGetShaderInfoLog(vertShader, 32768))
            return
        }

        fragShader = glCreateShader(GL_FRAGMENT_SHADER)
        if (Shaders.newShaders)
            GL20.glShaderSource(fragShader, readShader("fsh"))
        else
            ARBShaderObjects.glShaderSourceARB(fragShader, readShader("fsh"))
        glCompileShader(fragShader)

        if (glGetShaderi(fragShader, GL_COMPILE_STATUS) != 1) {
            println(glGetShaderInfoLog(fragShader, 32768))
            return
        }

        glAttachShader(program, vertShader)
        glAttachShader(program, fragShader)

        glLinkProgram(program)

        if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
            println(glGetProgramInfoLog(program, 32768))
            return
        }

        if (Shaders.newShaders) GL20.glValidateProgram(program) else ARBShaderObjects.glValidateProgramARB(program)

        if (glGetProgrami(program, GL20.GL_VALIDATE_STATUS) != 1) {
            println(glGetProgramInfoLog(program, 32768))
            return
        }

        usable = true
    }

    private fun readShader(ext: String) =
        this::class.java.getResource("/shaders/$name.$ext").readText()
}