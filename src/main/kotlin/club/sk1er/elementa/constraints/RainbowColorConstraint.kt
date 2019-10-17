package club.sk1er.elementa.constraints

import club.sk1er.elementa.UIComponent
import net.minecraft.util.MathHelper
import java.awt.Color
import kotlin.math.sin
import kotlin.random.Random

class RainbowColorConstraint(private val alpha: Int = 255, private val speed: Float = 50f) : ColorConstraint {
    override var cachedValue = Color.WHITE
    override var recalculate = true

    private var currentColor: Color = Color.WHITE
    private var currentStep = Random.nextInt(500)

    override fun getColorImpl(component: UIComponent, parent: UIComponent): Color {
        return currentColor
    }

    override fun animationFrame() {
        currentStep++

        val red = ((sin((currentStep / speed).toDouble()) + 0.75) * 170).toInt()
        val green = ((sin(currentStep / speed + 2 * Math.PI / 3) + 0.75) * 170).toInt()
        val blue = ((sin(currentStep / speed + 4 * Math.PI / 3) + 0.75) * 170).toInt()

        currentColor = Color(
            MathHelper.clamp_int(red, 0, 255),
            MathHelper.clamp_int(green, 0, 255),
            MathHelper.clamp_int(blue, 0, 255),
            MathHelper.clamp_int(alpha, 0, 255)
        )
    }
}