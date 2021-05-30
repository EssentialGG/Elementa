package gg.essential.elementa.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.shaders.FloatUniform
import gg.essential.elementa.shaders.Shader
import gg.essential.elementa.shaders.Vec4Uniform
import gg.essential.elementa.utils.Vector4f
import gg.essential.universal.UMatrixStack
import java.awt.Color

/**
 * Draws a rounded rectangle
 */
open class UIRoundedRectangle(radius: Float) : UIComponent() {
    init {
        setRadius(radius.pixels())
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val radius = getRadius()

        val color = getColor()
        if (color.alpha != 0)
            drawRoundedRectangle(matrixStack, getLeft(), getTop(), getRight(), getBottom(), radius, color)

        super.draw(matrixStack)
    }

    companion object {
        private lateinit var shader: Shader
        private lateinit var shaderRadiusUniform: FloatUniform
        private lateinit var shaderInnerRectUniform: Vec4Uniform

        fun initShaders() {
            if (::shader.isInitialized)
                return

            shader = Shader("rect", "rounded_rect")
            shaderRadiusUniform = FloatUniform(shader.getUniformLocation("u_Radius"))
            shaderInnerRectUniform = Vec4Uniform(shader.getUniformLocation("u_InnerRect"))
        }

        @Deprecated(
            UMatrixStack.Compat.DEPRECATED,
            ReplaceWith("drawRoundedRectangle(matrixStack, left, top, right, bottom, radius, color)"),
        )
        fun drawRoundedRectangle(left: Float, top: Float, right: Float, bottom: Float, radius: Float, color: Color) =
            drawRoundedRectangle(UMatrixStack(), left, top, right, bottom, radius, color)

        fun drawRoundedRectangle(matrixStack: UMatrixStack, left: Float, top: Float, right: Float, bottom: Float, radius: Float, color: Color) {
            if (!::shader.isInitialized)
                return

            shader.bindIfUsable()
            shaderRadiusUniform.setValue(radius)
            shaderInnerRectUniform.setValue(Vector4f(left + radius, top + radius, right - radius, bottom - radius))

            UIBlock.drawBlock(matrixStack, color, left.toDouble(), top.toDouble(), right.toDouble(), bottom.toDouble())

            shader.unbindIfUsable()
        }
    }
}
