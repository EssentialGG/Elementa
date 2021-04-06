package com.example.examplemod

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.image.BlurHashImage
import club.sk1er.elementa.components.input.UIMultilineTextInput
import club.sk1er.elementa.components.input.UITextInput
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.elementa.markdown.MarkdownComponent
import java.awt.Color
import java.net.URL

class ComponentsGui : WindowScreen() {
    init {
        ComponentType("UIContainer") {
            val bar = UIBlock().constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 150.pixels()
                height = 50.pixels()
            } childOf this

            val container = UIContainer().constrain {
                x = 0.pixels(true)
                width = ChildBasedSizeConstraint(padding = 2f)
                height = ChildBasedMaxSizeConstraint()
            } childOf bar effect OutlineEffect(Color.BLUE, 2f)

            repeat(3) {
                UIBlock(Color.RED).constrain {
                    x = SiblingConstraint(padding = 2f)
                    width = 25.pixels()
                    height = 25.pixels()
                } childOf container
            }
        } childOf window

        ComponentType("UIBlock") {
            UIBlock().constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 50.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        ComponentType("UIText") {
            UIText("This is my non-wrapping text").constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                // I have no need to set a width/height, UIText sets those to the
                // inherent width/height of the passed string.
            } childOf this

            UIText("I can scale!").constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                textScale = (1.5f).pixels()
            } childOf this

            UIText("Shadowless...", shadow = false).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
            } childOf this
        } childOf window

        ComponentType("UIWrappedText") {
            UIWrappedText("This is my text that is wrapping at 100 pixels!").constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
            } childOf this

            UIWrappedText("I'm going to wrap at 100 pixels, but centered :)", centered = true).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
            } childOf this

            UIWrappedText("I have a height, a lot of text, and will trim. foo bar baz qux", trimText = true).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 100.pixels()
                height = 30.pixels()
            } childOf this
        } childOf window

        ComponentType("UIRoundedRectangle") {
            UIRoundedRectangle(2f).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = 50.pixels()
            } childOf this

            UIRoundedRectangle(10f).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        ComponentType("UICircle") {
            UICircle().constrain {
                // These x & y positions describe the CENTER of the circle
                x = 30.pixels()
                y = SiblingConstraint() + 15.pixels()

                // We do not specify the width & height of the circle, rather, we specify
                // its radius.
                radius = 10.pixels()
            } childOf this

            UICircle(10f).constrain {
                x = 30.pixels()
                y = SiblingConstraint() + 15.pixels()
            } childOf this
        } childOf window

        ComponentType("UIShape") {
            val shapeHolder = UIContainer().constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 50.pixels()
                height = 40.pixels()
            } childOf this

            (UIShape() childOf shapeHolder).apply {
                // Must be called after [childOf] because [addVertex] requires a parent at call
                addVertex(UIPoint(
                    x = 15.pixels(),
                    y = 10.pixels()
                ))

                addVertex(UIPoint(
                    x = 16.pixels(),
                    y = 30.pixels()
                ))

                addVertex(UIPoint(
                    x = 36.pixels(),
                    y = 24.pixels()
                ))

                addVertex(UIPoint(
                    x = 32.pixels(),
                    y = 15.pixels()
                ))

                addVertex(UIPoint(
                    x = 10.pixels(),
                    y = 4.pixels()
                ))
            }
        } childOf window

        ComponentType("UIImage") {
            UIImage.ofURL(URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = 50.pixels()
            } childOf this

            UIImage.ofURL(URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 150.pixels()
                height = ImageAspectConstraint()
            } childOf this
        } childOf window

        ComponentType("BlurHashImage") {
            BlurHashImage("L4ESU,OD1e#:=GwwJSAr1M,r|]Ar").constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = 50.pixels()
            } childOf this

            BlurHashImage.ofURL("L4ESU,OD1e#:=GwwJSAr1M,r|]Ar", URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        ComponentType("Text Input") {
            val box1 = UIBlock(Color(50, 50, 50)).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = 12.pixels()
            } childOf this

            val textInput1 = UITextInput("My single line text input!").constrain {
                x = 2.pixels()
                y = 2.pixels()

                width = RelativeConstraint(1f) - 6.pixels()
            } childOf box1

            box1.onMouseClick { textInput1.grabWindowFocus() }

            val box2 = UIBlock(Color(50, 50, 50)).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = ChildBasedSizeConstraint() + 4.pixels()
            } childOf this

            val textInput2 = UIMultilineTextInput("My multiline text input!").constrain {
                x = 2.pixels()
                y = 2.pixels()

                width = RelativeConstraint(1f) - 6.pixels()
            }.setMaxLines(4) childOf box2

            box2.onMouseClick { textInput2.grabWindowFocus() }
        } childOf window

        ComponentType("ScrollComponent") {
            val scroll1 = ScrollComponent().constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 150.pixels()
                height = 75.pixels()
            } childOf this

            repeat(5) {
                UIBlock(Color.RED).constrain {
                    x = CenterConstraint()
                    y = SiblingConstraint(padding = 2f)

                    width = 50.pixels()
                    height = 25.pixels()
                } childOf scroll1
            }

            ScrollComponent("I'm empty :(").constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 150.pixels()
                height = 75.pixels()
            } childOf this
        } childOf window

        ComponentType("Markdown") {
            MarkdownComponent(
                """
                    # Markdown!
                    
                    This is pretty cool. We [can](https://google.com) now render arbitrary markdown beautifully.
                    
                    ```
                    We even have code :)
                    ```
                """.trimIndent()
            ).constrain {
                x = 2.pixels()
                y = SiblingConstraint(padding = 2f)
                width = 200.pixels()
                height = 100.pixels()
            } childOf this
        } childOf window

        ComponentType("SVG") {
            SVGComponent.ofResource("/svg/test.svg").constrain {
                x = 2.pixels()
                y = SiblingConstraint(padding = 2f)
                width = 50.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        ComponentType("Gradient") {
            GradientComponent(Color.BLACK, Color.PINK).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 50.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        Inspector(window).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
        } childOf window
    }

    class ComponentType(componentName: String, initBlock: ComponentType.() -> Unit) : UIContainer() {
        init {
            constrain {
                x = CramSiblingConstraint(10f) coerceAtLeast 5.pixels()
                y = CramSiblingConstraint(10f) coerceAtLeast 5.pixels()
                width = ChildBasedMaxSizeConstraint() + 5.pixels()
                height = ChildBasedSizeConstraint(padding = 5f) + 5.pixels()
            }

            enableEffect(OutlineEffect(OUTLINE_COLOR, OUTLINE_WIDTH))

            UIText(componentName).constrain {
                x = 2.pixels()
                y = 2.pixels()

                textScale = (1.5f).pixels()
            } childOf this

            this.initBlock()
        }

        companion object {
            private val OUTLINE_COLOR = Color.BLACK
            private const val OUTLINE_WIDTH = 2f
        }
    }
}
