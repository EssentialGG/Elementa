package club.sk1er.elementa.effects

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import club.sk1er.elementa.utils.guiHint
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

        val leftHinted = left.guiHint()
        val rightHinted = right.guiHint()
        val topHinted = top.guiHint()
        val bottomHinted = bottom.guiHint()

        val leftWidthHinted = (left - width).guiHint()
        val rightWidthHinted = (right + width).guiHint()
        val topWidthHinted = (top - width).guiHint()
        val bottomWidthHinted = (bottom + width).guiHint()

        // Left outline block
        if (hasLeft)
            UIBlock.drawBlock(color, leftWidthHinted, topHinted, leftHinted, bottomHinted)

        // Top outline block
        if (hasTop)
            UIBlock.drawBlock(color, leftHinted, topWidthHinted, rightHinted, topHinted)

        // Right outline block
        if (hasRight)
            UIBlock.drawBlock(color, rightHinted, topHinted, rightWidthHinted, bottomHinted)

        // Bottom outline block
        if (hasBottom)
            UIBlock.drawBlock(color, leftHinted, bottomHinted, rightHinted, bottomWidthHinted)

        // Top left square
        if (hasLeft && hasTop)
            UIBlock.drawBlock(color, leftWidthHinted, topWidthHinted, leftHinted, topHinted)

        // Top right square
        if (hasRight && hasTop)
            UIBlock.drawBlock(color, rightHinted, topWidthHinted, rightWidthHinted, topHinted)

        // Bottom right square
        if (hasRight && hasBottom)
            UIBlock.drawBlock(color, rightHinted, bottomHinted, rightWidthHinted, bottomWidthHinted)

        if (hasBottom && hasLeft)
            UIBlock.drawBlock(color, leftWidthHinted, bottomHinted, leftHinted, bottomWidthHinted)
    }

    enum class Side {
        Left,
        Top,
        Right,
        Bottom,
    }
}
