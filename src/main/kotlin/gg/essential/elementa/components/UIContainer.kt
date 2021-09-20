package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.universal.UMatrixStack

/**
 * Bare-bones component that does no rendering and simply offers a bounding box.
 */
open class UIContainer : UIComponent() {
    override fun draw(matrixStack: UMatrixStack) {
        // This is necessary because if it isn't here, effects will never be applied.
        beforeDrawCompat(matrixStack)

        // no-op

        super.draw(matrixStack)
    }
}