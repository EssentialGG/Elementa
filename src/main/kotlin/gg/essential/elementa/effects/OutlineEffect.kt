package gg.essential.elementa.effects

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import java.awt.Color

/**
 * Draws a basic, rectangular outline of the specified [color] and [width] around
 * this component. The outline will be drawn before this component's children are drawn,
 * so all children will render above the outline.
 */
class OutlineEffect @JvmOverloads constructor(
    color: Color,
    width: Float,
    var drawAfterChildren: Boolean = false,
    var drawInsideChildren: Boolean = false,
    sides: Set<Side> = setOf(Side.Left, Side.Top, Side.Right, Side.Bottom)
) : Effect() {
    private var hasLeft = Side.Left in sides
    private var hasTop = Side.Top in sides
    private var hasRight = Side.Right in sides
    private var hasBottom = Side.Bottom in sides

    private var colorState: State<Color> = BasicState(color)
    private var widthState: State<Float> = BasicState(width)

    var color: Color
        get() = colorState.get()
        set(value) {
            colorState.set(value)
        }

    var width: Float
        get() = widthState.get()
        set(value) {
            widthState.set(value)
        }

    fun bindColor(state: State<Color>) = apply {
        colorState = state
    }

    fun bindWidth(state: State<Float>) = apply {
        widthState = state
    }

    var sides = sides
        set(value) {
            field = value
            hasLeft = Side.Left in sides
            hasTop = Side.Top in sides
            hasRight = Side.Right in sides
            hasBottom = Side.Bottom in sides
        }

    fun addSide(side: Side) = apply {
        sides = sides + side
    }

    fun removeSide(side: Side) = apply {
        sides = sides - side
    }

    override fun beforeChildrenDraw() {
        if (!drawAfterChildren)
            drawOutline()
    }

    override fun afterDraw() {
        if (drawAfterChildren)
            drawOutline()
    }

    private fun drawOutline() {
        val color = colorState.get()
        val width = widthState.get()

        val left = boundComponent.getLeft().toDouble()
        val right = boundComponent.getRight().toDouble()
        val top = boundComponent.getTop().toDouble()
        val bottom = boundComponent.getBottom().toDouble()

        val leftBounds = if (drawInsideChildren) {
            left to (left + width)
        } else (left - width) to left

        val topBounds = if (drawInsideChildren) {
            top to (top + width)
        } else (top - width) to top

        val rightBounds = if (drawInsideChildren) {
            (right - width) to right
        } else right to (right + width)

        val bottomBounds = if (drawInsideChildren) {
            (bottom - width) to bottom
        } else bottom to (bottom + width)

        // Left outline block
        if (hasLeft)
            UIBlock.drawBlock(color, leftBounds.first, top, leftBounds.second, bottom)

        // Top outline block
        if (hasTop)
            UIBlock.drawBlock(color, left, topBounds.first, right, topBounds.second)

        // Right outline block
        if (hasRight)
            UIBlock.drawBlock(color, rightBounds.first, top, rightBounds.second, bottom)

        // Bottom outline block
        if (hasBottom)
            UIBlock.drawBlock(color, left, bottomBounds.first, right, bottomBounds.second)

        if (!drawInsideChildren) {
            // Top left square
            if (hasLeft && hasTop)
                UIBlock.drawBlock(color, leftBounds.first, topBounds.first, left, top)

            // Top right square
            if (hasRight && hasTop)
                UIBlock.drawBlock(color, right, topBounds.first, rightBounds.second, top)

            // Bottom right square
            if (hasRight && hasBottom)
                UIBlock.drawBlock(color, right, bottom, rightBounds.second, bottomBounds.second)

            // Bottom left square
            if (hasBottom && hasLeft)
                UIBlock.drawBlock(color, leftBounds.first, bottom, left, bottomBounds.second)
        }
    }

    enum class Side {
        Left,
        Top,
        Right,
        Bottom,
    }
}
