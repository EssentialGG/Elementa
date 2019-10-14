package club.sk1er.elementa.dsl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints

fun UIComponent.constrain(config: UIConstraints.() -> Unit) = apply {
    getConstraints().config()
}

infix fun UIComponent.childOf(parent: UIComponent) = apply {
    parent.addChild(this)
}