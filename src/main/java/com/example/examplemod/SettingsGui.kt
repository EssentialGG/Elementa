package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
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
            width = RelativeConstraint.ONE_THIRD
            height = RelativeConstraint.FULL
            color = Color(0, 0, 0, 150).asConstraint()
        }

        val categoryTitle = UIBlock()
            .constrain {
                x = CenterConstraint()
                y = 10.pixels()
                width = 0.pixels()
                height = 36.pixels()
            }
            .childOf(categories)
            .enableEffects(ScissorEffect())

        UIText("Settings")
            .constrain {
                width = PixelConstraint("Settings".width() * 4f)
                height = 36.pixels()
            }
            .childOf(categoryTitle)

        val categoryHolder = UIBlock()
            .constrain {
                x = CenterConstraint()
                y = 50.pixels()
                width = RelativeConstraint(0.9f)
                height = ChildBasedSizeConstraint()
            } childOf categories

        window.addChild(categories)

        val settingsBox = UIBlock().constrain {
            x = PixelConstraint(-window.getWidth() * 2 / 3, true)
            width = RelativeConstraint.TWO_THIRDS
            height = RelativeConstraint.FULL
            color = Color(0, 0, 0, 100).asConstraint()
        } childOf window

        Category("General", settingsBox)
        .addSetting(ToggleSetting("General 1", "This toggles something"))
                .addSetting(ToggleSetting("General 2", "This toggles something"))
                .addSetting(ToggleSetting("General 3", "This toggles something"))
                .addSetting(ToggleSetting("General 4", "This toggles something"))
                .addSetting(ToggleSetting("General 5", "This toggles something"))
                .addSetting(ToggleSetting("General 6", "This toggles something"))
                .addSetting(ToggleSetting("General 7", "This toggles something"))
                .addSetting(ToggleSetting("General 8", "This toggles something"))
                .addSetting(ToggleSetting("General 9", "This toggles something"))
                .childOf(categoryHolder)
        Category("Position", settingsBox)
                .addSetting(ToggleSetting("Position 1", "This toggles something"))
                .addSetting(ToggleSetting("Position 2", "This toggles something"))
                .addSetting(ToggleSetting("Position 3", "This toggles something"))
                .childOf(categoryHolder)
        Category("Test", settingsBox)
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

        settingsBox.animate {
            setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(true))
        }

        categories.animate {
            setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())

            onComplete {
                categoryTitle.animate {
                    setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint(2f))
                }

                categoryHolder.children.forEachIndexed { index, uiComponent ->
                    uiComponent.animate {
                        setWidthAnimation(Animations.OUT_QUAD, 0.5f, RelativeConstraint(1f), delay = 0.1f * index)
                        setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(), delay = 0.1f * index)
                    }
                }
            }
        }

        categoryHolder.childrenOfType<Category>().first().select()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        window.click(mouseButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }

    override fun handleMouseInput() {
        super.handleMouseInput()

        val delta = Mouse.getEventDWheel().coerceIn(-1, 1)

        window.scroll(delta)
    }

    class Category(string: String, settingsBox: UIComponent) : UIBlock() {
        private val settings = mutableListOf<Setting>()
        private var selected = false

        private val text = UIText(string)
            .constrain {
                setWidth(PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string) * 2f))
                setHeight(18.pixels())
            }

        private val selBlock = UIBlock()
                .constrain {
                    setColor(Color(255, 255, 255, 255).asConstraint())
                    setY(SiblingConstraint())
                    setHeight(2.pixels())
                }

        private val settingsBlock = SettingsBlock()
                .constrain {
                    setWidth(RelativeConstraint(1f))
                    setHeight(RelativeConstraint(1f))
                } childOf settingsBox

        init {
            setY(SiblingConstraint())
            setX((-10).pixels())
            setHeight(ChildBasedSizeConstraint() + 8.pixels())

            enableEffects(ScissorEffect())

            onHover {
                text.animate {
                    setXAnimation(Animations.OUT_EXP, 0.5f, 10.pixels())
                }
            }

            onUnHover {
                text.animate {
                    setXAnimation(Animations.OUT_BOUNCE, 0.5f, 0.pixels())
                }
            }

            onClick {
                parent.children.forEach {
                    it as Category
                    if (it == this && !it.selected) select()
                    else if (it != this && it.selected) it.deselect()
                }
            }

            addChildren(text, selBlock)
        }

        fun select() = apply {
            selected = true
            selBlock.animate {
                setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint(1f))
            }

            settings.forEach {
                it.drawBox.constrain {
                    setY(10.pixels())
                }
                it.drawBox.animate {
                    setYAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 100).asConstraint())
                }
                it.title.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 255).asConstraint())
                }
                it.text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 255).asConstraint())
                }
            }
        }

        fun deselect() = apply {
            selected = false
            selBlock.animate {
                setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
            }

            settings.forEach {
                it.drawBox.constrain {
                    setY(0.pixels())
                }
                it.drawBox.animate {
                    setYAnimation(Animations.OUT_EXP, 0.5f, (-10).pixels())
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 0).asConstraint())
                }
                it.title.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint())
                }
                it.text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint())
                }
            }
        }

        fun addSetting(setting: UIComponent) = apply {
            settings.add((setting as Setting))
            settingsBlock.addChild(setting)
        }
    }

    private class SettingsBlock() : UIComponent() {
        var scrolled = 0

        init {
            onScroll {
                if (it == 0) return@onScroll
                scrolled += it * 50
                println(scrolled)
                if (scrolled <= 0) {
                    children.first().animate {
                        setYAnimation(Animations.OUT_EXP, 0.5f, scrolled.pixels())
                    }
                } else {
                    scrolled = 0
                    children.first().animate {
                        setYAnimation(Animations.OUT_ELASTIC, 0.5f, 0.pixels())
                    }
                }
            }
        }
    }

    private abstract class Setting(private val name: String, private val description: String) : UIComponent() {
        val drawBox = UIBlock()
        val title = UIText(name)
        val text = UIText(description)

        init {
            setX(CenterConstraint())
            setY(CramSiblingConstraint())
            setWidth(RelativeConstraint(0.75f))
            setHeight(100.pixels())

            drawBox.constrain {
                setHeight(RelativeConstraint(0.9f))
                setWidth(RelativeConstraint(0.9f))
                setX(CenterConstraint())
                setY(CenterConstraint())
                setColor(Color(0, 0, 0, 0).asConstraint())
            }.enableEffects(ScissorEffect())

            title.constrain {
                setX(3.pixels())
                setY(3.pixels())
                setWidth(PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) * 2f))
                setHeight(18.pixels())
                setColor(Color(0, 0, 0, 10).asConstraint())
            } childOf drawBox

            text.constrain {
                setX(3.pixels())
                setY(25.pixels())
                setColor(Color(0, 0, 0, 10).asConstraint())
            } childOf drawBox

            addChild(drawBox)
        }
    }

    private class ToggleSetting(name: String, description: String) : Setting(name, description) {
        var toggled = true

        val toggleBox = UIBlock()
        val toggleSlider = UIBlock()
        val toggleTextOn = UIText("ON")
        val toggleTextOff = UIText("OFF")

        init {
            toggleBox.constrain {
                
            }


            addChild(toggleBox)
        }

        fun toggle() {
            if (toggled) {

            } else {

            }
        }
    }

    private class Divider(name: String, description: String) : Setting(name, description) {

    }
}