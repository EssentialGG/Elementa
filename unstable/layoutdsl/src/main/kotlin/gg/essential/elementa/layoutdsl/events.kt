package gg.essential.elementa.layoutdsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.events.UIClickEvent
import gg.essential.elementa.state.v2.toV2
import gg.essential.elementa.util.Tag
import gg.essential.elementa.util.hoverScope
import gg.essential.elementa.util.makeHoverScope
import gg.essential.elementa.util.addTag
import gg.essential.elementa.util.removeTag

import gg.essential.elementa.state.State as StateV1
import gg.essential.elementa.state.v2.State as StateV2

inline fun Modifier.onLeftClick(crossinline callback: UIComponent.() -> Unit) = this then {
    val listener: UIComponent.(event: UIClickEvent) -> Unit = {
        if (it.mouseButton == 0) {
            callback()
        }
    }
    onMouseClick(listener)
    return@then { mouseClickListeners.remove(listener) }
}

/** Declare this component and its children to be in a hover scope. See [makeHoverScope]. */
fun Modifier.hoverScope(state: StateV1<Boolean>? = null) =
    then { makeHoverScope(state); { throw NotImplementedError() } }

/** Declare this component and its children to be in a hover scope. See [makeHoverScope]. */
fun Modifier.hoverScope(state: gg.essential.elementa.state.v2.State<Boolean>) =
    then { makeHoverScope(state); { throw NotImplementedError() } }

/**
 * Replaces the existing hover scope declared on this component with one which simply inherits from the parent scope.
 * Can effectively be used to remove a scope from an otherwise self-contained component to join it with other custom
 * components surrounding it.
 */
fun Modifier.inheritHoverScope() =
    then { makeHoverScope(hoverScope(parentOnly = true)); { throw NotImplementedError() } }

/**
 * Applies [hoverModifier] while the component is hovered, otherwise applies [noHoverModifier] (or nothing by default).
 *
 * Whether a component is considered "hovered" depends solely on whether its [hoverScope] says that it is.
 * It is not necessarily related to whether the mouse cursor is on top of the component (e.g. the label of a button may
 * be considered hovered when the overall button is hovered, even when the cursor isn't on the text itself).
 *
 * A [Modifier.hoverScope] is **require** on the component or one of its parents.
 */
fun Modifier.whenHovered(hoverModifier: Modifier, noHoverModifier: Modifier = Modifier): Modifier =
    then { Modifier.whenTrue(hoverScope(), hoverModifier, noHoverModifier).applyToComponent(this) }

/**
 * Provides the [hoverScope] to be evaluated in a lambda which returns a modifier
 */
fun Modifier.withHoverState(func: (StateV2<Boolean>) -> Modifier) =
    then { func(hoverScope().toV2()).applyToComponent(this) }

/** Applies a Tag to this component. See [UIComponent.addTag]. */
fun Modifier.tag(tag: Tag) = then { addTag(tag); { removeTag(tag) } }
