package club.sk1er.elementa.utils

import club.sk1er.elementa.components.UIImage
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiFunction
import javax.imageio.ImageIO

class ResourceCache(val size: Int = 50) {
    private val cacheMap = ConcurrentHashMap<String, UIImage>()

    fun get(path: String): UIImage {
        if (cacheMap.size > size)
            cacheMap.clear()
        return cacheMap.compute(path, BiFunction { pth, current ->
            if (current != null) {
                val tmp = CompletableFuture<BufferedImage>()
                tmp.complete(null)
                val uiImage = UIImage(tmp)
                current.supply(uiImage)
                return@BiFunction uiImage
            }
            UIImage(CompletableFuture.supplyAsync {
                ImageIO.read(this::class.java.getResourceAsStream(pth))
            })
        })!!
    }

    fun invalidateAll() {
        cacheMap.clear()
    }

    fun invalidate(path: String): Boolean {
        return cacheMap.remove(path) != null
    }
}