package gg.essential.elementa.shaders

import gg.essential.universal.UGraphics
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.GL20

@Deprecated("Use UniversalCraft's UShader instead.")
open class Shader(private val vertName: String, private val fragName: String) {
    protected var vertShader: Int = 0
    protected var fragShader: Int = 0
    protected var program: Int = 0
    var usable = false

    init {
        createShader()
    }

    fun bindIfUsable() {
        if (usable) {
            UGraphics.glUseProgram(program)
        }
    }

    fun unbindIfUsable() {
        if (usable) {
            UGraphics.glUseProgram(0)
        }
    }

    fun getUniformLocation(uniformName: String): Int {
        return if (Shaders.newShaders)
            GL20.glGetUniformLocation(program, uniformName)
        else
            ARBShaderObjects.glGetUniformLocationARB(program, uniformName)
    }

    private fun createShader() {
        program = UGraphics.glCreateProgram()

        vertShader = UGraphics.glCreateShader(GL20.GL_VERTEX_SHADER)
        if (Shaders.newShaders)
            GL20.glShaderSource(vertShader, readShader(vertName, "vsh"))
        else
            ARBShaderObjects.glShaderSourceARB(vertShader, readShader(vertName, "vsh"))
        UGraphics.glCompileShader(vertShader)

        if (UGraphics.glGetShaderi(vertShader, GL20.GL_COMPILE_STATUS) != 1) {
            println(UGraphics.glGetShaderInfoLog(vertShader, 32768))
            return
        }

        fragShader = UGraphics.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        if (Shaders.newShaders)
            GL20.glShaderSource(fragShader, readShader(fragName, "fsh"))
        else
            ARBShaderObjects.glShaderSourceARB(fragShader, readShader(fragName, "fsh"))
        UGraphics.glCompileShader(fragShader)

        if (UGraphics.glGetShaderi(fragShader, GL20.GL_COMPILE_STATUS) != 1) {
            println(UGraphics.glGetShaderInfoLog(fragShader, 32768))
            return
        }

        UGraphics.glAttachShader(program, vertShader)
        UGraphics.glAttachShader(program, fragShader)

        UGraphics.glLinkProgram(program)

        if (Shaders.newShaders) {
            GL20.glDetachShader(program, vertShader)
            GL20.glDetachShader(program, fragShader)
            GL20.glDeleteShader(vertShader)
            GL20.glDeleteShader(fragShader)
        } else {
            ARBShaderObjects.glDetachObjectARB(program, vertShader)
            ARBShaderObjects.glDetachObjectARB(program, fragShader)
            ARBShaderObjects.glDeleteObjectARB(vertShader)
            ARBShaderObjects.glDeleteObjectARB(fragShader)
        }

        if (UGraphics.glGetProgrami(program, GL20.GL_LINK_STATUS) != 1) {
            println(UGraphics.glGetProgramInfoLog(program, 32768))
            return
        }

        if (Shaders.newShaders) GL20.glValidateProgram(program) else ARBShaderObjects.glValidateProgramARB(program)

        if (UGraphics.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) != 1) {
            println(UGraphics.glGetProgramInfoLog(program, 32768))
            return
        }

        usable = true
    }

    private fun readShader(name: String, ext: String) =
        this::class.java.getResource("/shaders/$name.$ext").readText()
}
