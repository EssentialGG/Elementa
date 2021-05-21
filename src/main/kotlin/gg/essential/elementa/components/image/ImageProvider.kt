package gg.essential.elementa.components.image

import java.awt.Color

interface ImageProvider {
    /**
     * Render the image provided by this component with the provided attributes.
     *
     * This method is guaranteed to be called from the main thread.
     */
    fun drawImage(x: Double, y: Double, width: Double, height: Double, color: Color)
}