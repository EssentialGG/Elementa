package gg.essential.elementa.markdown

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.HeightConstraint
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.markdown.drawables.Drawable
import gg.essential.elementa.markdown.drawables.DrawableList
import gg.essential.elementa.markdown.selection.Cursor
import gg.essential.elementa.markdown.selection.Selection
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.font.ElementaFonts
import gg.essential.elementa.font.FontProvider
import gg.essential.universal.UKeyboard
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

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
    val config: MarkdownConfig = MarkdownConfig(),
    private val codeFontPointSize: Float = 10f,
    private val codeFontRenderer: FontProvider = ElementaFonts.JETBRAINS_MONO
) : UIComponent() {
    private var textState: State<String> = BasicState(text)
    private var removeListener = textState.onSetValue {
        reparse()
        layout()
    }

    val drawables = DrawableList(this, emptyList())

    private var baseX: Float = -1f
    private var baseY: Float = -1f
    private lateinit var lastValues: ConstraintValues
    private var maxHeight: HeightConstraint = Int.MAX_VALUE.pixels()
    private var cursor: Cursor<*>? = null
    private var selection: Selection? = null
    private var canDrag = false

    init {
        onMouseClick {
            val xShift = getLeft() - baseX
            val yShift = getTop() - baseY
            cursor = drawables.cursorAt(it.absoluteX - xShift, it.absoluteY - yShift, dragged = false, it.mouseButton)

            selection?.remove()
            selection = null
            releaseWindowFocus()

            canDrag = true
        }

        onMouseRelease {
            canDrag = false
        }

        onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton != 0 || !canDrag)
                return@onMouseDrag

            val x = baseX + mouseX.coerceIn(0f, getWidth())
            val y = baseY + mouseY.coerceIn(0f, getHeight())

            val otherEnd = drawables.cursorAt(x, y, dragged = true, mouseButton)

            if (cursor == otherEnd)
                return@onMouseDrag

            selection?.remove()
            selection = Selection.fromCursors(cursor!!, otherEnd)
            grabWindowFocus()
        }

        onKeyType { _, keyCode ->
            if (selection != null && keyCode == UKeyboard.KEY_C && UKeyboard.isCtrlKeyDown()) {
                val selection = StringSelection(drawables.selectedText(UKeyboard.isShiftKeyDown()))
                Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
            }
        }
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
        drawables.setDrawables(MarkdownRenderer(textState.get(), this, config).render())
    }

    /**
     * This method is responsible for laying out the markdown tree.
     *
     * @see Drawable.layout
     */
    fun layout() {
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

        // Re-layout if important constraint values have changed
        val currentValues = constraintValues()
        if (currentValues != lastValues)
            layout()
        lastValues = currentValues

        val drawState = DrawState(getLeft() - baseX, getTop() - baseY)

        drawables.forEach { it.draw(drawState) }
        selection?.draw(drawState) ?: cursor?.draw(drawState)

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
        // TODO: Remove
        const val DEBUG = false
    }
}
