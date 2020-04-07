package club.sk1er.elementa.dsl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.effects.Effect

fun <T : UIComponent> T.constrain(config: UIConstraints.() -> Unit) = apply {
    getConstraints().config()
}

infix fun <T : UIComponent> T.childOf(parent: UIComponent) = apply {
    parent.addChild(this)
}

infix fun <T : UIComponent> T.effect(effect: Effect) = apply {
    this.enableEffect(effect)
}