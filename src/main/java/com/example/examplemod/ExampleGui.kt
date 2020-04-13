package com.example.examplemod

import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.image.BlurHashImage
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.mods.core.universal.UniversalScreen

import java.awt.Color
import java.net.URL

class ExampleGui : UniversalScreen() {

    private val window: Window = Window()

    private val myTextBox = UIBlock(Color(0, 0, 0, 255))
    private val myText = UITextInput(placeholder = "Text Input", wrapped = false)
    private val scroll = ScrollComponent().constrain {
        width = RelativeConstraint(.3f)
        height = RelativeConstraint(1f)
    } childOf window

    private val notification = (Notify("") childOf window).apply { hide() }

    private val settings = UIBlock(Color(0, 172, 193, 255)).constrain {
        x = 5.pixels()
        y = 5.pixels()
        width = RelativeConstraint(1f)
        height = FillConstraint() - 5.pixels()
    }.addChildren(
        UIBlock(Color(0, 124, 145, 255)).constrain {
            width = FillConstraint()
            height = 20.pixels()
        }.addChildren(
            UIText("Settings").constrain {
                x = 5.pixels()
                y = CenterConstraint()
            },
            UIBlock(Color(93, 222, 255, 255)).constrain {
                x = 5.pixels(true)
                y = 5.pixels()
                width = 10.pixels()
                height = 10.pixels()
            }.onMouseClick { _, _, _ -> closeSettings() }
        ),
        UIBlock(Color(0, 0, 0, 255)).constrain {
            x = 5.pixels()
            y = SiblingConstraint() + 5.pixels()
            width = FillConstraint() - 5.pixels()
            height = 20.pixels()
        }.onMouseClick { _, _, _ ->

            notification.setText(
                title = "Example Notification",
                text = "This is a test notification with a really long text line"
            )

            notification.unhide()
            notification.grabParentFocus()
        }.addChild(
            UIText("Notify").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            }
        ),
        Slider("Slider 1", SiblingConstraint() + 10.pixels()),
        Slider("Second Slider", SiblingConstraint()),
        myTextBox.addChild(myText),
        UIBlock(Color.RED).constrain {
            y = SiblingConstraint()
            width = RelativeConstraint()
            height = 5.pixels()
        },
//        UIWrappedText("Example of some longer wrapped text with shadow").constrain {
//            y = SiblingConstraint()
//            width = RelativeConstraint()
//        },
        UIBlock(Color.RED).constrain {
            y = SiblingConstraint()
            width = RelativeConstraint()
            height = 5.pixels()
        }/*,
        UIWrappedText("More example of longer text wrapped and centered", shadow = true, centered = true).constrain {
            y = SiblingConstraint()
            width = RelativeConstraint()
        }*/
    ) childOf scroll

    init {

        myTextBox.constrain {
            y = SiblingConstraint()
            width = ChildBasedSizeConstraint() + 4.pixels()
            height = ChildBasedSizeConstraint() + 4.pixels()
        }.onMouseClick { _, _, _ ->
            myText.active = true
        } effect ScissorEffect()

        myText.minWidth = 100.pixels()
        myText.maxWidth = RelativeConstraint().to(settings) - 4.pixels() as WidthConstraint
        myText.constrain {
            x = 2.pixels()
            y = CenterConstraint()
        }

        window.onMouseClick { _, _, _ ->
            myText.active = false
        }

        val blocky = UIBlock().constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = 50.pixels()
            height = 10.pixels()
        }.addChild(
            UIText("I'm longer than my container")
        ) effect ScissorEffect() childOf window

        blocky.animate {
            setWidthAnimation(Animations.OUT_CIRCULAR, 3f, ChildBasedSizeConstraint())
            setColorAnimation(Animations.IN_EXP, 3f, Color.PINK.asConstraint())
            onComplete {
                window.removeChild(blocky)
            }
        }

        val text = UIText("My text").constrain {
            x = CenterConstraint()
            y = 2.pixels()
            textScale = 2.pixels()
        }

        (UIBlock(Color.RED).constrain {
            x = CenterConstraint()
            y = 5.pixels()
            width = 10.pixels()
            height = 10.pixels()
        } childOf window).animate {
            setWidthAnimation(Animations.LINEAR, 5f, 150.pixels())
        }

        SVGComponent.ofResource("/accessible.svg").constrain {
            x = CenterConstraint()
            y = 25.pixels()
            width = (24 * 2.25f).pixels()
            height = (24 * 2.25f).pixels()
            color = Color.RED.asConstraint()
        } childOf window

        BlurHashImage.ofURL("L4ESU,OD1e#:=GwwJSAr1M,r|]Ar", URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = RelativeConstraint(0.25f)
            height = RelativeConstraint(0.25f)
        } childOf window

//
//        repeat(100) {
//            UIBlock(Color.GREEN).constrain {
//                x = 1.pixels()
//                y = SiblingConstraint() + 1.pixels()
//                width = RelativeConstraint() - 2.pixels()
//                height = RelativeConstraint(0.098f)
//            } childOf scroller
//        }
//
//        UIBlock(Color.RED).constrain {
//            x = CenterConstraint()
//            y = 10.pixels(true)
//            width = (Minecraft.getMinecraft().fontRendererObj.getStringWidth("My big text!") * 2f).pixels()
//            height = 18.pixels()
//        } childOf window
//
//        UIText("My big text!").constrain {
//            x = CenterConstraint()
//            y = 10.pixels(true)
//            textScale = 2.pixels()
//        } childOf window
//
//        UIImage.ofURL(URL("https://visage.surgeplay.com/face/32/1173617851a0481c830ab35e143ddd1b")).constrain {
//            x = 1.pixels()
//            y = SiblingConstraint() + 1.pixels()
//            width = RelativeConstraint() - 2.pixels()
//            height = RelativeConstraint(0.098f)
//        } childOf scroller
//
//        val friendsListScrollBar = UIBlock(Color.CYAN).constrain {
//            x = 0.pixels(alignOpposite = true)
//            width = 6.pixels()
//            height = FillConstraint()
//        } childOf window
//
//        val friendsListScrollBarGripContainer = UIContainer().constrain {
//            x = CenterConstraint()
//            y = 2.pixels()
//            width = RelativeConstraint(1f) - 2.pixels()
//            height = RelativeConstraint(1f) - 4.pixels()
//        } childOf friendsListScrollBar
//
//        val friendsListScrollBarGrip = UIBlock(Color.BLACK).constrain {
//            width = RelativeConstraint(1f)
//            height = RelativeConstraint(1f)
//        } childOf friendsListScrollBarGripContainer
//
//        scroller.setScrollBarComponent(friendsListScrollBarGrip)

//        UIImage.ofURL(URL("https://visage.surgeplay.com/face/32/02f62a6be7484546b9ff26e3ab4b1076"))
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

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        window.mouseDrag(mouseX, mouseY, clickedMouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        window.mouseRelease()
    }

    override fun onMouseScroll(delta: Int) {
        window.mouseScroll(delta.coerceIn(-1, 1))
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        window.keyType(typedChar, keyCode)
    }

    //#if MC<11500
    override fun doesGuiPauseGame() = false
    //#endif

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

    private class Notify(textString: String, titleString: String? = null, var delay: Float = 3f) : UIBlock(Color(0, 0, 0, 200)) {
        private val timer = UIBlock(Color(0, 170, 255, 255)).constrain {
            y = 0.pixels(true)
            height = 7.pixels()
        } childOf this

        private val glow = UIShape(Color(255, 255, 255, 30))
        private val text = UIWrappedText(textString, true).constrain {
            x = 2.pixels()
            y = SiblingConstraint() + 6.pixels()
            width = FillConstraint() - 2.pixels()
        }
        private val title = UIText(titleString ?: "").constrain {
            x = 2.pixels()
            y = 5.pixels()
            textScale = 1.5f.pixels()
        }

        private val timerAnimation = timer.makeAnimation()
        private var clicked = false

        init {
            glow childOf this
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))

            if (titleString != null) {
                title childOf this
            }

            text childOf this

            constrain {
                x = 0.pixels(alignOpposite = true, alignOutside = true)
                y = 5.pixels(true)
                width = 155.pixels()
                height = ChildBasedSizeConstraint() + 11.pixels()
            }.onMouseEnter {
                if (clicked) return@onMouseEnter
                timerAnimation.width.pauseIfSupported()
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 50).asConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 20.pixels(true))
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 2f, 24.pixels(true))
                }
            }.onMouseLeave {
                if (clicked) return@onMouseLeave
                timerAnimation.width.resumeIfSupported()
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 30).asConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 10.pixels(true))
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 2f, 14.pixels(true))
                }
            }.onMouseClick { _, _, _ ->
                clicked = true
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(100, 100, 100, 255).asConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
                    onComplete {
                        hide()
                    }
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 0.pixels())
                }
            }

            animateBeforeHide {
                setXAnimation(Animations.IN_EXP, 0.5f, 0.pixels(alignOpposite = true, alignOutside = true), 0.5f)

                onComplete {
                    releaseParentFocus()
                }
            }

            animateAfterUnhide {
                timer.setWidth(0.pixels())
                clicked = false

                setXAnimation(Animations.OUT_EXP, 0.5f, 5.pixels(true))
                onComplete {
                    animateTimer()
                }
            }
        }

        fun setText(text: String, title: String? = null) {
            this.text.setText(text)
            this.title.setText(title ?: "")

            if (!children.contains(this.title)) this.title childOf this
        }

        private fun animateTimer() {
            glow.getVertices()[1].animate {
                setXAnimation(Animations.OUT_EXP, 1f, 10.pixels(true))
            }
            glow.getVertices()[2].animate {
                setXAnimation(Animations.OUT_EXP, 2f, 14.pixels(true))
            }
            timerAnimation
                .setWidthAnimation(Animations.LINEAR, delay, RelativeConstraint(), 0.5f)
                .begin()
                .onComplete {
                    hide()
                }
        }
    }
}