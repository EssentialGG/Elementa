package club.sk1er.elementa.components.image

import java.awt.image.BufferedImage
import java.net.URL

/**
 * Used as optional parameter for UIImage in order to cache images loaded through a URL
 */
interface ImageCache {

    /**
     * Returns the cached version of the URL if available, or null if it should be loaded from the URL
     */
    operator fun get(url: URL): BufferedImage?

    /**
     * Called when an uncached URL is successfully loaded and needs to be cached
     */
    operator fun set(url: URL, image: BufferedImage)


}