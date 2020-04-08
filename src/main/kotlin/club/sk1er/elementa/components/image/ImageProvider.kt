package club.sk1er.elementa.components.image

import net.minecraft.client.renderer.texture.AbstractTexture

interface ImageProvider {
    /**
     * Gets the MC texture of this image provider.
     *
     * This method is guaranteed to be called from the main thread,
     * so you can upload the texture from here.
     */
    fun getTexture(preferredWidth: Int, preferredHeight: Int): AbstractTexture
}