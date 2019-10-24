package com.example.examplemod.settings.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.StencilEffect
import java.awt.Color

class Toggle : UIComponent() {
    private var toggled = true
    private var selected = false

    private val slide = UIRoundedRectangle(5f).constrain {
        y = CenterConstraint()
        x = CenterConstraint()
        width = 20.pixels()
        height = 10.pixels()
    }.enableEffects(StencilEffect()) childOf this

    private val slideOn = UIBlock().constrain {
        width = RelativeConstraint()
        height = RelativeConstraint()
        color = Color(0, 170, 165, 0).asConstraint()
    } childOf slide

    private val slideOff = UIBlock().constrain {
        x = 0.pixels(true)
        height = RelativeConstraint()
        color = Color(120, 120, 120, 0).asConstraint()
    } childOf slide

    private val knob = Knob()

    init {
        constrain {
            x = 10.pixels(true)
            y = CenterConstraint()
            width = 30.pixels()
            height = 20.pixels()
        }.onClick {
            toggle()
        }.onHover {
            hover(true)
        }.onUnHover {
            hover(false)
        }

        knob childOf this
    }

    fun fadeIn() {
        selected = true
        slide.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 255).asConstraint()) }
        slideOn.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 170, 165, 255).asConstraint()) }
        slideOff.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 120, 255).asConstraint()) }
        knob.fadeIn()
    }

    fun fadeOut() {
        selected = false
        slide.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 0).asConstraint()) }
        slideOn.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 170, 165, 0).asConstraint()) }
        slideOff.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 120, 0).asConstraint()) }
        knob.fadeOut()
    }

    private fun toggle() {
        if (!selected) return
        knob.click(toggled)

        if (toggled) {
            toggled = false
            slideOn.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels()) }
            slideOff.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint()) }

        } else {
            toggled = true
            slideOn.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint()) }
            slideOff.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels()) }

        }
    }

    private fun hover(isHovered: Boolean) {
        if (!selected) return
        knob.hover(isHovered)
    }
}