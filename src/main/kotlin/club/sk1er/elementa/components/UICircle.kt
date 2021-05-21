package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.dsl.toConstraint
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.shaders.FloatUniform
import club.sk1er.elementa.shaders.Shader
import club.sk1er.elementa.shaders.Vec2Uniform
import club.sk1er.elementa.utils.Vector2f
import gg.essential.universal.UGraphics
import java.awt.Color

class UICircle @JvmOverloads constructor(radius: Float = 0f, color: Color = Color.WHITE, var steps: Int = 40) :
    UIComponent() {
    init {
        setColor(color.toConstraint())
        setRadius(radius.pixels())
    }

    override fun getLeft(): Float {
        return constraints.getX() - getRadius()
    }

    override fun getTop(): Float {
        return constraints.getY() - getRadius()
    }

    override fun getWidth(): Float {
        return getRadius() * 2
    }

    override fun getHeight(): Float {
        return getRadius() * 2
    }

    override fun isPositionCenter(): Boolean {
        return true
    }

    override fun draw() {
        beforeDraw()

        val x = constraints.getX()
        val y = constraints.getY()
        val r = getRadius()

        val color = getColor()
        if (color.alpha == 0) return super.draw()

        drawCircle(x, y, r, color)

        super.draw()
    }

    companion object {
        private lateinit var shader: Shader
        private lateinit var shaderRadiusUniform: FloatUniform
        private lateinit var shaderCenterPositionUniform: Vec2Uniform

        fun initShaders() {
            if (::shader.isInitialized)
                return

            shader = Shader("rect", "circle")
            shaderRadiusUniform = FloatUniform(shader.getUniformLocation("u_Radius"))
            shaderCenterPositionUniform = Vec2Uniform(shader.getUniformLocation("u_CenterPos"))
        }

        fun drawCircle(centerX: Float, centerY: Float, radius: Float, color: Color) {
            if (!::shader.isInitialized)
                return

            UGraphics.pushMatrix()

            shader.bindIfUsable()
            shaderRadiusUniform.setValue(radius)
            shaderCenterPositionUniform.setValue(Vector2f(centerX, centerY))

            UIBlock.drawBlock(
                color,
                (centerX - radius).toDouble(),
                (centerY - radius).toDouble(),
                (centerX + radius).toDouble(),
                (centerY + radius).toDouble()
            )

            shader.unbindIfUsable()

            UGraphics.popMatrix()
        }
    }
}
