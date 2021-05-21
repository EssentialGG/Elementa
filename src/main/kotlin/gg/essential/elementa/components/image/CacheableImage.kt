package gg.essential.elementa.components.image

import gg.essential.universal.utils.ReleasedDynamicTexture

interface CacheableImage {

    fun supply(image: CacheableImage)

    fun applyTexture(texture: ReleasedDynamicTexture?)

}