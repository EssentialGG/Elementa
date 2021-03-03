package club.sk1er.elementa.markdown

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.HeightConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.elementa.markdown.cursor.TextCursor
import club.sk1er.elementa.markdown.drawables.Drawable
import club.sk1er.elementa.markdown.drawables.DrawableList
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import java.awt.Color

/**
 * Component that parses a string as Markdown and renders it.
 *
 * This component's width and height must be non-child-related
 * constraints. This is because the actual text rendering is
 * done with direct render calls instead of through the component
 * hierarchy.
 */
class MarkdownComponent @JvmOverloads constructor(
    text: String,
    private val config: MarkdownConfig = MarkdownConfig()
) : UIComponent() {
    private var textState: State<String> = BasicState(text)
    private var removeListener = textState.onSetValue {
        reparse()
        layout()
    }

    val drawables = DrawableList(config, emptyList())

    private var baseX: Float = -1f
    private var baseY: Float = -1f
    private lateinit var lastValues: ConstraintValues
    private var maxHeight: HeightConstraint = Int.MAX_VALUE.pixels()
    private var cursor: TextCursor? = null

    init {
        onMouseClick {
            val xShift = getLeft() - baseX
            val yShift = getTop() - baseY
            cursor = drawables.select(it.absoluteX - xShift, it.absoluteY - yShift)
        }

        enableEffect(OutlineEffect(Color.BLUE, 1f))
    }

    fun bindText(state: State<String>) = apply {
        removeListener()
        textState = state
        reparse()
        layout()

        removeListener = textState.onSetValue {
            reparse()
            layout()
        }
    }

    fun setMaxHeight(maxHeight: HeightConstraint) = apply {
        this.maxHeight = maxHeight
    }

    /**
     * Parses the text into a markdown tree. This is called everytime
     * that the text of this component changes, and is always followed
     * by a call to layout().
     */
    private fun reparse() {
        drawables.setDrawables(MarkdownRenderer(textState.get(), config).render())
    }

    /**
     * This method is responsible for laying out the markdown tree.
     *
     * @see Drawable.layout
     */
    private fun layout() {
        baseX = getLeft()
        baseY = getTop()
        var currY = baseY
        val width = getWidth()

        drawables.forEach {
            currY += it.layout(baseX, currY, width).height
        }

        setHeight((currY - baseY).coerceAtMost(maxHeight.getHeight(this)).pixels())
    }

    override fun afterInitialization() {
        reparse()
        layout()
        lastValues = constraintValues()
    }

    override fun draw() {
        if (!isInitialized) {
            isInitialized = true
            afterInitialization()
        }

        beforeChildrenDraw()

        val currentValues = constraintValues()
        if (currentValues != lastValues)
            layout()
        lastValues = currentValues

        val drawState = DrawState(getLeft() - baseX, getTop() - baseY)

        drawables.forEach { it.draw(drawState) }
        cursor?.draw(drawState)

        afterDraw()
    }

    private fun constraintValues() = ConstraintValues(
        getWidth(),
        getTextScale()
    )

    /**
     * This class stores the values of the important constraints of this
     * component. If these values change between frames, we need to do a
     * complete re-layout of the entire markdown tree.
     */
    data class ConstraintValues(
        val width: Float,
        val textScale: Float
    )

    companion object {
        const val DEBUG = true
    }
}
