package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.features.ScissorFeature
import club.sk1er.elementa.helpers.Padding
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.init.Items
import java.awt.Color

class SettingsGui : GuiScreen() {
    private val window = Window()

    init {
        val categories = UIBlock()
                .setX(PixelConstraint(-window.getWidth() / 3))
                .setWidth(RelativeConstraint(1 / 3f))
                .setHeight(RelativeConstraint(1f))
                .setColor(ConstantColorConstraint(Color(0, 0, 0, 150)))

        val categoryTitle = UIBlock()
                .setX(CenterConstraint())
                .setY(PixelConstraint(10f))
                .setWidth(PixelConstraint(0f))
                .setHeight(PixelConstraint(36f))

        val categoryText = UIText("Settings")
                .setWidth(PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth("Settings") * 4f))
                .setHeight(PixelConstraint(36f))
        categoryTitle.addChild(categoryText)
        categoryTitle.enableFeatures(ScissorFeature())

        categories.addChild(categoryTitle)

        val categoryHolder = UIContainer()
                .setX(CenterConstraint())
                .setY(PixelConstraint(50f))
                .setWidth(RelativeConstraint(0.9f))
        categoryHolder.addChild(Category("General").getComponent())
        categoryHolder.addChild(Category("Position").getComponent())
        categoryHolder.addChild(Category("Test").getComponent())
        categoryHolder.addChild(Category("Category").getComponent())
        categoryHolder.addChild(Category("Woohoo").getComponent())

        val categoryAnimation = UIContainer()

        categories.addChildren(categoryHolder, categoryAnimation)

        val settings = UIBlock()
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
                            recursiveAnimate(categoryAnimation, categoryHolder, 0)
                            categoryTitle.animateTo(
                                    categoryTitle.makeAnimation()
                                            .setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint( 2f))
                            )
                        }
        )
    }

    private fun recursiveAnimate(handler: UIComponent, holder: UIComponent, index: Int) {
        if (index == holder.children.size) return

        val child = holder.children[index]
        handler.animateTo(
                handler.makeAnimation()
                        .setYAnimation(Animations.LINEAR, 0.2f, PixelConstraint(child.getTop()))
                        .onComplete {
                            child.animateTo(
                                    child.makeAnimation()
                                            .setWidthAnimation(Animations.OUT_QUAD, 0.5f, RelativeConstraint(1f))
                                            .setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f))
                            )
                            recursiveAnimate(handler, holder, index + 1)
                        }
        )
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        window.click()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }

    class Category(string: String) {
        private val container = UIBlock()
                .setY(SiblingConstraint(Padding(8f)))
                .setX(PixelConstraint(-10f))
                .setHeight(ChildBasedSizeConstraint())

        private val text = UIText(string)
                .setWidth(PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string) * 2f))
                .setHeight(PixelConstraint(18f))

        init {
            container.enableFeatures(ScissorFeature())

            container.onHover {
                text.animateTo(text.makeAnimation().setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(10f)))
            }.onUnHover {
                text.animateTo(text.makeAnimation().setXAnimation(Animations.OUT_BOUNCE, 0.5f, PixelConstraint(0f)))
            }.onClick {
                println(Items.string)
            }

            container.addChild(text)
        }

        fun getComponent() = container
    }
}