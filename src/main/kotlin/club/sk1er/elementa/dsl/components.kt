package club.sk1er.elementa.dsl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.effects.Effect

fun UIComponent.constrain(config: UIConstraints.() -> Unit) = apply {
    getConstraints().config()
}

infix fun UIComponent.childOf(parent: UIComponent) = apply {
    parent.addChild(this)
}

infix fun UIComponent.effect(effect: Effect) = apply {
    this.enableEffect(effect)
}