package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIImage
import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.XConstraint
import club.sk1er.elementa.constraints.YConstraint
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownComponent
import club.sk1er.elementa.markdown.selection.ImageCursor
import java.net.URL

class ImageDrawable(md: MarkdownComponent, val url: URL, private val fallback: Drawable) : Drawable(md) {
    var selected = false

    private lateinit var imageX: ShiftableMDPixelConstraint
    private lateinit var imageY: ShiftableMDPixelConstraint

    private val image = UIImage.ofURL(url) childOf md
    private var hasLoaded = false

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        return if (image.isLoaded) {
            imageX = ShiftableMDPixelConstraint(x, 0f)
            imageY = ShiftableMDPixelConstraint(y, 0f)
            image.setX(imageX)
            image.setY(imageY)

            val aspectRatio = image.imageWidth / image.imageHeight
            val imageWidth = image.imageWidth.coerceAtMost(width)
            val imageHeight = imageWidth / aspectRatio

            image.setWidth(imageWidth.pixels())
            image.setHeight(imageHeight.pixels())

            Layout(x, y, imageWidth, imageHeight)
        } else fallback.layout(x, y, width)
    }

    override fun draw(state: DrawState) {
        if (!image.isLoaded) {
            fallback.draw(state)
        } else {
            if (!hasLoaded) {
                hasLoaded = true
                md.layout()
            }

            imageX.shift = state.xShift
            imageY.shift = state.yShift
            image.draw()
        }
    }

    // ImageDrawable mouse selection is managed by ParagraphDrawable#select
    override fun cursorAt(mouseX: Float, mouseY: Float, dragged: Boolean) = throw IllegalStateException("never called")
    override fun cursorAtStart() = ImageCursor(this)
    override fun cursorAtEnd() = ImageCursor(this)

    override fun selectedText(asMarkdown: Boolean): String {
        if (asMarkdown) {
            // TODO: `fallback.selectedText(true)` will be empty since the children aren't
            // marked as selected
            return " ![${fallback.selectedText(true)}]($url) "
        }
        return " $url "
    }

    // TODO: Rename this function?
    override fun hasSelectedText() = selected

    private inner class ShiftableMDPixelConstraint(val base: Float, var shift: Float) : XConstraint, YConstraint {
        override var cachedValue = 0f
        override var recalculate = true
        override var constrainTo: UIComponent? = null

        override fun getXPositionImpl(component: UIComponent) = base + shift
        override fun getYPositionImpl(component: UIComponent) = base + shift

        override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
    }
}
