package com.example.examplemod

import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import java.awt.Color

class ExampleGui : GuiScreen() {
    private val window: Window = Window()

    private val myText =
        UIWrappedText("this is a test of really long text that wont fit on just one line inside of the gui")


    private val settings = UIBlock(Color(0, 172, 193, 255))
        .setX(5.pixels())
        .setY(5.pixels())
        .setWidth(RelativeConstraint(0.3f))
        .setHeight(FillConstraint() + (-5).pixels())
        .addChildren(
            UIBlock(Color(0, 124, 145, 255))
                .setWidth(FillConstraint())
                .setHeight(PixelConstraint(20f))
                .addChildren(
                    UIText("Settings").setX(5.pixels()).setY(CenterConstraint()),
                    UIBlock(Color(93, 222, 255, 255))
                        .setX(5.pixels(true))
                        .setY(5.pixels())
                        .setWidth(10.pixels())
                        .setHeight(10.pixels())
                        .onMouseClick { _, _, _ ->
                            closeSettings()
                        }
                ),
            Slider("Slider 1", 30.pixels()),
            Slider("Second Slider", 65.pixels()),
            myText,
            UIBlock(Color.RED)
                .setY(SiblingConstraint())
                .setWidth(RelativeConstraint())
                .setHeight(5.pixels())
        ) childOf window

    init {
        myText.setY(SiblingConstraint())
            .setWidth(RelativeConstraint())
            .setTextScale(2.pixels())

        val blocky = UIBlock()
            .setX(CenterConstraint())
            .setY(CenterConstraint())
            .setWidth(50.pixels())
            .setHeight(10.pixels())
            .addChild(UIText("I'm longer than my container"))
            .effect(ScissorEffect())
            .childOf(window)

        blocky.animate {
            setWidthAnimation(Animations.OUT_CIRCULAR, 3f, ChildBasedSizeConstraint())
            setColorAnimation(Animations.IN_EXP, 3f, Color.PINK.asConstraint())
            onComplete {
                window.removeChild(blocky)
            }
        }

        val scroller = ScrollComponent().constrain {
            x = CenterConstraint()
            y = 30.pixels()
            width = RelativeConstraint(0.7f)
            height = RelativeConstraint(0.8f)
        } childOf window

        repeat(13) {
            UIBlock(Color.GREEN).constrain {
                x = 1.pixels()
                y = SiblingConstraint() + 1.pixels()
                width = RelativeConstraint() - 2.pixels()
                height = RelativeConstraint(0.098f)
            } childOf scroller
        }

//        UIImage.ofURL(URL("https://avatars3.githubusercontent.com/u/10331479?s=460&v=4"))
//            .setWidth(RelativeConstraint())
//            .setHeight(RelativeConstraint())
//            .childOf(window)
//            .animate {
//                setWidthAnimation(Animations.IN_CUBIC, 1.5f, RelativeConstraint(1 / 4f))
//                setHeightAnimation(Animations.IN_CUBIC, 1.5f, RelativeConstraint(1 / 4f))
//                setXAnimation(Animations.OUT_QUINT, 1.5f, PixelConstraint(15f, true))
//                setYAnimation(Animations.OUT_QUINT, 1.5f, PixelConstraint(15f, true))
//                onComplete {
//                    setColorAnimation(Animations.IN_CUBIC, 1f, Color(1, 1, 1, 0).asConstraint())
//                }
//            }
    }

    private fun closeSettings() {
        window.removeChild(settings)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        window.mouseClick(mouseX, mouseY, mouseButton)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val delta = Mouse.getEventDWheel().coerceIn(-1, 1)
        window.mouseScroll(delta)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        myText.setText(myText.getText() + typedChar)
    }

    private class Slider(text: String, yConstraint: PositionConstraint) : UIContainer() {
        private val slider = UICircle(5f, Color(0, 0, 0, 255))


        init {
            slider
                .setY(CenterConstraint())
                .onMouseEnter {
                    hover()
                }.onMouseLeave {
                    unhover()
                }.onMouseClick { _, _, _ ->
                    println("radius: " + slider.getRadius())
                }
            this
                .setX(PixelConstraint(15f))
                .setY(yConstraint)
                .setWidth(FillConstraint() + (-15).pixels())
                .setHeight(PixelConstraint(30f))
                .addChildren(
                    UIText(text),
                    UIBlock(Color(64, 64, 64, 255))
                        .setY(12.pixels())
                        .setWidth(FillConstraint())
                        .setHeight(5.pixels())
                        .addChild(slider)
                )
        }

        fun hover() {
            slider.animate {
                setRadiusAnimation(Animations.OUT_EXP, 1f, 10.pixels())
            }
        }

        fun unhover() {
            slider.animate {
                setRadiusAnimation(Animations.OUT_EXP, 1f, 5.pixels())
            }
        }
    }
}