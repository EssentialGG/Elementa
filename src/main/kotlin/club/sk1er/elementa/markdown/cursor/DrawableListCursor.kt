package club.sk1er.elementa.markdown.cursor

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownComponent
import club.sk1er.elementa.markdown.drawables.Drawable
import club.sk1er.elementa.markdown.drawables.DrawableList
import club.sk1er.elementa.markdown.drawables.ListDrawable
import club.sk1er.elementa.markdown.drawables.ParagraphDrawable

class DrawableListCursor(val drawables: DrawableList) : DrawableCursor() {
    private var impl: DrawableCursor? = null

    override val target: Drawable
        get() = impl!!.target

    override fun moveToStart() {
        impl = forDrawable(drawables.first()).apply {
            moveToStart()
        }
    }

    override fun moveToEnd() {
        impl = forDrawable(drawables.last()).apply {
            moveToEnd()
        }
    }

    override fun moveTo(mouseX: Float, mouseY: Float) {
        if (impl?.target?.isHovered(mouseX, mouseY) == true) {
            impl!!.moveTo(mouseX, mouseY)
            return
        }

        // Used for positioning the cursor in-between drawables if no
        // drawable is being directly hovered
        var closestDrawable: Drawable? = null
        var closestDistance = Float.MAX_VALUE
        var direction: Direction? = null

        for (drawable in drawables) {
            if (drawable.isHovered(mouseX, mouseY)) {
                impl = forDrawable(drawable).apply {
                    moveTo(mouseX, mouseY)
                }
                return
            } else {
                if (mouseY < drawable.y && drawable.y - mouseY < closestDistance) {
                    direction = Direction.Down
                    closestDistance = drawable.y - mouseY
                    closestDrawable = drawable
                } else if (mouseY > drawable.y + drawable.height && drawable.y + drawable.height - mouseY < closestDistance) {
                    direction = Direction.Up
                    closestDistance = mouseY - (drawable.y + drawable.height)
                    closestDrawable = drawable
                } else {
                    // The drawable is hovered vertically, but not horizontally
                    closestDistance = 0f
                    closestDrawable = drawable
                    direction = if (mouseX < drawable.x) {
                        Direction.Left
                    } else Direction.Right
                    break
                }
            }
        }

        if (closestDrawable == null || closestDistance == Float.MAX_VALUE || direction == null)
            TODO()

        impl = forDrawable(closestDrawable)

        when (direction) {
            Direction.Up -> impl!!.moveToEnd()
            Direction.Down -> impl!!.moveToStart()
            Direction.Left, Direction.Right -> impl!!.moveTo(mouseX, mouseY)
        }
    }

    enum class Direction {
        Up,
        Down,
        Left,
        Right
    }

    override fun draw(state: DrawState) {
        impl?.draw(state)
    }
}
