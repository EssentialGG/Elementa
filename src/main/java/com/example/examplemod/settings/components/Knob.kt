package com.example.examplemod.settings.components

import club.sk1er.elementa.components.UICircle
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import java.awt.Color

class Knob : UIContainer() {
    private val hover = UICircle().constrain {
        x =7f.pixels()
        y = CenterConstraint()
        width = 14.pixels()
        color = Color(0, 255, 250, 0).asConstraint()
    } childOf this

    private val click = UICircle().constrain {
        x =7f.pixels()
        y = CenterConstraint()
        width = 14.pixels()
        color = Color(255, 255, 255, 0).asConstraint()
    } childOf this

    private val knob = UICircle().constrain {
        x = 7f.pixels()
        y = CenterConstraint()
        width = 14.pixels()
        color = Color(0, 210, 205, 0).asConstraint()
    } childOf this

    init {
        constrain {
            x = 0f.pixels(true)
            y = CenterConstraint()
            width = 14.pixels()
            height = 14.pixels()
        }
    }

    fun fadeIn() {
        hover.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 255, 250, 50).asConstraint()) }
        knob.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 210, 205, 255).asConstraint()) }
    }

    fun fadeOut() {
        hover.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 255, 250, 0).asConstraint()) }
        knob.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 210, 205, 0).asConstraint()) }
    }

    fun hover(isHovered: Boolean) {
        if (isHovered) {
            hover.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 25f.pixels()) }
        } else {
            hover.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 14f.pixels()) }
        }
    }

    fun click(toggled: Boolean) {
        click.constrain {
            width = 14.pixels()
            color = Color(255, 255, 255, 75).asConstraint()
        }
        click.animate {
            setWidthAnimation(Animations.OUT_EXP, 0.5f, 25.pixels())
            setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 0).asConstraint())
        }

        if (toggled) {
            animate { setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels()) }
            hover.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 50).asConstraint()) }
            knob.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(164, 164, 164, 255).asConstraint()) }
        } else {
            animate { setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(true)) }
            hover.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 255, 250, 50).asConstraint()) }
            knob.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 210, 205).asConstraint()) }
        }
    }
}