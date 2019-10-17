package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.features.ScissorFeature
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

        val categoryHolder = UIBlock()
            .constrain {
                x = CenterConstraint()
                y = PixelConstraint(50f)
                width = RelativeConstraint(0.9f)
                height = ChildBasedSizeConstraint()
            } childOf categories

        window.addChild(categories)

        val settingsBox = UIBlock().constrain {
            x = PixelConstraint(-window.getWidth() * 2 / 3, true)
            width = RelativeConstraint.TWO_THIRDS
            height = RelativeConstraint.FULL
            color = ConstantColorConstraint(Color(0, 0, 0, 100))
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

    override fun handleMouseInput() {
        super.handleMouseInput()

        val delta = Mouse.getEventDWheel().coerceIn(-1, 1)

        window.scroll(delta)
    }

    class Category(string: String, settingsBox: UIComponent) : UIBlock() {
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
            setY(SiblingConstraint())
            setX(PixelConstraint(-10f))
            setHeight(ChildBasedSizeConstraint().padHeight(8f))

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

            settings.forEach {
                it.drawBox.animate {
                    setYAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f))
                    setColorAnimation(Animations.OUT_EXP, 0.5f, ConstantColorConstraint(Color(0, 0, 0, 100)))
                }
                it.text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, ConstantColorConstraint(Color(255, 255, 255, 255)))
                }
            }
        }

        fun deselect() = apply {
            selBlock.animate {
                setWidthAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(0f))
            }

            settings.forEach {
                it.drawBox.animate {
                    setYAnimation(Animations.OUT_EXP, 0.5f, PixelConstraint(-10f))
                    setColorAnimation(Animations.OUT_EXP, 0.5f, ConstantColorConstraint(Color(0, 0, 0, 0)))
                    onComplete {
                        it.drawBox.constrain {
                            setY(PixelConstraint(10f))
                        }
                    }
                }
                it.text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, ConstantColorConstraint(Color(255, 255, 255, 10)))
                }
            }
        }

        fun addSetting(setting: UIComponent) = apply {
            settings.add((setting as Setting))
            settingBlock.addChild(setting)
        }
    }

    private abstract class Setting(private val name: String, private val description: String) : UIComponent() {
        val drawBox = UIBlock()
        val text = UIText(name)

        init {
            setX(CramSiblingConstraint())
            setY(CramSiblingConstraint())
            setWidth(PixelConstraint(100f).padWidth(10f))
            setHeight(PixelConstraint(20f).padHeight(10f))

            drawBox.constrain {
                setHeight(RelativeConstraint(1f))
                setWidth(RelativeConstraint(1f))
                setY(PixelConstraint(10f))
                setColor(ConstantColorConstraint(Color(0, 0, 0, 0)))
            }.enableFeatures(ScissorFeature())

            text.constrain {
                setX(PixelConstraint(3f))
                setY(CenterConstraint())
                setColor(ConstantColorConstraint(Color(0, 0, 0, 10)))
            } childOf drawBox

            addChild(drawBox)
        }
    }

    private class ToggleSetting(name: String, description: String) : Setting(name, description) {

    }
}