package gg.essential.elementa.effects

import gg.essential.elementa.UIComponent
import gg.essential.universal.UResolution
import org.lwjgl.opengl.GL11.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Enables GL Scissoring to restrict all drawing done by
 * the component where this is enabled to be only inside of that component's bounds.
 *
 * By proxy, this restricts all of said component's children drawing to inside of the same bounds.
 *
 * [scissorIntersection] will try to combine this scissor with all of it's parents scissors (if any).
 */
class ScissorEffect @JvmOverloads constructor(
    private val customBoundingBox: UIComponent? = null,
    private val scissorIntersection: Boolean = true
) : Effect() {
    private var oldState: ScissorState? = null
    private var scissorBounds: ScissorBounds? = null

    /**
     * Create a custom bounding box using precise coordinates.
     */
    @JvmOverloads
    constructor(
        x1: Number,
        y1: Number,
        x2: Number,
        y2: Number,
        scissorIntersection: Boolean = true
    ) : this(scissorIntersection = scissorIntersection) {
        scissorBounds = ScissorBounds(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }

    override fun beforeDraw() {
        val bounds = customBoundingBox?.getScissorBounds() ?: scissorBounds ?: boundComponent.getScissorBounds()
        val scaleFactor = UResolution.scaleFactor.toInt()

        if (currentScissorState == null) {
            glEnable(GL_SCISSOR_TEST)
        }

        oldState = currentScissorState
        val state = oldState

        var x = bounds.x1 * scaleFactor
        var y = (UResolution.scaledHeight * scaleFactor) - (bounds.y2 * scaleFactor)
        var width = bounds.width * scaleFactor
        var height = bounds.height * scaleFactor

        if (state != null && scissorIntersection) {
            val x2 = x + width
            val y2 = y + height

            val oldX = state.x
            val oldY = state.y
            val oldX2 = state.x + state.width
            val oldY2 = state.y + state.height

            x = max(x, oldX)
            y = max(y, oldY)
            width = min(x2, oldX2) - x
            height = min(y2, oldY2) - y
        }

        glScissor(x, y, width.coerceAtLeast(0), height.coerceAtLeast(0))

        currentScissorState = ScissorState(x, y, width.coerceAtLeast(0), height.coerceAtLeast(0))
    }

    override fun afterDraw() {
        val state = oldState

        if (state != null) {
            glScissor(state.x, state.y, state.width, state.height)

            oldState = null
        } else {
            glDisable(GL_SCISSOR_TEST)
        }

        currentScissorState = state
    }

    private fun UIComponent.getScissorBounds(): ScissorBounds = ScissorBounds(
        getLeft().roundToInt(),
        getTop().roundToInt(),
        getRight().roundToInt(),
        getBottom().roundToInt()
    )

    data class ScissorState(val x: Int, val y: Int, val width: Int, val height: Int)

    private data class ScissorBounds(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
        val width: Int
            get() = x2 - x1
        val height: Int
            get() = y2 - y1
    }

    companion object {
        var currentScissorState: ScissorState? = null
    }
}
