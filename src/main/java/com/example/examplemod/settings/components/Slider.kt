package com.example.examplemod.settings.components

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.StencilEffect
import java.awt.Color

class Slider : UIContainer() {
    private var grabbed = false
    private val value = 0.5f

    private val slide = UIRoundedRectangle(1f).constrain {
        y = CenterConstraint()
        width = RelativeConstraint()
        height = 2.pixels()
        color = Color(120, 120, 120, 0).asConstraint()
    }.enableEffect(StencilEffect()) childOf this

    private val slideBackground = UIBlock().constrain {
        width = RelativeConstraint()
        height = RelativeConstraint()
        color = Color(0, 170, 165, 0).asConstraint()
    } childOf slide

    private val knob = Knob(10)

    init {
        knob.constrain {
            x = 0.pixels(true)
        }.onMouseEnter {
            if (!grabbed)
            knob.hover()
        }.onMouseLeave {
            if (!grabbed)
            knob.unHover()
        }.onMouseClick { _, _, _ ->
            knob.grab()
            grabbed = true
        }.onMouseRelease {
            if (!grabbed) return@onMouseRelease
            knob.release()
            grabbed = false
        } childOf this

        onMouseDrag { mouseX, _, _ ->
            if (!grabbed) return@onMouseDrag
            knob.animate {
                setXAnimation(Animations.OUT_EXP, 0.5f, MaxPositionConstraint(MinPositionConstraint((mouseX - 5).pixels(), 0.pixels()), 0.pixels(true)))
            }
        }

        constrain {
            x = CenterConstraint()
            y = CramSiblingConstraint()
            width = RelativeConstraint(0.75f)
            height = 20.pixels()
        }
    }

    fun fadeIn() {
        knob.fadeIn()
        slide.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 120, 255).asConstraint()) }
        slideBackground.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 170, 165, 255).asConstraint()) }
    }

    fun fadeOut() {
        knob.fadeOut()
        slide.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 120, 0).asConstraint()) }
        slideBackground.animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 170, 165, 0).asConstraint()) }
    }
}