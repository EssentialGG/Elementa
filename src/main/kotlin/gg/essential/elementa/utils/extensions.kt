package gg.essential.elementa.utils

import gg.essential.elementa.UIComponent
import gg.essential.universal.UResolution
import java.awt.Color
import kotlin.math.round

fun Float.guiHint(roundDown: Boolean) = UIComponent.guiHint(this, roundDown)
fun Double.guiHint(roundDown: Boolean) = UIComponent.guiHint(this, roundDown)

fun Float.roundToRealPixels(): Float {
    val factor = UResolution.scaleFactor.toFloat()
    return round(this * factor) / factor
}
fun Double.roundToRealPixels(): Double {
    val factor = UResolution.scaleFactor
    return round(this * factor) / factor
}

fun Color.withAlpha(alpha: Int) = Color(this.red, this.green, this.blue, alpha)
fun Color.withAlpha(alpha: Float) = Color(this.red, this.green, this.blue, (alpha * 255).toInt())
fun Color.invisible() = withAlpha(0)

operator fun Color.component1() = this.red
operator fun Color.component2() = this.green
operator fun Color.component3() = this.blue
operator fun Color.component4() = this.alpha

