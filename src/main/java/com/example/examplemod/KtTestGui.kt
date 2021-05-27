package com.example.examplemod

import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.image.MSDFComponent
import gg.essential.elementa.components.input.UIMultilineTextInput
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.font.DefaultFonts
import net.minecraft.client.Minecraft
import java.awt.Color

class KtTestGui : WindowScreen() {
    private val myTextBox = UIBlock(Color(0, 0, 0, 255))

    init {
//        for (i in 0..100) {
//            UIImage.ofResource("/cosmetics.png").constrain {
//                width = 15.pixels()
//                height = 15.pixels()
//                x = CramSiblingConstraint()
//                y = CramSiblingConstraint()
//            } childOf window
//        }

//        var first = true
//        val items = listOf(
//            "cart",
//            "add",
//            "arrow-left",
//            "search",
//            "arrow-right",
//            "kick",
//            "featured",
//            "hat",
//            "friends",
//            "chat"
//        )
//        UIContainer().constrain {
//            x = CenterConstraint()
//            y = RelativeConstraint(.25f)
//        } childOf window
//        for (j in 1..10) {
//            val tmp = UIContainer().constrain {
//                x = CenterConstraint()
//                y = SiblingConstraint()
//                width = RelativeConstraint(.5f)
//                height = ChildBasedMaxSizeConstraint()
//            } childOf window
//            for (i in items) {
//                UIImage.ofResourceCached("/${i}.png").constrain {
//                    width = (2 * j).pixels()
//                    height = (2 * j).pixels()
//                    x = CramSiblingConstraint()
//                    y = CramSiblingConstraint()
//                    color = Color.white.toConstraint()
//                } childOf tmp
//            }
//
//        }
//        val input by UIMultilineTextInput("Type something...").constrain {
//            x = 15.pixels()
//            y = CenterConstraint()
//            width = 100.percent() - 63.pixels()
//            color = Color(255,255,255,255).toConstraint()
//            fontProvider = DefaultFonts.ELEMENTA_MINECRAFT_FONT_RENDERER
//        }.setMaxLines(3) childOf window
//
//        input.onMouseClick {
//            input.grabWindowFocus()
//        }
//        val searchInput by UITextInput("Search...", shadow = false).constrain {
//            width = 90.percent()
//            height = 100.percent()
//            fontProvider = DefaultFonts.ELEMENTA_MINECRAFT_FONT_RENDERER
//            x = CenterConstraint()
//            y = CenterConstraint()
//        } childOf window
//        searchInput.onMouseClick {
//            searchInput.grabWindowFocus()
//
//        }
        UIText("\u00a7lAdd Outfit to Cart").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 2.pixels()
            fontProvider = DefaultFonts.ELEMENTA_MINECRAFT_FONT_RENDERER
        } childOf window

        Inspector(window) childOf window
    }

    override fun onResize(mcIn: Minecraft, w: Int, h: Int) {
        window.onWindowResize()
        super.onResize(mcIn, w, h)
    }
    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(mouseX, mouseY, partialTicks)

//        UIBlock.drawBlock(Color.GRAY, 10.0, 10.0, 500.0, 600.0)
//
//        val random = Random(1)
//
//        var y = 0
//        for (i in 8..40) {
//            if (i % 2 == 0)
//                ElementaFonts.MINECRAFT
//                    .drawString(
//                        "This is a TEST String! ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
//                        Color(16777215), 20.0f, 10.0f + y, i.toFloat()/2
//                    )
//            else
//                ElementaFonts.MINECRAFT
//                    .drawString(
//                        "§lThis is a TEST String! ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
//                        Color(16777215), 20.0f, 10.0f + y, i.toFloat()/2
//                    )
//
//            y += i/2 + 1
//        }
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
//        window.removeChild(settings)
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
                    setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 50).toConstraint())
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
                    setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 30).toConstraint())
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
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(100, 100, 100, 255).toConstraint())
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