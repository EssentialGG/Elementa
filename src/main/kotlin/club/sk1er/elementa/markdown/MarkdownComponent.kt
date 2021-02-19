package club.sk1er.elementa.markdown

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.markdown.drawables.Drawable
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

    private lateinit var drawables: List<Drawable>
    private lateinit var lastValues: ConstraintValues

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

    private fun reparse() {
        drawables = MarkdownRenderer(textState.get(), config).render()
    }

    private fun layout() {
        val x = getLeft()
        var y = getTop()
        val width = getWidth()

        drawables.forEach {
            y += it.layout(x, y, width)
        }
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

        drawables.forEach(Drawable::draw)

        afterDraw()
    }

    private fun constraintValues() = ConstraintValues(
        getLeft(),
        getTop(),
        getWidth(),
        getHeight(),
        getRadius(),
        getTextScale(),
        getColor()
    )

    data class ConstraintValues(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val radius: Float,
        val textScale: Float,
        val color: Color
    )
}
