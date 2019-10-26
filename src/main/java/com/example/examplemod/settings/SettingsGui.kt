package com.example.examplemod.settings

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import java.awt.Color

class SettingsGui : GuiScreen() {
    private val window = Window()

    init {
        val categories = UIBlock().constrain {
            x = PixelConstraint(-window.getWidth() / 3)
            width = RelativeConstraint(1 / 3f)
            height = RelativeConstraint()
            color = Color(0, 0, 0, 150).asConstraint()
        } childOf window

        val categoryTitle = UIBlock().constrain {
            y = 10.pixels()
            x = CenterConstraint()
            width = 0.pixels()
            height = 36.pixels()
        }.enableEffects(ScissorEffect()) childOf categories

        UIText("Settings").constrain {
            width = MaxSizeConstraint(PixelConstraint("Settings".width() * 4f), RelativeConstraint().constrainTo(categories))
            height = 36.pixels()
        } childOf categoryTitle

        val categoryHolder = UIBlock().constrain {
            x = CenterConstraint()
            y = 50.pixels()
            width = RelativeConstraint(0.9f)
            height = ChildBasedSizeConstraint()
        } childOf categories

        val settingsBox = UIBlock().constrain {
            x = PixelConstraint(-window.getWidth() * 2 / 3, true)
            width = RelativeConstraint(2 / 3f)
            height = RelativeConstraint()
            color = Color(0, 0, 0, 100).asConstraint()
        } childOf window

        Category("General", settingsBox)
                .addSetting(SettingDivider("General Settings"))
                .addSetting(ToggleSetting("General 1", "This toggles something"))
                .addSetting(SliderSetting("General 2", "This changes a value"))
                .addSetting(ToggleSetting("General 3", "This toggles something"))
                .addSetting(ToggleSetting("General 4", "This toggles something"))
                .addSetting(SettingDivider("Category"))
                .addSetting(ToggleSetting("General 5", "This toggles something"))
                .addSetting(ToggleSetting("General 6", "This toggles something"))
                .addSetting(ToggleSetting("General 7", "This toggles something"))
                .addSetting(ToggleSetting("General 8", "This toggles something"))
                .addSetting(ToggleSetting("General 9", "This toggles something"))
                .childOf(categoryHolder)
        Category("Position", settingsBox)
                .addSetting(SettingDivider("Position Settings"))
                .addSetting(ToggleSetting("Position 1", "This toggles something"))
                .addSetting(ToggleSetting("Position 2", "This toggles something"))
                .addSetting(ToggleSetting("Position 3", "This toggles something"))
                .childOf(categoryHolder)
        Category("Test", settingsBox)
                .addSetting(SettingDivider("Test Settings"))
                .addSetting(ToggleSetting("Test 1", "This toggles something"))
                .addSetting(ToggleSetting("Test 2", "This toggles something"))
                .addSetting(ToggleSetting("Test 3", "This toggles something"))
                .addSetting(ToggleSetting("Test 4", "This toggles something"))
                .addSetting(ToggleSetting("Test 5", "This toggles something"))
                .addSetting(ToggleSetting("Test 6", "This toggles something"))
                .childOf(categoryHolder)
        Category("Category", settingsBox)
                .addSetting(ToggleSetting("Category 1", "This toggles something"))
                .addSetting(ToggleSetting("Category 2", "This toggles something"))
                .childOf(categoryHolder)
        Category("Woohoo", settingsBox)
                .addSetting(ToggleSetting("Woohoo 1", "This toggles something"))
                .addSetting(ToggleSetting("Woohoo 2", "This toggles something"))
                .addSetting(ToggleSetting("Woohoo 3", "This toggles something"))
                .addSetting(ToggleSetting("Woohoo 4", "This toggles something"))
                .addSetting(ToggleSetting("Woohoo 5", "This toggles something"))
                .childOf(categoryHolder)

        ////////////////
        // ANIMATIONS //
        ////////////////

        settingsBox.animate { setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(true)) }

        categories.animate {
            setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())

            onComplete {
                categoryTitle.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint()) }
                categoryHolder.children.forEachIndexed { index, uiComponent ->
                    uiComponent.animate {
                        setWidthAnimation(Animations.OUT_QUAD, 0.5f, RelativeConstraint(), delay = 0.1f * index)
                        setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(), delay = 0.1f * index)
                    }
                }
            }
        }

        categoryHolder.childrenOfType<Category>().first().select()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        window.mouseClick(mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        window.mouseRelease()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val delta = Mouse.getEventDWheel().coerceIn(-1, 1)
        window.mouseScroll(delta)
    }

    class Category(string: String, settingsBox: UIComponent) : UIBlock() {
        private val settings = mutableListOf<SettingObject>()
        var selected = false

        private val text = UIText(string).constrain {
            width = PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string) * 2f)
            height = 18.pixels()
        } childOf this

        private val selBlock = UIBlock().constrain {
            color = Color(255, 255, 255, 255).asConstraint()
            y = SiblingConstraint()
            height = 2.pixels()
        } childOf this

        private val settingsBlock = SettingsBlock().constrain {
            width = RelativeConstraint()
            height = RelativeConstraint()
        } childOf settingsBox

        init {
            setY(SiblingConstraint())
            setX((-10).pixels())
            setHeight(ChildBasedSizeConstraint() + 8.pixels())

            enableEffects(ScissorEffect())

            onMouseEnter {
                text.animate { setXAnimation(Animations.OUT_EXP, 0.5f, 10.pixels()) }
            }

            onMouseLeave {
                text.animate { setXAnimation(Animations.OUT_BOUNCE, 0.5f, 0.pixels()) }
            }

            onMouseClick {
                parent.children.forEach {
                    it as Category
                    if (it == this && !it.selected) select()
                    else if (it != this && it.selected) it.deselect()
                }
            }
        }

        fun select() = apply {
            selected = true
            selBlock.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint()) }
            settings.forEach { it.animateIn() }
        }

        fun deselect() = apply {
            selected = false
            selBlock.animate { setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels()) }
            settings.forEach { it.animateOut() }
        }

        fun addSetting(setting: UIComponent) = apply {
            settings.add((setting as SettingObject))
            settingsBlock.addChild(setting)
        }
    }

    private class SettingsBlock : UIComponent() {
        var scrolled = 0

        init {
            onMouseScroll {
                if (it == 0 && !(children.first() as SettingObject).selected) return@onMouseScroll
                scrolled += it * 50

                children.first().animate {
                    setYAnimation(Animations.OUT_EXP, 0.5f, scrolled.pixels())
                }

                if (scrolled > 0) {
                    scrolled = 0
                    children.first().animate {
                        setYAnimation(Animations.OUT_ELASTIC, 0.5f, 0.pixels(), delay = 0.3f)
                    }
                }
            }
        }
    }
}