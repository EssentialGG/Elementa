package gg.essential.elementa.components

import gg.essential.elementa.UIComponent

/**
 * Bare bones component that does no drawing and simply offers a bounding box.
 */
open class UIContainer : UIComponent() {
    override fun draw() {
        // This is necessary because if it isn't here, effects will never be applied.
        beforeDraw()

        // no-op

        super.draw()
    }
}