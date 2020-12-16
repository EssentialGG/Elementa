package club.sk1er.elementa.effects

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import java.awt.Color

/**
 * Draws a basic, rectangular outline of the specified [color] and [width] around
 * this component. The outline will be drawn before this component's children are drawn,
 * so all children will render above the outline.
 */
class OutlineEffect @JvmOverloads constructor(
    private val color: Color,
    private val width: Float,
    private val drawAfterChildren: Boolean = false
) : Effect() {
    override fun beforeChildrenDraw() {
        if (!drawAfterChildren)
            drawOutline()
    }

    override fun afterDraw() {
        if (drawAfterChildren)
            drawOutline()
    }

    private fun drawOutline() {
        val left = boundComponent.getLeft().toDouble()
        val right = boundComponent.getRight().toDouble()
        val top = boundComponent.getTop().toDouble()
        val bottom = boundComponent.getBottom().toDouble()

        // Top outline block
        UIBlock.drawBlock(color, left - width, top - width, right + width, top)

        // Right outline block
        UIBlock.drawBlock(color, right, top, right + width, bottom)

        // Bottom outline block
        UIBlock.drawBlock(color, left - width, bottom, right + width, bottom + width)

        // Left outline block
        UIBlock.drawBlock(color, left - width, top, left, bottom)
    }
}
