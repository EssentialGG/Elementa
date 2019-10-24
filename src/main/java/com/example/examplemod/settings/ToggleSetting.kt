package com.example.examplemod.settings

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.effects.StencilEffect
import net.minecraft.client.Minecraft
import java.awt.Color

class ToggleSetting(name: String, description: String) : SettingObject() {
    var toggled = true

    private val drawBox = UIBlock().constrain {
        height = RelativeConstraint(0.9f)
        width = RelativeConstraint()
        color = Color(0, 0, 0, 0).asConstraint()
    }.enableEffects(ScissorEffect()) childOf this

    private val title = UIText(name).constrain {
        x = 3.pixels()
        y = 3.pixels()
        width = PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) * 2f)
        height = 18.pixels()
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf drawBox

    private val text = UIText(description).constrain {
        x = 3.pixels()
        y = 25.pixels()
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf drawBox



    private val toggleBox = UIContainer().constrain() {
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
    } childOf drawBox

    private val toggleBack = UIRoundedRectangle(5f).constrain {
        y = CenterConstraint()
        x = CenterConstraint()
        width = 20.pixels()
        height = 10.pixels()
    }.enableEffects(StencilEffect()) childOf toggleBox

    private val toggleBackOn = UIBlock().constrain {
        width = RelativeConstraint()
        height = RelativeConstraint()
        color = Color(0, 170, 165, 0).asConstraint()
    } childOf toggleBack

    private val toggleBackOff = UIBlock().constrain {
        x = 0.pixels(true)
        height = RelativeConstraint()
        color = Color(120, 120, 120, 0).asConstraint()
    } childOf toggleBack

    private val toggleKnob = SettingKnob()

    init {
        toggleKnob childOf toggleBox
    }

    override fun animateIn() {
        super.animateIn()
        drawBox.constrain { y = 10.pixels() }
        drawBox.animate {
            setYAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
            setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 100).asConstraint())
        }
        toggleKnob.fadeIn()
        animateAlpha(255, title, text, toggleBack, toggleBackOn, toggleBackOff)
    }

    override fun animateOut() {
        super.animateOut()
        drawBox.constrain { y = 0.pixels() }
        drawBox.animate {
            setYAnimation(Animations.OUT_EXP, 0.5f, (-10).pixels())
            setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 0).asConstraint())
        }
        toggleKnob.fadeOut()
        animateAlpha(0, title, text, toggleBack, toggleBackOn, toggleBackOff)
    }

    private fun animateAlpha(alpha: Int, vararg components: UIComponent) {
        var newAlpha = alpha
        components.forEach {
            if (newAlpha < 10 && it is UIText) newAlpha = 10
            val constraint = Color(it.getColor().red, it.getColor().green, it.getColor().blue, newAlpha).asConstraint()
            it.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, constraint)
            }
        }
    }

    private fun toggle() {
        if (!selected) return
        toggleKnob.click(toggled)

        if (toggled) {
            toggled = false
            toggleBackOn.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels()) }
            toggleBackOff.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint()) }

        } else {
            toggled = true
            toggleBackOn.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint()) }
            toggleBackOff.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels()) }

        }
    }

    private fun hover(isHovered: Boolean) {
        if (!selected) return
        toggleKnob.hover(isHovered)
    }
}