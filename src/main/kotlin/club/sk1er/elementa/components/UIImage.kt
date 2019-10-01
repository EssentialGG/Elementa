package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.shaders.Shaders
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class UIImage @JvmOverloads constructor(name: String, url: String? = null) : UIComponent() {
    private val image: BufferedImage
    private val texture: DynamicTexture
    private var useShader = false
    private var program = 0

    init {
        image = getBufferedImage(name, url)
        texture = DynamicTexture(image)

//        initShader()
    }

    private fun initShader() {
        var vertShader = 0
        var fragShader = 0

        try {
            vertShader = createShader("/shaders/blur.vsh", GL20.GL_VERTEX_SHADER)
            fragShader = createShader("/shaders/blur.fsh", GL20.GL_FRAGMENT_SHADER)
        } catch (exc: Exception) {
            exc.printStackTrace()
            return
        } finally {
            if (vertShader == 0 || fragShader == 0)
                return
        }

        program = GL20.glCreateProgram()

        if (program == 0)
            return

        /*
        * if the vertex and fragment shaders setup sucessfully,
        * attach them to the shader program, link the sahder program
        * (into the GL context I suppose), and validate
        */
//        GL20.glAttachShader(program, vertShader)
//        GL20.glAttachShader(program, fragShader)
//        ARBShaderObjects.glAttachObjectARB(program, vertShader)
//        ARBShaderObjects.glAttachObjectARB(program, fragShader)

        GL20.glLinkProgram(program)
//        ARBShaderObjects.glLinkProgramARB(program)
//        if (ARBShaderObjects.glGetObjectParameteriARB(
        if (GL20.glGetShaderi(
                program,
                GL20.GL_LINK_STATUS
//                ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB
            ) == GL11.GL_FALSE
        ) {
            System.err.println(getLogInfo(program))
            return
        }

        GL20.glValidateProgram(program)
//        ARBShaderObjects.glValidateProgramARB(program)
//        if (ARBShaderObjects.glGetObjectParameteriARB(
        if (GL20.glGetShaderi(
                program,
                GL20.GL_VALIDATE_STATUS
//                ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB
            ) == GL11.GL_FALSE
        ) {
            System.err.println(getLogInfo(program))
            return
        }

        useShader = true
    }

    /*
    * With the exception of syntax, setting up vertex and fragment shaders
    * is the same.
    * @param the name and path to the vertex shader
    */
    @Throws(Exception::class)
    private fun createShader(filename: String, shaderType: Int): Int {
        var shader = 0
        try {
            shader = GL20.glCreateShader(shaderType)
//            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType)

            println("Shader created!")

            if (shader == 0)
                return 0

            GL20.glShaderSource(shader, this::class.java.getResourceAsStream(filename).bufferedReader().readText())
            GL20.glCompileShader(shader)

            println("Shader compiled!")
//            ARBShaderObjects.glShaderSourceARB(shader, this::class.java.getResourceAsStream(filename).bufferedReader().readText())
//            ARBShaderObjects.glCompileShaderARB(shader)

            if (GL20.glGetShaderi(
//            if (ARBShaderObjects.glGetObjectParameteriARB(
                    shader,
                    GL20.GL_COMPILE_STATUS
//                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB
                ) == GL11.GL_FALSE
            )
                throw RuntimeException("Error creating shader: " + getLogInfo(shader))

            println("Shader finished!")

            return shader
        } catch (exc: Exception) {
            GL20.glDeleteShader(shader)
//            ARBShaderObjects.glDeleteObjectARB(shader)
            throw exc
        }

    }

    private fun getLogInfo(obj: Int): String {
//        return ARBShaderObjects.glGetInfoLogARB(
        return GL20.glGetProgramInfoLog(
            obj,
            GL20.glGetShaderi(obj, GL20.GL_INFO_LOG_LENGTH /*ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB*/)
//            ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)
        )
    }

    override fun makeDefaultConstraints(): UIConstraints {
        val superConstraints = super.makeDefaultConstraints()
        superConstraints.setWidth(PixelConstraint(image.width.toFloat()))
        superConstraints.setHeight(PixelConstraint(image.height.toFloat()))
        return superConstraints
    }

    override fun draw() {
        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()

        GL11.glPushMatrix()

        Shaders.blurShader.bindIfUsable()
//        if(useShader)
//            GL20.glUseProgram(program)
//            ARBShaderObjects.glUseProgramObjectARB(program);

        GlStateManager.enableBlend()
        GlStateManager.scale(1f, 1f, 50f)
        GlStateManager.bindTexture(texture.glTextureId)
        GlStateManager.enableTexture2D()

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX)

        worldRenderer.pos(x, y + height, 0.0).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(x + width, y + height, 0.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(x + width, y, 0.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()

        Shaders.blurShader.unbindIfUsable()
//        if(useShader)
//            GL20.glUseProgram(0)
//            ARBShaderObjects.glUseProgramObjectARB(0);

        GL11.glPopMatrix()

        super.draw()
    }

    private fun getBufferedImage(name: String, url: String? = null): BufferedImage {
        // TODO: fix this
        val resourceFile = File(name)

        if (resourceFile.exists()) {
            return ImageIO.read(resourceFile)
        }

        val image = ImageIO.read(URL(url))
        resourceFile.parentFile.mkdirs()
        resourceFile.createNewFile()
        ImageIO.write(image, "png", resourceFile)
        return image
    }


}