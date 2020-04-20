package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.image.BlurHashImage
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.mods.core.universal.UniversalScreen
import java.awt.Color
import java.net.URL

class ExampleServerList : UniversalScreen() {
    val window = Window()

    val serverList = (ScrollComponent().constrain {
        y = 30.pixels()
        width = RelativeConstraint()
        height = RelativeConstraint()
    } childOf window)

    init {
        serverList.addChildren(
            ServerBlock(
                "Hypixel",
                "mc.hypixel.net",
                UIImage.ofURL(URL("https://i.imgur.com/rZYAHbE.png")),
                BlurHashImage.ofURL("L49ZWHE19at,}HIW9^%KM^J.g3#R", URL("https://i.imgur.com/iFawofh.png"))
            ),
            ServerBlock(
                "Mineplex",
                "us.mineplex.com",
                UIImage.ofURL(URL("https://imgur.com/TmvUI18.png")),
                UIImage.ofURL(URL("https://imgur.com/hVvpUex.png"))
            ),
            ServerBlock(
                "Hive",
                "play.hivemc.com",
                UIImage.ofURL(URL("https://imgur.com/OyE5sMI.png")),
                UIImage.ofURL(URL("https://imgur.com/pVrcZ4E.png"))
            ),
            ServerBlock(
                "Wynncraft",
                "play.wynncraft.com",
                UIImage.ofURL(URL("https://imgur.com/i3nXXvQ.png")),
                UIImage.ofURL(URL("https://imgur.com/CNP8VwC.png"))
            ),
            ServerBlock(
                "My Server",
                "mc.kerbybit.com"
            )
        )

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

    override fun onMouseScroll(delta: Int) {
        window.mouseScroll(delta.coerceIn(-1, 1))
    }
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        window.keyType(typedChar, keyCode)
    }

    class ServerBlock(
        val name: String,
        val ip: String,
        val logo: UIComponent = UIText(name),
        val banner: UIComponent = defaultBanner
    ) : UIBlock(Color(0, 0, 0, 100)) {

        var inOptions = false

        val glow = UIShape(Color(200, 200, 200, 30))
        val optionContainer = UIContainer()
        val optionGlow = UIShape(Color(200, 200, 200, 100))

        val nameInputBox = UIBlock(Color(0, 0, 0, 175))
        val nameInput = UITextInput(placeholder = "\u00a77Server Name", wrapped = false)

        val ipInputBox = UIBlock(Color(0, 0, 0, 175))
        val ipInput = UITextInput(placeholder = "\u00a77Server IP", wrapped = false)

        init {
            constrain {
                x = CenterConstraint()
                y = SiblingConstraint() + 10.pixels()
                width = RelativeConstraint(2 / 3f).max(500.pixels())
                height = AspectConstraint(1/5f)
            }.onMouseEnter {
                if (inOptions) return@onMouseEnter
                banner.animate {
                    setWidthAnimation(Animations.OUT_EXP, 0.5f, RelativeConstraint() + 20.pixels())
                }
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 75).asConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 0.5f, 60.pixels(true))
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 80.pixels(true))
                }
            }.onMouseLeave {
                if (inOptions) return@onMouseLeave
                banner.animate {
                    setWidthAnimation(Animations.OUT_EXP, 1f, RelativeConstraint())
                }
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 30).asConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 2f, 20.pixels(true))
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 40.pixels(true))
                }
            }.onMouseClick { event ->
                when (event.mouseButton) {
                    0 -> {
                        if (inOptions) {
                            nameInput.active = false
                            ipInput.active = false
                        } else {
                            // TODO connect to server using this.ip
                        }
                    }
                    1 -> {
                        inOptions = !inOptions
                        if (inOptions) {
                            optionContainer.animate {
                                setXAnimation(Animations.OUT_EXP, 1f, 0.pixels(alignOpposite = true))
                            }
                            banner.animate {
                                setWidthAnimation(Animations.OUT_EXP, 1f, RelativeConstraint() + 40.pixels())
                            }
                            optionGlow.getVertices()[1].animate {
                                setXAnimation(Animations.OUT_EXP, 1f, 20.pixels(true))
                            }
                            optionGlow.getVertices()[2].animate {
                                setXAnimation(Animations.OUT_EXP, 2f, 40.pixels(true))
                            }
                            glow.getVertices()[1].animate {
                                setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
                            }
                            glow.getVertices()[2].animate {
                                setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
                            }
                        } else {
                            nameInput.active = false
                            ipInput.active = false
                            optionContainer.animate {
                                setXAnimation(Animations.OUT_EXP, 1f, 0.pixels(alignOpposite = true, alignOutside = true))
                            }
                            banner.animate {
                                setWidthAnimation(Animations.OUT_EXP, 1f, RelativeConstraint() + 20.pixels())
                            }
                            optionGlow.getVertices()[1].animate {
                                setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(true))
                            }
                            optionGlow.getVertices()[2].animate {
                                setXAnimation(Animations.OUT_EXP, 1f, 0.pixels(true))
                            }
                            glow.getVertices()[1].animate {
                                setXAnimation(Animations.OUT_EXP, 1f, 60.pixels(true))
                            }
                            glow.getVertices()[2].animate {
                                setXAnimation(Animations.OUT_EXP, 1f, 80.pixels(true))
                            }
                        }
                    }
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
                width = RelativeConstraint(1/3f)
                height = when (logo) {
                    is UIImage -> ImageAspectConstraint()
                    is UIText -> TextAspectConstraint()
                    else -> AspectConstraint()
                }
            } childOf this

            glow childOf this
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(20.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(40.pixels(true), 0.pixels(true)))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))

            optionGlow childOf this
            optionGlow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            optionGlow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            optionGlow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))
            optionGlow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))

            optionContainer.constrain {
                x = 0.pixels(alignOpposite = true, alignOutside = true)
                width = 125.pixels()
                height = RelativeConstraint()
            } childOf this


            nameInputBox.constrain {
                x = 30.pixels()
                y = 10.pixels()
                width = ChildBasedSizeConstraint() + 10.pixels()
                height = ChildBasedSizeConstraint() + 8.pixels()
            }.onMouseClick {
                nameInput.active = true
            } effect ScissorEffect() childOf optionContainer

            nameInput.text = name
            nameInput.minWidth = 75.pixels()
            nameInput.maxWidth = 75.pixels()
            nameInput.constrain {
                x = 5.pixels()
                y = CenterConstraint()
                width = 50.pixels()
            } childOf nameInputBox

            ipInputBox.constrain {
                x = 30.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = ChildBasedSizeConstraint() + 10.pixels()
                height = ChildBasedSizeConstraint() + 8.pixels()
            }.onMouseClick {
                ipInput.active = true
            } effect ScissorEffect() childOf optionContainer

            ipInput.text = ip
            ipInput.minWidth = 75.pixels()
            ipInput.maxWidth = 75.pixels()
            ipInput.constrain {
                x = 5.pixels()
                y = CenterConstraint()
                width = 50.pixels()
            } childOf ipInputBox
        }

        companion object {
            val defaultBanner = UIImage.ofURL(URL("https://imgur.com/caLyNoy.png"))
        }
    }
}