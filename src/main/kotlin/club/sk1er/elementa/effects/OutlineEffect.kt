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
    private val drawAfterChildren: Boolean = false,
    sides: Set<Side> = setOf(Side.Left, Side.Top, Side.Right, Side.Bottom)
) : Effect() {
    private val hasLeft = Side.Left in sides
    private val hasTop = Side.Top in sides
    private val hasRight = Side.Right in sides
    private val hasBottom = Side.Bottom in sides

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

        // Left outline block
        if (hasLeft)
            UIBlock.drawBlock(color, left - width, top, left, bottom)

        // Top outline block
        if (hasTop)
            UIBlock.drawBlock(color, left, top - width, right, top)

        // Right outline block
        if (hasRight)
            UIBlock.drawBlock(color, right, top, right + width, bottom)

        // Bottom outline block
        if (hasBottom)
            UIBlock.drawBlock(color, left, bottom, right, bottom + width)

        // Top left square
        if (hasLeft && hasTop)
            UIBlock.drawBlock(color, left - width, top - width, left, top)

        // Top right square
        if (hasRight && hasTop)
            UIBlock.drawBlock(color, right, top - width, right + width, top)

        // Bottom right square
        if (hasRight && hasBottom)
            UIBlock.drawBlock(color, right, bottom, right + width, bottom + width)

        if (hasBottom && hasLeft)
            UIBlock.drawBlock(color, left - width, bottom, left, bottom + width)
    }

    enum class Side {
        Left,
        Top,
        Right,
        Bottom,
    }
}
