package com.example.examplemod

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.ConstantColorConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.features.ScissorFeature
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class SettingsGui : GuiScreen() {
    private val window = Window()

    init {
        val categories = UIBlock()
        categories.getConstraints()
                .setX(PixelConstraint(-window.getWidth() / 3))
                .setWidth(RelativeConstraint(1 / 3f))
                .setHeight(RelativeConstraint(1f))
                .setColor(ConstantColorConstraint(Color(0, 0, 0, 150)))

        val categoryTitle = UIBlock()
        categoryTitle.getConstraints()
                .setX(CenterConstraint())
                .setY(PixelConstraint(10f))
                .setWidth(PixelConstraint(0f))
                .setHeight(PixelConstraint(18f))
                .setColor(ConstantColorConstraint(Color(0, 0, 0, 100)))
        val categoryText = UIText("Settings")
        categoryTitle.addChild(categoryText)
        categories.addChild(categoryTitle)
        categoryTitle.enableFeatures(ScissorFeature())


        val settings = UIBlock()
        settings.getConstraints()
                .setX(PixelConstraint(-window.getWidth() * 2 / 3, true))
                .setWidth(RelativeConstraint(2 / 3f))
                .setHeight(RelativeConstraint(1f))
                .setColor(ConstantColorConstraint(Color(0, 0, 0, 100)))

        window.addChildren(categories, settings)

        ////////////////
        // ANIMATIONS //
        ////////////////


        settings.animateTo(settings.makeAnimation().setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f, true)))

        categories.animateTo(
                categories.makeAnimation()
                        .setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f))
                        .onComplete {
                            categoryTitle.animateTo(
                                    categoryTitle.makeAnimation()
                                            .setWidthAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(100f))
                            )
                        }
        )
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }
}