package com.example.examplemod.settings

import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import net.minecraft.client.Minecraft
import java.awt.Color

class SettingDivider(name: String) : SettingObject() {
    private val container = UIContainer().constrain {
        x = CenterConstraint()
        width = ChildBasedSizeConstraint()
        height = 25.pixels()
    } childOf this

    private val title = UIText(name).constrain {
        width = PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) * 2f)
        height = 18.pixels()
        color = Color(255, 255, 255, 10).asConstraint()
    } childOf container

    override fun animateIn() {
        title.constrain { y = CenterConstraint() + 10.pixels() }
        title.animate {
            setYAnimation(Animations.OUT_EXP, 0.5f, CenterConstraint())
            setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 255).asConstraint())
        }
    }

    override fun animateOut() {
        title.constrain { y = CenterConstraint() }
        title.animate {
            setYAnimation(Animations.OUT_EXP, 0.5f, CenterConstraint() + (-10).pixels())
            setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint())
        }
    }
}