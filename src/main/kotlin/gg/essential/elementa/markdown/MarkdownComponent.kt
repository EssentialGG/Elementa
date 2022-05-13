package gg.essential.elementa.markdown

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.HeightConstraint
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.markdown.drawables.Drawable
import gg.essential.elementa.markdown.drawables.DrawableList
import gg.essential.elementa.markdown.selection.Cursor
import gg.essential.elementa.markdown.selection.Selection
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.font.ElementaFonts
import gg.essential.elementa.font.FontProvider
import gg.essential.elementa.markdown.drawables.HeaderDrawable
import gg.essential.elementa.utils.elementaDebug
import gg.essential.universal.UDesktop
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI
import kotlin.math.sin

/**
 * Component that parses a string as Markdown and renders it.
 *
 * This component's width and height must be non-child-related
 * constraints. This is because the actual text rendering is
 * done with direct render calls instead of through the component
 * hierarchy.
 */
class MarkdownComponent(
    text: String,
    val config: MarkdownConfig = MarkdownConfig(),
    private val codeFontPointSize: Float = 10f,
    private val codeFontRenderer: FontProvider = ElementaFonts.JETBRAINS_MONO,
    private val disableSelection: Boolean = false,
) : UIComponent() {

    @JvmOverloads
    constructor(
        text: String,
        config: MarkdownConfig = MarkdownConfig(),
        codeFontPointSize: Float = 10f,
        codeFontRenderer: FontProvider = ElementaFonts.JETBRAINS_MONO,
    ) : this(text, config, codeFontPointSize, codeFontRenderer, false)

    private var textState: State<String> = BasicState(text)
    private var removeListener = textState.onSetValue {
        reparse()
        layout()
    }

    val drawables = DrawableList(this, emptyList())
    private val offsets = HashMap<String, Float>()
    val sectionOffsets: Map<String, Float>
        get() = offsets

    private var baseX: Float = -1f
    private var baseY: Float = -1f
    private lateinit var lastValues: ConstraintValues
    private var maxHeight: HeightConstraint = Int.MAX_VALUE.pixels()
    private var cursor: Cursor<*>? = null
    private var selection: Selection? = null
    private var canDrag = false
    private var needsInitialLayout = true

    init {
        onMouseClick {
            val xShift = getLeft() - baseX
            val yShift = getTop() - baseY
            cursor =
                drawables.cursorAt(it.absoluteX - xShift, it.absoluteY - yShift, dragged = false, it.mouseButton)

            selection?.remove()
            selection = null
            releaseWindowFocus()
        }
        if (!disableSelection) {
            onMouseClick {
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
                    UDesktop.setClipboardString(drawables.selectedText(UKeyboard.isShiftKeyDown()))
                }
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
            if (it is HeaderDrawable) {
                offsets[it.id] = currY
            }
            currY += it.layout(baseX, currY, width).height
        }

        setHeight((currY - baseY).coerceAtMost(maxHeight.getHeight(this)).pixels())
    }

    override fun animationFrame() {
        super.animationFrame()

        if (needsInitialLayout) {
            needsInitialLayout = false
            reparse()
            layout()
            lastValues = constraintValues()
        }

        // Re-layout if important constraint values have changed
        val currentValues = constraintValues()
        if (currentValues != lastValues)
            layout()
        lastValues = currentValues
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDraw(matrixStack)

        val drawState = DrawState(getLeft() - baseX, getTop() - baseY)
        val parentWindow = Window.of(this)

        drawables.forEach {

            // Draw colored outline around the drawables
            if (elementaDebug) {
                if (ScissorEffect.currentScissorState != null) {
                    GL11.glDisable(GL11.GL_SCISSOR_TEST)
                }

                val l = it.layout.left.toDouble() + drawState.xShift
                val r = it.layout.right.toDouble() + drawState.xShift
                val t = it.layout.top.toDouble() + drawState.yShift
                val b = it.layout.bottom.toDouble() + drawState.yShift

                val color = getDebugColor(depth(), (parent.hashCode() / PI) % PI)

                // Top outline block
                UIBlock.drawBlock(
                    matrixStack,
                    color,
                    l - DEBUG_OUTLINE_WIDTH,
                    t - DEBUG_OUTLINE_WIDTH,
                    r + DEBUG_OUTLINE_WIDTH,
                    t
                )

                // Right outline block
                UIBlock.drawBlock(matrixStack, color, r, t, r + DEBUG_OUTLINE_WIDTH, b)

                // Bottom outline block
                UIBlock.drawBlock(
                    matrixStack,
                    color,
                    l - DEBUG_OUTLINE_WIDTH,
                    b,
                    r + DEBUG_OUTLINE_WIDTH,
                    b + DEBUG_OUTLINE_WIDTH
                )

                // Left outline block
                UIBlock.drawBlock(matrixStack, color, l - DEBUG_OUTLINE_WIDTH, t, l, b)

                if (ScissorEffect.currentScissorState != null) {
                    GL11.glEnable(GL11.GL_SCISSOR_TEST)
                }
            }

            if (!parentWindow.isAreaVisible(
                    it.layout.left.toDouble() + drawState.xShift, it.layout.top.toDouble() + drawState.yShift,
                    it.layout.right.toDouble() + drawState.xShift, it.layout.bottom.toDouble() + drawState.yShift
            )) return@forEach
            it.draw(matrixStack, drawState)
        }
        if (!disableSelection)
            selection?.draw(matrixStack, drawState) ?: cursor?.draw(matrixStack, drawState)

        super.draw(matrixStack)
    }

    private fun constraintValues() = ConstraintValues(
        getWidth(),
        getTextScale(),
    )

    /**
     * This class stores the values of the important constraints of this
     * component. If these values change between frames, we need to do a
     * complete re-layout of the entire markdown tree.
     */
    data class ConstraintValues(
        val width: Float,
        val textScale: Float,
    )

    companion object {
        // TODO: Remove
        const val DEBUG = false

        private fun getDebugColor(depth: Int, offset: Double): Color {
            val step = depth.toDouble() / PI + offset

            val red = ((sin((step)) + 0.75) * 170).toInt().coerceIn(0..255)
            val green = ((sin(step + 2 * Math.PI / 3) + 0.75) * 170).toInt().coerceIn(0..255)
            val blue = ((sin(step + 4 * Math.PI / 3) + 0.75) * 170).toInt().coerceIn(0..255)
            return Color(red, green, blue, 255)
        }
    }
}
