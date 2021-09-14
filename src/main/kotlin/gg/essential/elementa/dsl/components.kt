package gg.essential.elementa.dsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.effects.Effect
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

inline infix fun <T : UIComponent> T.constrain(config: UIConstraints.() -> Unit) = apply {
    constraints.config()
}

inline infix fun <T : UIComponent> T.addChild(child: T.() -> UIComponent) = apply {
    addChild(child())
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
