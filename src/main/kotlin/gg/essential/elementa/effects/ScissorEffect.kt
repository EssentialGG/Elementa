package gg.essential.elementa.effects

import gg.essential.elementa.UIComponent
import gg.essential.elementa.utils.resolutionManager
import gg.essential.elementa.utils.roundToRealPixels
import gg.essential.universal.UMatrixStack
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
class ScissorEffect private constructor(
    private val customBoundingBox: UIComponent? = null,
    private val scissorIntersection: Boolean = true,
    private val unroundedScissorBounds: ScissorBounds? = null,
) : Effect() {
    private var oldState: ScissorState? = null
    private val roundedScissorBounds: ScissorBounds? by lazy {
        if (unroundedScissorBounds == null) {
            return@lazy null
        }
        ScissorBounds(
            unroundedScissorBounds.x1.roundToRealPixels(boundComponent),
            unroundedScissorBounds.y1.roundToRealPixels(boundComponent),
            unroundedScissorBounds.x2.roundToRealPixels(boundComponent),
            unroundedScissorBounds.y2.roundToRealPixels(boundComponent),
        )
    }

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
    ) : this(
        scissorIntersection = scissorIntersection,
        unroundedScissorBounds = ScissorBounds(
            x1.toFloat(),
            y1.toFloat(),
            x2.toFloat(),
            y2.toFloat(),
        ),
    )

    @JvmOverloads
    constructor(
        customBoundingBox: UIComponent? = null,
        scissorIntersection: Boolean = true,
    ) : this(
        customBoundingBox,
        scissorIntersection,
        null,
    )

    override fun beforeDraw(matrixStack: UMatrixStack) {
        val bounds = customBoundingBox?.getScissorBounds() ?: roundedScissorBounds ?: boundComponent.getScissorBounds()
        val resolutionManager = boundComponent.resolutionManager
        val scaleFactor = resolutionManager.scaleFactor.toInt()

        if (currentScissorState == null) {
            glEnable(GL_SCISSOR_TEST)
        }

        oldState = currentScissorState
        val state = oldState

        // TODO ideally we should respect matrixStack offset and maybe scale, though we do not currently care about
        //      global gl state either, so not really important until someone needs it
        var x = (bounds.x1 * scaleFactor).roundToInt()
        var y = resolutionManager.viewportHeight - (bounds.y2 * scaleFactor).roundToInt()
        var width = (bounds.width * scaleFactor).roundToInt()
        var height = (bounds.height * scaleFactor).roundToInt()

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

    override fun afterDraw(matrixStack: UMatrixStack) {
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
        getLeft().roundToRealPixels(this),
        getTop().roundToRealPixels(this),
        getRight().roundToRealPixels(this),
        getBottom().roundToRealPixels(this),
    )

    data class ScissorState(val x: Int, val y: Int, val width: Int, val height: Int)

    private data class ScissorBounds(val x1: Float, val y1: Float, val x2: Float, val y2: Float) {
        val width: Float
            get() = x2 - x1
        val height: Float
            get() = y2 - y1
    }

    companion object {
        var currentScissorState: ScissorState? = null
    }
}
