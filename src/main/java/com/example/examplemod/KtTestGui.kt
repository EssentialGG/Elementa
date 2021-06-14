package com.example.examplemod

import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.input.UIMultilineTextInput
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.font.DefaultFonts
import gg.essential.elementa.font.ElementaFonts
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.UMatrixStack
import net.minecraft.util.text.TextFormatting
import java.awt.Color

class KtTestGui : WindowScreen() {
    private val myTextBox = UIBlock(Color(0, 0, 0, 255))

    init {
//        val hello = UIBlock(Color.BLACK).constrain {
//            x = CenterConstraint()
//            y = CenterConstraint()
//            height = 50.percent()
//            width = 50.percent()
//        } childOf window
//        val hi = UIBlock(Color.PINK).constrain {
//            x = CenterConstraint()
//            y = CenterConstraint()
//            height = 100.pixels()
//            width = 100.pixels()
//        }.also {
//            it childOf hello
//            it effect ScissorEffect()//ScissorEffect(it.getLeft() - 10f, it.getTop() - 10f, it.getRight() + 10f, it.getBottom() + 10f)
//        }
//
//        UIBlock(Color.MAGENTA.withAlpha(100)).constrain {
//            x = (-50).pixels()
//            y = (-50).pixels()
//            height = 100.percent() + 100.pixels()
//            width = 100.percent() + 100.pixels()
//        } childOf hi
//
//        Inspector(window) childOf window
//        val tmp = UIMultilineTextInput("Placeholder").constrain {
//            x = CenterConstraint()
//            y = CenterConstraint()
//            width = 100.pixels()
//            fontProvider = DefaultFonts.ELEMENTA_MINECRAFT_FONT_RENDERER
//        } childOf window
//        tmp.lineHeight = 10f
//        tmp.grabWindowFocus()
        val tmp = UIText("Hello World").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 4.pixels()
        } childOf window

        tmp.onMouseEnter {
            tmp.setText(TextFormatting.BOLD.toString() + "Hello World");
        }
        tmp.onMouseLeave {
            tmp.setText("Hello World");
        }
    }


    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)

//        UIBlock.drawBlock(Color.GRAY, 10.0, 10.0, 500.0, 600.0)
//
//        val random = Random(1)
//
//        var y = 0
//        for (i in 8..40) {
//            if (i % 2 == 0)
//                DefaultFonts.ELEMENTA_MINECRAFT_FONT_RENDERER
//                    .drawString(
//                        "This is a TEST String! ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
//                        Color(16777215), 20.0f, 10.0f + y, 9f, i.toFloat()/20
//                    )
//            else
//                DefaultFonts.ELEMENTA_MINECRAFT_FONT_RENDERER
//                    .drawString(
//                        "§lThis is a TEST String! ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
//                        Color(16777215), 20.0f, 10.0f + y, 9f, i.toFloat()/20
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