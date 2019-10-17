package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.effects.ScissorEffect
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class ExampleGui : GuiScreen() {
    private val settings: UIComponent
    private val window: Window = Window()

    init {
//        Window.addChild(uiBlock {
//            x = PixelConstraint()
//        })

        settings = UIBlock()
            .getConstraints()
            .setX(PixelConstraint(5f))
            .setY(PixelConstraint(5f))
            .setWidth(RelativeConstraint(0.3f))
            .setHeight(FillConstraint(-5f))
            .setColor(ConstantColorConstraint(Color(0, 172, 193, 255)))
            .finish()

        val top = UIBlock()
            .getConstraints()
            .setWidth(FillConstraint())
            .setHeight(PixelConstraint(20f))
            .setColor(ConstantColorConstraint(Color(0, 124, 145, 255)))
            .finish()

        val title = UIText("Settings")
            .getConstraints()
            .setX(PixelConstraint(5f))
            .setY(CenterConstraint())
            .finish()

        val button = UIBlock()
            .getConstraints()
            .setX(PixelConstraint(5f, true))
            .setY(PixelConstraint(5f))
            .setWidth(PixelConstraint(10f))
            .setHeight(PixelConstraint(10f))
            .setColor(ConstantColorConstraint(Color(93, 222, 244, 255)))
            .finish()
        button.onClick {
            window.removeChild(settings)
            println("CLICKED")
        }

        top.addChild(button)
        top.addChild(title)

        settings.addChild(top)
        settings.addChild(createSlider("Slider 1", PixelConstraint(30f)))
        settings.addChild(createSlider("Second Slider", PixelConstraint(65f)))

//        val blocky = UIBlock()
//            .getConstraints()
//            .setX(SiblingConstraint(Padding(2f)))
//            .setY(SiblingConstraint(Padding(5f)))
//            .setWidth(PixelConstraint(75f))
//            .setHeight(PixelConstraint(35f))
//            .setColor(ConstantColorConstraint(Color.RED))
//            .finish()
//
//        val blocky2 = UIBlock()
//            .getConstraints()
//            .setX(CramSiblingConstraint(Padding(5f)))
//            .setY(CramSiblingConstraint(Padding(5f)))
//            .setWidth(PixelConstraint(75f))
//            .setHeight(PixelConstraint(15f))
//            .setColor(ConstantColorConstraint(Color.GREEN))
//            .finish()
//
//        val blocky3 = UIBlock()
//            .getConstraints()
//            .setX(CramSiblingConstraint(Padding(5f)))
//            .setY(CramSiblingConstraint(Padding(5f)))
//            .setWidth(PixelConstraint(15f))
//            .setHeight(PixelConstraint(15f))
//            .setColor(RainbowColorConstraint())
//            .finish()

        val cont = UIContainer()
            .getConstraints()
            .setHeight(ChildBasedSizeConstraint())
            .setWidth(FillConstraint())
            .setX(SiblingConstraint())
            .setY(SiblingConstraint())
            .finish()
//        cont.addChildren(blocky, blocky2, blocky3)

        settings.addChild(cont)

        val textHolder = UIBlock()
            .getConstraints()
            .setX(CenterConstraint())
            .setY(CenterConstraint())
            .setWidth(PixelConstraint(50f))
            .setHeight(PixelConstraint(10f))
//            .setColor(ConstantColorConstraint(Color.BLACK))
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

//        val image = UIImage("./images/logo.png", "https://avatars3.githubusercontent.com/u/10331479?s=460&v=4")
//        image.getConstraints()
//                .setX(CenterConstraint())
//                .setY(PixelConstraint(3f))
//                .setWidth(RelativeConstraint(0.2f))
//                .setHeight(AspectConstraint(1f))
//
//        Window.addChild(image);
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
            .setX(PixelConstraint(15f))
            .setY(yConstraint)
            .setWidth(FillConstraint(-15f))
            .setHeight(PixelConstraint(30f))

        val title = UIText(text)

        val back = UIBlock()
        back.getConstraints()
            .setY(PixelConstraint(12f))
            .setWidth(FillConstraint())
            .setHeight(PixelConstraint(5f))
            .setColor(ConstantColorConstraint(Color(64, 64, 64, 255)))

        val grab = UIBlock()
        grab.getConstraints()
            .setX(PixelConstraint(0f))
            .setY(PixelConstraint(-2f))
            .setWidth(PixelConstraint(3f))
            .setHeight(PixelConstraint(9f))
            .setColor(ConstantColorConstraint(Color(0, 0, 0, 255)))

        container.addChildren(title, back.addChild(grab))

        return container
    }
}