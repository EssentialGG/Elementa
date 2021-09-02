package gg.essential.elementa.components

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.State
import gg.essential.elementa.state.toConstraint
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Extremely simple component that simply draws a colored rectangle.
 */
open class UIBlock(colorConstraint: ColorConstraint = Color.WHITE.toConstraint()) : UIComponent() {
    constructor(color: Color) : this(color.toConstraint())

    constructor(colorState: State<Color>) : this(colorState.toConstraint())

    init {
        setColor(colorConstraint)
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val x2 = this.getRight().toDouble()
        val y2 = this.getBottom().toDouble()

        val color = getColor()
        if (color.alpha == 0)
            return super.draw(matrixStack)

        drawBlock(matrixStack, color, x, y, x2, y2)

        super.draw(matrixStack)
    }

    companion object {
        @Deprecated(
            "Pass UMatrixStack as first argument, required for 1.17",
            ReplaceWith("drawBlock(matrixStack, color, x1, y1, x2, y2)")
        )
        fun drawBlock(color: Color, x1: Double, y1: Double, x2: Double, y2: Double) =
            drawBlock(UMatrixStack(), color, x1, y1, x2, y2)

        fun drawBlock(matrixStack: UMatrixStack, color: Color, x1: Double, y1: Double, x2: Double, y2: Double) {
            UGraphics.enableBlend()
            UGraphics.tryBlendFuncSeparate(770, 771, 1, 0)

            val buffer = UGraphics.getFromTessellator()
            buffer.beginWithDefaultShader(UGraphics.DrawMode.QUADS, DefaultVertexFormats.POSITION_COLOR)
            drawBlock(buffer, matrixStack, color, x1, y1, x2, y2)

            UGraphics.disableBlend()
        }

        fun drawBlockWithActiveShader(matrixStack: UMatrixStack, color: Color, x1: Double, y1: Double, x2: Double, y2: Double) {
            val buffer = UGraphics.getFromTessellator()
            buffer.beginWithActiveShader(UGraphics.DrawMode.QUADS, DefaultVertexFormats.POSITION_COLOR)
            drawBlock(buffer, matrixStack, color, x1, y1, x2, y2)
        }

        private fun drawBlock(worldRenderer: UGraphics, matrixStack: UMatrixStack, color: Color, x1: Double, y1: Double, x2: Double, y2: Double) {
            val red = color.red.toFloat() / 255f
            val green = color.green.toFloat() / 255f
            val blue = color.blue.toFloat() / 255f
            val alpha = color.alpha.toFloat() / 255f

            worldRenderer.pos(matrixStack, x1, y2, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(matrixStack, x2, y2, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(matrixStack, x2, y1, 0.0).color(red, green, blue, alpha).endVertex()
            worldRenderer.pos(matrixStack, x1, y1, 0.0).color(red, green, blue, alpha).endVertex()

            if (ElementaVersion.active >= ElementaVersion.v1) {
                // At some point MC started enabling its depth test during font rendering but all GUI code is
                // essentially flat and has depth tests disabled. This can cause stuff rendered in the background of the
                // GUI to interfere with text rendered in the foreground because none of the blocks rendered in between
                // will actually write to the depth buffer.
                // So that's what we're doing, resetting the depth buffer in the area where we draw the block.
                UGraphics.enableDepth()
                UGraphics.depthFunc(GL11.GL_ALWAYS)
                worldRenderer.drawDirect()
                UGraphics.disableDepth()
                UGraphics.depthFunc(GL11.GL_LEQUAL)
            } else {
                worldRenderer.drawDirect()
            }
        }

        @Deprecated(
            "Pass UMatrixStack as first argument, required for 1.17",
            ReplaceWith("drawBlock(matrixStack, color, x1, y1, x2, y2)")
        )
        fun drawBlockSized(color: Color, x: Double, y: Double, width: Double, height: Double) =
            drawBlockSized(UMatrixStack(), color, x, y, width, height)

        fun drawBlockSized(matrixStack: UMatrixStack, color: Color, x: Double, y: Double, width: Double, height: Double) {
            drawBlock(matrixStack, color, x, y, x + width, y + height)
        }
    }
}
