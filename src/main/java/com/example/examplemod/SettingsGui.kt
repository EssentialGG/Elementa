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
            width = RelativeConstraint(1 / 3f)
            height = RelativeConstraint(1f)
            color = Color(0, 0, 0, 150).asConstraint()
    } childOf window

        val categoryTitle = UIContainer()
            .constrain {
                y = 10.pixels()
                x = CenterConstraint()
                width = 0.pixels()
                height = 36.pixels()
            }.enableEffects(ScissorEffect()) childOf categories

        UIText("Settings")
            .constrain {
                width = MaxSizeConstraint(PixelConstraint("Settings".width() * 4f), RelativeConstraint(1f).constrainTo(categories))
                height = 36.pixels()
            } childOf categoryTitle

        val categoryHolder = UIBlock()
            .constrain {
                x = CenterConstraint()
                y = 50.pixels()
                width = RelativeConstraint(0.9f)
                height = ChildBasedSizeConstraint()
            } childOf categories

        val settingsBox = UIBlock().constrain {
            x = PixelConstraint(-window.getWidth() * 2 / 3, true)
            width = RelativeConstraint(2 / 3f)
            height = RelativeConstraint(1f)
            color = Color(0, 0, 0, 100).asConstraint()
        } childOf window

        Category("General", settingsBox)
                .addSetting(Divider("General Settings"))
                .addSetting(ToggleSetting("General 1", "This toggles something"))
                .addSetting(ToggleSetting("General 2", "This toggles something"))
                .addSetting(ToggleSetting("General 3", "This toggles something"))
                .addSetting(ToggleSetting("General 4", "This toggles something"))
                .addSetting(Divider("Category"))
                .addSetting(ToggleSetting("General 5", "This toggles something"))
                .addSetting(ToggleSetting("General 6", "This toggles something"))
                .addSetting(ToggleSetting("General 7", "This toggles something"))
                .addSetting(ToggleSetting("General 8", "This toggles something"))
                .addSetting(ToggleSetting("General 9", "This toggles something"))
                .childOf(categoryHolder)
        Category("Position", settingsBox)
                .addSetting(Divider("Position Settings"))
                .addSetting(ToggleSetting("Position 1", "This toggles something"))
                .addSetting(ToggleSetting("Position 2", "This toggles something"))
                .addSetting(ToggleSetting("Position 3", "This toggles something"))
                .childOf(categoryHolder)
        Category("Test", settingsBox)
                .addSetting(Divider("Test Settings"))
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
                    setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint())
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

    class Category(string: String, settingsBox: UIComponent) : UIContainer() {
        private val settings = mutableListOf<Setting>()
        private var selected = false

        private val text = UIText(string)
            .constrain {
                width = PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string) * 2f)
                height = 18.pixels()
            }

        private val selBlock = UIBlock()
                .constrain {
                    color = Color(255, 255, 255, 255).asConstraint()
                    y = SiblingConstraint()
                    height = 2.pixels()
                }

        private val settingsBlock = SettingsBlock()
                .constrain {
                    width = RelativeConstraint(1f)
                    height = RelativeConstraint(1f)
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
                it.animateIn()
            }
        }

        fun deselect() = apply {
            selected = false
            selBlock.animate {
                setWidthAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
            }

            settings.forEach {
                it.animateOut()
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

    private abstract class Setting() : UIContainer() {
        init {
            setX(CenterConstraint())
            setY(CramSiblingConstraint())
            setWidth(RelativeConstraint(0.75f))
            setHeight(40.pixels())
        }

        abstract fun animateOut()
        abstract fun animateIn()
    }

    private class ToggleSetting(name: String, description: String) : Setting() {
        var toggled = true
        private var selected = false

        val drawBox = UIBlock()
        val title = UIText(name)
        val text = UIText(description)

        val toggleBox = UIBlock()
        val toggleSlider = UIBlock()
        val toggleTextOn = UIText("ON")
        val toggleTextOff = UIText("OFF")

        init {
            drawBox.constrain {
                height = RelativeConstraint(0.9f)
                width = RelativeConstraint(1f)
                color = Color(0, 0, 0, 0).asConstraint()
            }.enableEffects(ScissorEffect()) childOf this

            title.constrain {
                x = 3.pixels()
                y = 3.pixels()
                width = PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) * 2f)
                height = 18.pixels()
                color = Color(255, 255, 255, 10).asConstraint()
            } childOf drawBox

            text.constrain {
                x = 3.pixels()
                y = 25.pixels()
                color = Color(255, 255, 255, 10).asConstraint()
            } childOf drawBox

            toggleBox.constrain {
                x = 5.pixels(true)
                y = CenterConstraint()
                width = 50.pixels()
                height = 20.pixels()
                color = Color(0, 0, 0, 0).asConstraint()
            }.onClick {
                toggle()
            } childOf drawBox

            toggleSlider.constrain {
                width = RelativeConstraint(0.5f)
                height = RelativeConstraint(1f)
                color = Color(0, 120, 0, 0).asConstraint()
            } childOf toggleBox

            toggleTextOn.constrain {
                x = 6.pixels()
                y = CenterConstraint()
                color = Color(255, 255, 255, 10).asConstraint()
            } childOf toggleBox

            toggleTextOff.constrain {
                x = 3.pixels(true)
                y = CenterConstraint()
                color = Color(255, 255, 255, 10).asConstraint()
            } childOf toggleBox
        }

        override fun animateIn() {
            selected = true
            drawBox.constrain {
                y = 10.pixels()
            }
            drawBox.animate {
                setYAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 100).asConstraint())
            }
            animateAlpha(255, title, text, toggleBox, toggleTextOn, toggleTextOff, toggleSlider)
        }

        override fun animateOut() {
            selected = false
            drawBox.constrain {
                y = 0.pixels()
            }
            drawBox.animate {
                setYAnimation(Animations.OUT_EXP, 0.5f, (-10).pixels())
                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 0, 0, 0).asConstraint())
            }
            animateAlpha(0, title, text, toggleBox, toggleTextOn, toggleTextOff, toggleSlider)
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

        fun toggle() {
            if (!selected) return
            if (toggled) {
                toggled = false
                toggleSlider.animate {
                    setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(true))
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 0, 0).asConstraint())
                }
            } else {
                toggled = true
                toggleSlider.animate {
                    setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(0, 120, 0).asConstraint())
                }
            }
        }
    }

    private class Divider(name: String) : Setting() {
        val title = UIText(name)

        init {
            title.constrain {
                x = CenterConstraint()
                y = CenterConstraint(10f)
                width = PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) * 2f)
                height = 18.pixels()
                color = Color(255, 255, 255, 10).asConstraint()
            } childOf this
        }

        override fun animateIn() {
            title.constrain {
                y = CenterConstraint(10f)
            }
            title.animate {
                setYAnimation(Animations.OUT_EXP, 0.5f, CenterConstraint())
                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 255).asConstraint())
            }
        }

        override fun animateOut() {
            title.constrain {
                y = CenterConstraint()
            }
            title.animate {
                setYAnimation(Animations.OUT_EXP, 0.5f, CenterConstraint(-10f))
                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 10).asConstraint())
            }
        }
    }
}