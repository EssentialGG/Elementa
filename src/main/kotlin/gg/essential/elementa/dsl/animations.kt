package gg.essential.elementa.dsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.animation.AnimatingConstraints

/**
 * Wrapper around [UIComponent.makeAnimation] and [UIComponent.animateTo],
 * providing a handy dandy DSL.
 */
inline fun <T : UIComponent> T.animate(animation: AnimatingConstraints.() -> Unit) = apply {
    val anim = this.makeAnimation()
    anim.animation()
    this.animateTo(anim)
}
