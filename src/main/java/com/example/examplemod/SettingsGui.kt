package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.animate
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.features.ScissorFeature
import club.sk1er.elementa.helpers.Padding
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class SettingsGui : GuiScreen() {
    private val window = Window()

    init {
        val categories = UIBlock().constrain {
            x = PixelConstraint(-window.getWidth() / 3)
            width = RelativeConstraint.ONE_THIRD
            height = RelativeConstraint.FULL
            color = ConstantColorConstraint(Color(0, 0, 0, 150))
        }

        val categoryTitle = UIBlock()
            .constrain {
                x = CenterConstraint()
                y = PixelConstraint(10f)
                width = PixelConstraint(0f)
                height = PixelConstraint(36f)
            }
            .childOf(categories)
            .enableFeatures(ScissorFeature())

        UIText("Settings")
            .constrain {
                width = PixelConstraint("Settings".width() * 4f)
                height = PixelConstraint(36f)
            }
            .childOf(categoryTitle)

        val categoryHolder = UIContainer()
            .constrain {
                x = CenterConstraint()
                y = PixelConstraint(50f)
                width = RelativeConstraint(0.9f)
            } childOf categories

        window.addChild(categories)

        val settingsBox = UIBlock().constrain {
            x = PixelConstraint(-window.getWidth() * 2 / 3, true)
            width = RelativeConstraint.TWO_THIRDS
            height = RelativeConstraint.FULL
            color = ConstantColorConstraint(Color(0, 0, 0, 100))
        } childOf window

        Category("General", settingsBox)
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .childOf(categoryHolder)
        Category("Position", settingsBox)
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .childOf(categoryHolder)
        Category("Test", settingsBox)
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .childOf(categoryHolder)
        Category("Category", settingsBox)
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .childOf(categoryHolder)
        Category("Woohoo", settingsBox)
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .addSetting(ToggleSetting("Toggle", "This toggles something"))
                .childOf(categoryHolder)

        ////////////////
        // ANIMATIONS //
        ////////////////

        settingsBox.animate {
            setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f, true))
        }

        categories.animate {
            setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f))

            onComplete {
                categoryTitle.animate {
                    setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint(2f))
                }

                categoryHolder.children.forEachIndexed { index, uiComponent ->
                    uiComponent.animate {
                        setWidthAnimation(Animations.OUT_QUAD, 0.5f, RelativeConstraint(1f), delay = 0.1f * index)
                        setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f), delay = 0.1f * index)
                    }
                }
            }
        }

        categoryHolder.childrenOfType<Category>().first().select()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        window.click()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }

    private class Category(string: String, settingsBox: UIComponent) : UIComponent() {
        private val settings = mutableListOf<Setting>()

        private val text = UIText(string)
            .constrain {
                setWidth(PixelConstraint(Minecraft.getMinecraft().fontRendererObj.getStringWidth(string) * 2f))
                setHeight(PixelConstraint(18f))
            }

        private val selBlock = UIBlock()
                .constrain {
                    setColor(ConstantColorConstraint(Color(255, 255, 255, 255)))
                    setY(SiblingConstraint())
                    setHeight(PixelConstraint(2f))
                }

        private val settingBlock = UIContainer()
                .constrain {
                    setWidth(RelativeConstraint(1f))
                    setHeight(RelativeConstraint(1f))
                } childOf settingsBox

        init {
            setY(SiblingConstraint(Padding(8f)))
            setX(PixelConstraint(-10f))
            setHeight(ChildBasedSizeConstraint())

            enableFeatures(ScissorFeature())

            onHover {
                text.animate {
                    setXAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(10f))
                }
            }

            onUnHover {
                text.animate {
                    setXAnimation(Animations.OUT_BOUNCE, 0.5f, PixelConstraint(0f))
                }
            }

            onClick {
                parent.children.forEach {
                    if (it == this) select()
                    else (it as Category).deselect()
                }
            }

            addChildren(text, selBlock)
        }

        fun select() = apply {
            selBlock.animate {
                setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint(1f))
            }

            settings.forEachIndexed { index, setting ->
                setting.children.first().animate {
                    setWidthAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(100f), delay = 0.1f * index)
                    setColorAnimation(Animations.OUT_EXP, 0.5f, ConstantColorConstraint(Color(0, 0, 0, 100)), delay = 0.1f * index)
                }
            }
        }

        fun deselect() = apply {
            selBlock.animate {
                setWidthAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f))
            }

            settings.forEachIndexed { index, setting ->
                setting.children.first().animate {
                    setWidthAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f), delay = 0.1f * index)
                    setColorAnimation(Animations.OUT_EXP, 0.5f, ConstantColorConstraint(Color(0, 0, 0, 0)), delay = 0.1f * index)
                }
            }
        }

        fun addSetting(setting: UIComponent) = apply {
            settings.add((setting as Setting))
            settingBlock.addChild(setting)
        }
    }

    private abstract class Setting(private val name: String, private val description: String) : UIComponent() {
        init {
            setX(CramSiblingConstraint(Padding(10f)))
            setY(CramSiblingConstraint(Padding(10f)))
            setWidth(PixelConstraint(100f))
            setHeight(PixelConstraint(20f))

            val drawBox = UIBlock()
                    .constrain {
                        setHeight(RelativeConstraint(1f))
                        setColor(ConstantColorConstraint(Color(0, 0, 0, 100)))
                    }.enableFeatures(ScissorFeature())
            val text = UIText(name)
                    .constrain {
                        setX(PixelConstraint(3f))
                        setY(CenterConstraint())
                    } childOf drawBox

            addChild(drawBox)
        }
    }

    private class ToggleSetting(name: String, description: String) : Setting(name, description) {

    }
}