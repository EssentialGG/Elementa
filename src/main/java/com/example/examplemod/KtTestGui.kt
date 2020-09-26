package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.input.UIMultilineTextInput
import club.sk1er.elementa.components.input.UITextInput
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import java.awt.Color
import java.net.URL
import kotlin.concurrent.thread

class KtTestGui : WindowScreen() {
    private val myTextBox = UIBlock(Color(0, 0, 0, 255))

    //    private val myText = UITextInput(placeholder = "Text Input", wrapped = false)
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
            }.onMouseClick { closeSettings() }
        ),
        UIBlock(Color(0, 0, 0, 255)).constrain {
            x = 5.pixels()
            y = SiblingConstraint() + 5.pixels()
            width = FillConstraint() - 5.pixels()
            height = 20.pixels()
        }.onMouseClick {
            notification.setText(
                title = "Example Notification",
                text = "This is a test notification with a really long text line"
            )

            notification.unhide()
        }.addChild(
            UIText("Notify").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            }
        ),
        Slider("Slider 1", SiblingConstraint() + 10.pixels()),
        Slider("Second Slider", SiblingConstraint()),
//        myTextBox.addChild(myText),
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
        }.onMouseClick {
//            myText.active = true
        } effect ScissorEffect()
//
//        myText.minWidth = 100.pixels()
//        myText.maxWidth = RelativeConstraint().to(settings) - 4.pixels() as WidthConstraint
//        myText.constrain {
//            x = 2.pixels()
//            y = CenterConstraint()
//        }
//
//        window.onMouseClick {
//            myText.active = false
//        }

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

        val parent = UIBlock(Color.BLACK).constrain {
            x = CenterConstraint()
            y = 5.pixels()
            width = ChildBasedSizeConstraint() + 3.pixels()
            height = ChildBasedSizeConstraint()
        } childOf window effect ScissorEffect()

        val input = UITextInput("empty").constrain {
            x = 0.pixels()
        }.setMinWidth(50.pixels()).setMaxWidth(80.pixels()) childOf parent

        parent.onMouseClick {
            input.grabWindowFocus()
        }

        val parent2 = UIBlock(Color.BLACK).constrain {
            x = CenterConstraint()
            y = 50.pixels()
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        } childOf window effect ScissorEffect()

        val input2 = UIMultilineTextInput("empty").constrain {
            x = 0.pixels()
            width = 80.pixels()
        }.setMaxLines(7) childOf parent2

        input2.onFocus {
            input2.setActive(true)
        }.onFocusLost {
            input2.setActive(false)
        }

        parent2.onMouseClick {
            input2.grabWindowFocus()
        }

        // For testing constraint error resolution
//        val firstBad = UIContainer().constrain {
//            width = ChildBasedSizeConstraint()
//            height = AspectConstraint()
//        } childOf parent2
//
//        val secondBad = UIContainer().constrain {
//            width = AspectConstraint()
//            height = RelativeConstraint()
//        } childOf firstBad

        val canvasContainer = UIContainer().constrain {
            x = 30.pixels()
            y = 30.pixels(alignOpposite = true)
            width = 120.pixels()
            height = 120.pixels()
        } childOf window

        val canvas = ScrollComponent(horizontalScrollEnabled = true, verticalScrollEnabled = true).constrain {
            width = RelativeConstraint()
            height = RelativeConstraint()
        } childOf canvasContainer

        val horizontalScroll = ScrollComponent.DefaultScrollBar(isHorizontal = true) childOf canvasContainer
        val verticalScroll = ScrollComponent.DefaultScrollBar(isHorizontal = false) childOf canvasContainer

        canvas.setHorizontalScrollBarComponent(horizontalScroll.grip)
        canvas.setVerticalScrollBarComponent(verticalScroll.grip)

        UIBlock(Color.RED).constrain {
            width = 100.pixels()
            height = 100.pixels()
        } childOf canvas

        UIBlock(Color.GREEN).constrain {
            x = 100.pixels()
            width = 100.pixels()
            height = 100.pixels()
        } childOf canvas

        UIBlock(Color.BLUE).constrain {
            y = 100.pixels()
            width = 100.pixels()
            height = 100.pixels()
        } childOf canvas

        UIBlock(Color.YELLOW).constrain {
            x = 100.pixels()
            y = 100.pixels()
            width = 100.pixels()
            height = 100.pixels()
        } childOf canvas

        val img = UIImage.ofURL(URL("https://i.imgur.com/Pc6iMw3e.png")).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = RelativeConstraint(0.25f)
            height = RelativeConstraint(0.25f)
        } childOf window
        animImgSmall(img)
//        BlurHashImage.ofURL("L4ESU,OD1e#:=GwwJSAr1M,r|]Ar", URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
//            x = CenterConstraint()
//            y = CenterConstraint()
//            width = RelativeConstraint(0.25f)
//            height = RelativeConstraint(0.25f)
//        } childOf window

        val inspector = Inspector(window).constrain {
            x = 20.pixels(alignOpposite = true)
            y = 20.pixels()
        } childOf window

        Inspector(inspector).constrain {
            x = 20.pixels()
            y = 20.pixels()
        } childOf window

        thread {
            Thread.sleep(5000)
            UIBlock(Color.RED).constrain {
                x = 50.pixels()
                y = 50.pixels()
                width = 50.pixels()
                height = 50.pixels()
            } childOf window
        }
    }

    private fun animImgSmall(img: UIImage) {
        img.animate {
            setWidthAnimation(Animations.LINEAR, 7f, 5.pixels(), 0.5f)
            setHeightAnimation(Animations.LINEAR, 7f, 5.pixels(), 0.5f)
            onComplete {
                animImgBig(img)
            }
        }
    }

    private fun animImgBig(img: UIImage) {
        img.animate {
            setWidthAnimation(Animations.LINEAR, 7f, RelativeConstraint(0.25f), 0.5f)
            setHeightAnimation(Animations.LINEAR, 7f, RelativeConstraint(0.25f), 0.5f)
            onComplete {
                animImgSmall(img)
            }
        }
    }

    private fun closeSettings() {
        window.removeChild(settings)
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
                }.onMouseClick {
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

    private class Notify(textString: String, titleString: String? = null, var delay: Float = 3f) :
        UIBlock(Color(0, 0, 0, 200)) {
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
            }.onMouseClick {
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