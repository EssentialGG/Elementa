package gg.essential.elementa.utils

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.effects.Effect
import gg.essential.elementa.events.UIClickEvent
import gg.essential.elementa.manager.*
import gg.essential.elementa.manager.DefaultMousePositionManager
import gg.essential.elementa.manager.DefaultResolutionManager
import gg.essential.elementa.manager.KeyboardManager
import gg.essential.elementa.manager.MousePositionManager
import gg.essential.elementa.manager.ResolutionManager
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.universal.UMouse
import gg.essential.universal.UResolution
import gg.essential.universal.shader.BlendState
import gg.essential.universal.shader.UShader
import org.jetbrains.annotations.ApiStatus
import java.awt.Color
import kotlin.math.round
import kotlin.math.sign
import kotlin.reflect.KProperty

//@Deprecated("This relies on global states", replaceWith = ReplaceWith("guiHint(roundDown, component)"))
@Suppress("DEPRECATION")
fun Float.guiHint(roundDown: Boolean) = UIComponent.guiHint(this, roundDown)

//@Deprecated("This relies on global states", replaceWith = ReplaceWith("guiHint(roundDown, component)"))
@Suppress("DEPRECATION")
fun Double.guiHint(roundDown: Boolean) = UIComponent.guiHint(this, roundDown)

fun Float.guiHint(roundDown: Boolean, component: UIComponent) = UIComponent.guiHint(this, roundDown, component)

fun Double.guiHint(roundDown: Boolean, component: UIComponent) = UIComponent.guiHint(this, roundDown, component)

//@Deprecated("This relies on global states", replaceWith = ReplaceWith("roundToRealPixels(component)"))
fun Float.roundToRealPixels(): Float {
    val factor = Window.resolutionManager.scaleFactor.toFloat()
    return round(this * factor).let { if (it == 0f && this != 0f) sign(this) else it } / factor
}

fun Float.roundToRealPixels(component: UIComponent): Float {
    val factor = component.resolutionManager.scaleFactor.toFloat()
    return round(this * factor).let { if (it == 0f && this != 0f) sign(this) else it } / factor
}

//@Deprecated("This relies on global states", replaceWith = ReplaceWith("roundToRealPixels(component)"))
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
    get() = window?.resolutionManager ?: DefaultResolutionManager

internal val UIComponent.mousePositionManager: MousePositionManager
    get() = window?.mousePositionManager ?: DefaultMousePositionManager

internal val UIComponent.keyboardManager: KeyboardManager
    get() = window?.keyboardManager ?: DefaultKeyboardManager

inline fun UIComponent.onLeftClick(crossinline method: UIComponent.(event: UIClickEvent) -> Unit) = onMouseClick {
    if (it.mouseButton == 0) {
        this.method(it)
    }
}

fun <T> State<T>.onSetValueAndNow(listener: (T) -> Unit) = onSetValue(listener).also { listener(get()) }

operator fun <T> State<T>.getValue(obj: Any, property: KProperty<*>): T = get()
operator fun <T> State<T>.setValue(obj: Any, property: KProperty<*>, value: T) = set(value)

fun State<String>.empty() = map { it.isBlank() }
operator fun State<Boolean>.not() = map { !it }
infix fun State<Boolean>.and(other: State<Boolean>) = zip(other).map { (a, b) -> a && b }
infix fun State<Boolean>.or(other: State<Boolean>) = zip(other).map { (a, b) -> a || b }

@ApiStatus.Internal
fun <T : UIComponent> T.bindParent(
    parent: UIComponent,
    state: State<Boolean>,
    delayed: Boolean = false,
    index: Int? = null
) =
    bindParent(state.map {
        if (it) parent else null
    }, delayed, index)

@ApiStatus.Internal
fun <T : UIComponent> T.bindParent(state: State<UIComponent?>, delayed: Boolean = false, index: Int? = null) = apply {
    state.onSetValueAndNow { parent ->
        val handleStateUpdate = {
            if (this.hasParent && this.parent != parent) {
                this.parent.removeChild(this)
            }
            if (parent != null && this !in parent.children) {
                if (index != null) {
                    parent.insertChildAt(this, index)
                } else {
                    parent.addChild(this)
                }
            }
        }
        if (delayed) {
            Window.enqueueRenderOperation {
                handleStateUpdate()
            }
        } else {
            handleStateUpdate()
        }
    }
}

/**
 * Executes the supplied [block] on this component's animationFrame
 */
private fun UIComponent.onAnimationFrame(block: () -> Unit) =
    enableEffect(object : Effect() {
        override fun animationFrame() {
            block()
        }
    })


/**
 * Returns a state representing whether this UIComponent is hovered
 *
 * [hitTest] will perform a hit test to make sure the user is actually hovered over this component
 * as compared to the mouse just being within its content bounds while being hovered over another
 * component rendered above this.
 *
 * [layoutSafe] will delay the state change until a time in which it is safe to make layout changes.
 * This option will induce an additional delay of one frame because the state is updated during the next
 * [Window.enqueueRenderOperation] after the hoverState changes.
 */
fun UIComponent.hoveredState(hitTest: Boolean = true, layoutSafe: Boolean = true): State<Boolean> {
    // "Unsafe" means that it is not safe to depend on this for layout changes
    val unsafeHovered = BasicState(false)

    // "Safe" because layout changes can directly happen when this changes (ie in onSetValue)
    val safeHovered = BasicState(false)

    // Performs a hit test based on the current mouse x / y
    fun hitTestHovered(): Boolean {
        // Positions the mouse in the center of pixels so isPointInside will
        // pass for items 1 pixel wide objects. See ElementaVersion v2 for more details
        val halfPixel = 0.5f / UResolution.scaleFactor.toFloat()
        val mouseX = UMouse.Scaled.x.toFloat() + halfPixel
        val mouseY = UMouse.Scaled.y.toFloat() + halfPixel
        return if (isPointInside(mouseX, mouseY)) {

            val window = Window.of(this)
            val hit = (window.hoveredFloatingComponent?.hitTest(mouseX, mouseY)) ?: window.hitTest(mouseX, mouseY)

            hit.isComponentInParentChain(this) || hit == this
        } else {
            false
        }
    }

    if (hitTest) {
        // It's possible the animation framerate will exceed that of the actual frame rate
        // Therefore, in order to avoid redundantly performing the hit test multiple times
        // in the same frame, this boolean is used to ensure that hit testing is performed
        // at most only a single time each frame
        var registerHitTest = true

        onAnimationFrame {
            if (registerHitTest) {
                registerHitTest = false
                Window.enqueueRenderOperation {
                    // The next animation frame should register another renderOperation
                    registerHitTest = true

                    // Since enqueueRenderOperation will keep polling the queue until there are no more items,
                    // the forwarding of any update to the safeHovered state will still happen this frame
                    unsafeHovered.set(hitTestHovered())
                }
            }
        }
    }
    onMouseEnter {
        if (hitTest) {
            unsafeHovered.set(hitTestHovered())
        } else {
            unsafeHovered.set(true)
        }
    }

    onMouseLeave {
        unsafeHovered.set(false)
    }

    return if (layoutSafe) {
        unsafeHovered.onSetValue {
            Window.enqueueRenderOperation {
                safeHovered.set(it)
            }
        }
        safeHovered
    } else {
        unsafeHovered
    }
}

private fun UIComponent.isComponentInParentChain(target: UIComponent): Boolean {
    var component: UIComponent = this
    while (component.hasParent && component !is Window) {
        component = component.parent
        if (component == target)
            return true
    }

    return false
}