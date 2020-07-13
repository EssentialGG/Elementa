package club.sk1er.elementa.components.image

import club.sk1er.elementa.utils.drawTexture
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object DefaultLoadingImage : ImageProvider {
    private var loadingImage: BufferedImage? = ImageIO.read(this::class.java.getResourceAsStream("/loading.png"))
    private lateinit var loadingTexture: DynamicTexture

    override fun drawImage(x: Double, y: Double, width: Double, height: Double, color: Color) {
        if (!::loadingTexture.isInitialized) {
            loadingTexture = UniversalGraphicsHandler.getTexture(loadingImage!!)
            loadingImage = null
        }

        drawTexture(loadingTexture, color, x, y, width, height)
    }
}