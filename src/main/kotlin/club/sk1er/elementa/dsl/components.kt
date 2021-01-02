package club.sk1er.elementa.dsl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.effects.Effect
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

fun <T : UIComponent> T.constrain(config: UIConstraints.() -> Unit) = apply {
    constraints.config()
}

infix fun <T : UIComponent> T.childOf(parent: UIComponent) = apply {
    parent.addChild(this)
}

infix fun <T : UIComponent> T.effect(effect: Effect) = apply {
    this.enableEffect(effect)
}

operator fun <T : UIComponent> T.provideDelegate(
    thisRef: Any?,
    property: KProperty<*>
) = Delegates.observable(this.also { componentName = property.name }) { _, _, value ->
    value.componentName = property.name
}
