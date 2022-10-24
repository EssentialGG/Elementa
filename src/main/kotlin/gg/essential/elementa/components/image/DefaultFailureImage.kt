package gg.essential.elementa.components.image

import gg.essential.elementa.utils.drawTexture
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.utils.ReleasedDynamicTexture
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object DefaultFailureImage : ImageProvider {

    private var loadingImage: BufferedImage? = ImageIO.read(this::class.java.getResourceAsStream("/textures/failure.png"))
    private lateinit var loadingTexture: ReleasedDynamicTexture

    override fun drawImage(
        matrixStack: UMatrixStack,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        color: Color,
    ) {
        if (!::loadingTexture.isInitialized) {
            loadingTexture = UGraphics.getTexture(loadingImage!!)
            loadingImage = null
        }

        drawTexture(
            matrixStack,
            loadingTexture,
            color,
            x,
            y,
            width,
            height,
            textureMinFilter = GL11.GL_LINEAR,
            textureMagFilter = GL11.GL_LINEAR,
        )
    }
}
