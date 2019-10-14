package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.features.ScissorFeature
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
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
        categoryHolder.addChild(createCategory(PixelConstraint(0f), "General"))
        categoryHolder.addChild(createCategory(PixelConstraint(25f), "Position"))
        categoryHolder.addChild(createCategory(PixelConstraint(50f), "Test"))
        categoryHolder.addChild(createCategory(PixelConstraint(75f), "Category"))
        categoryHolder.addChild(createCategory(PixelConstraint(100f), "Woohoo"))

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

    private fun createCategory(offset: PositionConstraint, string: String): UIComponent {
        val container = UIBlock()
                .setY(offset)
                .setX(PixelConstraint(-10f))
                .setHeight(ChildBasedSizeConstraint())

        val text = UIText(string)
                .setWidth(PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string) * 2f))
                .setHeight(PixelConstraint(18f))

        container.enableFeatures(ScissorFeature())

        container.addChild(text)

        container.onHover {
            container.animateTo(container.makeAnimation().setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(10f)))
        }.onUnHover {
            container.animateTo(container.makeAnimation().setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f)))
        }.onClick {
            println(string)
        }

        return container
    }
}