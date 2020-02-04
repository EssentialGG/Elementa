package com.example.examplemod

import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import net.minecraft.client.gui.GuiScreen
import java.awt.Color
import java.net.URL

class ExampleServerList : GuiScreen() {
    val window = Window()

    val serverList = (ScrollComponent().constrain {
        y = 30.pixels()
        width = RelativeConstraint()
        height = RelativeConstraint()
    } childOf window) as ScrollComponent

    init {
        serverList.addChild(ServerBlock("Hypixel", "mc.hypixel.net", UIImage.ofURL(URL("https://i.imgur.com/rZYAHbE.png")), UIImage.ofURL(URL("https://i.imgur.com/iFawofh.png"))))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.draw()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        window.mouseClick(mouseX, mouseY, mouseButton)
    }

    private class ServerBlock(val name: String, val ip: String, val logo: UIImage, val banner: UIImage) : UIBlock(Color(0, 0, 0, 100)) {
        val glow = UIShape(Color(200, 200, 200, 100))

        init {
            constrain {
                x = CenterConstraint()
                y = SiblingConstraint() + 10.pixels()
                width = RelativeConstraint(2 / 3f)
                height = 75.pixels()
            }.onMouseEnter {
                banner.animate {
                    setWidthAnimation(Animations.OUT_EXP, 1f, RelativeConstraint() + 20.pixels())
                }
                glow.getVertexes()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 60.pixels(true))
                }
                glow.getVertexes()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 80.pixels(true))
                }
            }.onMouseLeave {
                banner.animate {
                    setWidthAnimation(Animations.OUT_EXP, 1f, RelativeConstraint())
                }
                glow.getVertexes()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 20.pixels(true))
                }
                glow.getVertexes()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 40.pixels(true))
                }
            } effect ScissorEffect()

            banner.constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                width = RelativeConstraint()
                height = ImageAspectConstraint()
            } childOf this

            logo.constrain {
                x = 5.pixels()
                y = CenterConstraint()
                width = ImageAspectConstraint()
                height = RelativeConstraint(0.75f)
            } childOf this

            glow childOf this
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(20.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(40.pixels(true), 0.pixels(true)))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))
        }
    }
}