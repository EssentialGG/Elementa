package gg.essential.elementa.utils

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.events.UIClickEvent
import gg.essential.elementa.manager.*
import gg.essential.elementa.manager.DefaultMousePositionManager
import gg.essential.elementa.manager.DefaultResolutionManager
import gg.essential.elementa.manager.KeyboardManager
import gg.essential.elementa.manager.MousePositionManager
import gg.essential.elementa.manager.ResolutionManager
import gg.essential.elementa.state.State
import gg.essential.universal.shader.BlendState
import gg.essential.universal.shader.UShader
import java.awt.Color
import kotlin.math.round
import kotlin.math.sign
import kotlin.reflect.KProperty

@Deprecated("This relies on global states", replaceWith = ReplaceWith("guiHint(roundDown, component)"))
@Suppress("DEPRECATION")
fun Float.guiHint(roundDown: Boolean) = UIComponent.guiHint(this, roundDown)

@Deprecated("This relies on global states", replaceWith = ReplaceWith("guiHint(roundDown, component)"))
@Suppress("DEPRECATION")
fun Double.guiHint(roundDown: Boolean) = UIComponent.guiHint(this, roundDown)

fun Float.guiHint(roundDown: Boolean, component: UIComponent) = UIComponent.guiHint(this, roundDown, component)

fun Double.guiHint(roundDown: Boolean, component: UIComponent) = UIComponent.guiHint(this, roundDown, component)

@Deprecated("This relies on global states", replaceWith = ReplaceWith("roundToRealPixels(component)"))
fun Float.roundToRealPixels(): Float {
    val factor = Window.resolutionManager.scaleFactor.toFloat()
    return round(this * factor).let { if (it == 0f && this != 0f) sign(this) else it } / factor
}

fun Float.roundToRealPixels(component: UIComponent): Float {
    val factor = component.resolutionManager.scaleFactor.toFloat()
    return round(this * factor).let { if (it == 0f && this != 0f) sign(this) else it } / factor
}

@Deprecated("This relies on global states", replaceWith = ReplaceWith("roundToRealPixels(component)"))
fun Double.roundToRealPixels(): Double {
    val factor = Window.resolutionManager.scaleFactor
    return round(this * factor).let { if (it == 0.0 && this != 0.0) sign(this) else it } / factor
}

fun Double.roundToRealPixels(component: UIComponent): Double {
    val factor = component.resolutionManager.scaleFactor
    return round(this * factor).let { if (it == 0.0 && this != 0.0) sign(this) else it } / factor
}

fun Color.withAlpha(alpha: Int) = Color(this.red, this.green, this.blue, alpha)
fun Color.withAlpha(alpha: Float) = Color(this.red, this.green, this.blue, (alpha * 255).toInt())
fun Color.invisible() = withAlpha(0)

operator fun Color.component1() = this.red
operator fun Color.component2() = this.green
operator fun Color.component3() = this.blue
operator fun Color.component4() = this.alpha

internal fun UShader.Companion.readFromLegacyShader(vertName: String, fragName: String, blendState: BlendState) =
    fromLegacyShader(readShader(vertName, "vsh"), readShader(fragName, "fsh"), blendState)
private fun readShader(name: String, ext: String) =
    Window::class.java.getResource("/shaders/$name.$ext").readText()

val UIComponent.window: Window?
    get() = Window.ofOrNull(this)

internal val UIComponent.resolutionManager: ResolutionManager
    get() = this.window?.resolutionManager ?: DefaultResolutionManager

internal val UIComponent.mousePositionManager: MousePositionManager
    get() = this.window?.mousePositionManager ?: DefaultMousePositionManager

internal val UIComponent.keyboardManager: KeyboardManager
    get() = this.window?.keyboardManager ?: DefaultKeyboardManager

inline fun UIComponent.onLeftClick(crossinline method: UIComponent.(event: UIClickEvent) -> Unit) = onMouseClick {
    if (it.mouseButton == 0) {
        this.method(it)
    }
}

fun <T> State<T>.onSetValueAndNow(listener: (T) -> Unit) = onSetValue(listener).also { listener(get()) }

operator fun <T> State<T>.getValue(obj: Any, property: KProperty<*>): T = get()
operator fun <T> State<T>.setValue(obj: Any, property: KProperty<*>, value: T) = set(value)
