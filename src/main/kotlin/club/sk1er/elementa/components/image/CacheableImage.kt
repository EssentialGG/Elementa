package club.sk1er.elementa.components.image

import club.sk1er.mods.core.universal.utils.ReleasedDynamicTexture

interface CacheableImage {

    fun supply(image: CacheableImage)

    fun applyTexture(texture: ReleasedDynamicTexture?)

}