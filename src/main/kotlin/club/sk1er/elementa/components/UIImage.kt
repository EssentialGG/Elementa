package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.shaders.Shaders
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class UIImage @JvmOverloads constructor(name: String, url: String? = null) : UIComponent() {
    private val image: BufferedImage
    private val texture: DynamicTexture

    init {
        image = getBufferedImage(name, url)
        texture = DynamicTexture(image)
        super.getConstraints().setWidth(PixelConstraint(image.width.toFloat()))
        super.getConstraints().setHeight(PixelConstraint(image.height.toFloat()))
    }

    override fun draw() {
        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()

        GL11.glPushMatrix()

        Shaders.blurShader.bindIfUsable()

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