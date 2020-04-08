package club.sk1er.elementa.components.image

import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.DynamicTexture
import javax.imageio.ImageIO

object DefaultLoadingImage : ImageProvider {
    private val loadingImage = ImageIO.read(this::class.java.getResourceAsStream("/loading.png"))
    private lateinit var loadingTexture: DynamicTexture

    override fun getTexture(preferredWidth: Int, preferredHeight: Int): AbstractTexture {
        if (!::loadingTexture.isInitialized) {
            loadingTexture = UniversalGraphicsHandler.getTexture(loadingImage)
        }

        return loadingTexture
    }
}