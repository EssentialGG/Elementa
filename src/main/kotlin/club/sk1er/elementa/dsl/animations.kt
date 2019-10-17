package club.sk1er.elementa.dsl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.animation.AnimatingConstraints

/**
 * Wrapper around [UIComponent.makeAnimation] and [UIComponent.animateTo],
 * providing a handy dandy DSL.
 */
fun UIComponent.animate(animation: AnimatingConstraints.() -> Unit) = apply {
    val anim = this.makeAnimation()
    anim.animation()
    this.animateTo(anim)
}