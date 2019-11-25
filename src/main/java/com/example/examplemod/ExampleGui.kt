package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.asConstraint
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels
import club.sk1er.elementa.dsl.plus
import club.sk1er.elementa.effects.ScissorEffect
import net.minecraft.client.gui.GuiScreen
import java.awt.Color
import java.net.URL

class ExampleGui : GuiScreen() {
    private val settings: UIComponent
    private val window: Window = Window()

    init {
//        Window.addChild(uiBlock {
//            x = PixelConstraint()
//        })

        settings = UIBlock()
            .getConstraints()
            .withX(PixelConstraint(5f))
            .withY(PixelConstraint(5f))
            .withWidth(RelativeConstraint(0.3f))
            .withHeight(FillConstraint() + (-5).pixels())
            .withColor(ConstantColorConstraint(Color(0, 172, 193, 255)))
            .finish()

        val top = UIBlock()
            .getConstraints()
            .withWidth(FillConstraint())
            .withHeight(PixelConstraint(20f))
            .withColor(ConstantColorConstraint(Color(0, 124, 145, 255)))
            .finish()

        val title = UIText("Settings")
            .getConstraints()
            .withX(PixelConstraint(5f))
            .withY(CenterConstraint())
            .finish()

        val button = UIBlock()
            .getConstraints()
            .withX(PixelConstraint(5f, true))
            .withY(PixelConstraint(5f))
            .withWidth(PixelConstraint(10f))
            .withHeight(PixelConstraint(10f))
            .withColor(ConstantColorConstraint(Color(93, 222, 244, 255)))
            .finish()
        button.onMouseClick { _, _, _ ->
            window.removeChild(settings)
            println("CLICKED")
        }

        top.addChild(button)
        top.addChild(title)

        settings.addChild(top)
        settings.addChild(createSlider("Slider 1", PixelConstraint(30f)))
        settings.addChild(createSlider("Second Slider", PixelConstraint(65f)))
        settings.addChild(
            UIWrappedText("this is a test of really long text that wont fit on just one line inside of the gui").constrain {
                y = SiblingConstraint()
                width = RelativeConstraint()
                textScale = 2.pixels()
            }
        )
        settings.addChild(
            UIBlock().constrain {
                y = SiblingConstraint()
                width = RelativeConstraint()
                height = 5.pixels()
                color = Color.RED.asConstraint()
            }
        )

        val cont = UIContainer()
            .getConstraints()
            .withHeight(ChildBasedSizeConstraint())
            .withWidth(FillConstraint())
            .withX(SiblingConstraint())
            .withY(SiblingConstraint())
            .finish()

        settings.addChild(cont)

        val textHolder = UIBlock()
            .getConstraints()
            .withX(CenterConstraint())
            .withY(CenterConstraint())
            .withWidth(PixelConstraint(50f))
            .withHeight(PixelConstraint(10f))
            .finish()

        val bigText = UIText("I'm longer than my container")
        textHolder.addChild(bigText)

        window.addChild(settings)
        window.addChild(textHolder)

        textHolder.enableEffects(ScissorEffect())

        textHolder.makeAnimation()
            .setWidthAnimation(Animations.OUT_CIRCULAR, 3f, ChildBasedSizeConstraint())
            .setColorAnimation(Animations.IN_EXP, 3f, ConstantColorConstraint(Color.PINK))
            .begin()

        val imageComponent = UIImage.ofURL(URL("https://avatars3.githubusercontent.com/u/10331479?s=460&v=4"))
            .setX(PixelConstraint(0f))
            .setY(PixelConstraint(0f))
            .setWidth(RelativeConstraint(1f))
            .setHeight(RelativeConstraint(1f))

        window.addChild(imageComponent)

        val anim = imageComponent.makeAnimation()
            .setWidthAnimation(Animations.IN_CUBIC, 1.5f, RelativeConstraint(1 / 4f))
            .setHeightAnimation(Animations.IN_CUBIC, 1.5f, RelativeConstraint(1 / 4f))
            .setXAnimation(Animations.OUT_QUINT, 1.5f, PixelConstraint(15f, true))
            .setYAnimation(Animations.OUT_QUINT, 1.5f, PixelConstraint(15f, true))
            .onComplete {
                val newAnim = imageComponent.makeAnimation()
                    .setColorAnimation(
                        Animations.IN_CUBIC, 1f,
                        ConstantColorConstraint(Color(1f, 1f, 1f, 0f))
                    )

                imageComponent.animateTo(newAnim)
            }

        imageComponent.animateTo(anim)
    }


    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        if (mouseButton == 1) {
            val anim = settings.makeAnimation()

            if (settings.getConstraints().getX() == 5f) {
                anim.setXAnimation(Animations.IN_OUT_BOUNCE, 1f, PixelConstraint(5f, true))
            } else {
                anim.setXAnimation(Animations.OUT_BOUNCE, 1f, PixelConstraint(5f))
            }

            settings.animateTo(anim)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }

    private fun createSlider(text: String, yConstraint: PositionConstraint): UIComponent {
        val container = UIContainer()
        container.getConstraints()
            .withX(PixelConstraint(15f))
            .withY(yConstraint)
            .withWidth(FillConstraint() + (-15).pixels())
            .withHeight(PixelConstraint(30f))

        val title = UIText(text)

        val back = UIBlock()
        back.getConstraints()
            .withY(PixelConstraint(12f))
            .withWidth(FillConstraint())
            .withHeight(PixelConstraint(5f))
            .withColor(ConstantColorConstraint(Color(64, 64, 64, 255)))

        val grab = UIBlock()
        grab.getConstraints()
            .withX(PixelConstraint(0f))
            .withY(PixelConstraint(-2f))
            .withWidth(PixelConstraint(3f))
            .withHeight(PixelConstraint(9f))
            .withColor(ConstantColorConstraint(Color(0, 0, 0, 255)))

        container.addChildren(title, back.addChild(grab))

        return container
    }
}