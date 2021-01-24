package club.sk1er.elementa.utils

import club.sk1er.elementa.UIComponent
import java.awt.Color

fun Float.guiHint() = UIComponent.guiHint(this)
fun Double.guiHint() = UIComponent.guiHint(this)

fun Color.withAlpha(alpha: Int) = Color(this.red, this.green, this.blue, alpha)
fun Color.withAlpha(alpha: Float) = Color(this.red, this.green, this.blue, (alpha * 255).toInt())
fun Color.invisible() = withAlpha(0)

operator fun Color.component1() = this.red
operator fun Color.component2() = this.green
operator fun Color.component3() = this.blue
operator fun Color.component4() = this.alpha

