package com.example.examplemod

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.image.BlurHashImage
import gg.essential.elementa.components.input.UIMultilineTextInput
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.components.inspector.CompactToggle
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.markdown.*
import gg.essential.elementa.state.BasicState
import java.awt.Color
import java.net.URL

class ComponentsGui : WindowScreen(ElementaVersion.V2) {
    init {
        // Components declared through a delegated properly will have their component
        // name set to the property name in the inspector. In this case, the component
        // will be called "bar
        val containerExample by ComponentType("UIContainer") {
            val bar by UIBlock().constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 150.pixels()
                height = 50.pixels()
            } childOf this

            val container by UIContainer().constrain {
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

        val blockExample by ComponentType("UIBlock") {
            UIBlock().constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 50.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        val textExample by ComponentType("UIText") {
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

        val wrappedTextExample by ComponentType("UIWrappedText") {
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

        val rectangleExample by ComponentType("UIRoundedRectangle") {
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

        val circleExample by ComponentType("UICircle") {
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

        val shapeExample by ComponentType("UIShape") {
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

        val imageExample by ComponentType("UIImage") {
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

        val blurHashImageExample by ComponentType("BlurHashImage") {
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

        val textInputExample by ComponentType("Text Input") {
            val box1 by UIBlock(Color(50, 50, 50)).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = 12.pixels()
            } childOf this

            val textInput1 by UITextInput("My single line text input!").constrain {
                x = 2.pixels()
                y = 2.pixels()

                width = RelativeConstraint(1f) - 6.pixels()
            } childOf box1

            box1.onMouseClick { textInput1.grabWindowFocus() }

            val box2 by UIBlock(Color(50, 50, 50)).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()

                width = 100.pixels()
                height = ChildBasedSizeConstraint() + 4.pixels()
            } childOf this

            val textInput2 by UIMultilineTextInput("My multiline text input!").constrain {
                x = 2.pixels()
                y = 2.pixels()

                width = RelativeConstraint(1f) - 6.pixels()
            }.setMaxLines(4) childOf box2

            box2.onMouseClick { textInput2.grabWindowFocus() }
        } childOf window

        val scrollComponentExample by ComponentType("ScrollComponent") {
            val scroll1 by ScrollComponent().constrain {
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

        val markdownExample by ComponentType("Markdown") {
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

        val svgExample by ComponentType("SVG") {
            SVGComponent.ofResource("/svg/test.svg").constrain {
                x = 2.pixels()
                y = SiblingConstraint(padding = 2f)
                width = 50.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        val gradientExample by ComponentType("Gradient") {
            GradientComponent(Color.BLACK, Color.PINK).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 50.pixels()
                height = 50.pixels()
            } childOf this
        } childOf window

        val inspectorExample by ComponentType("Inspector") {
            constrain {
                width = 250.pixels
            }
            val startDetached = Inspector.startDetached
            Inspector.startDetached = false
            val inspector = Inspector(window)
            Inspector.startDetached = startDetached

            val inspectorTextDescription by MarkdownComponent(
                """The Elementa inspector provides useful debug features for creating UIs with Elementa.  
                    It can operate within a Window as an overlay or in an external window. 
                    It's initial position can be configured by adding 
                    `-Delementa.inspector.detached=<true/false>` to the JVM arguments.  
                  """,
                config = MarkdownConfig(
                    paragraphConfig = ParagraphConfig(
                        spaceBetweenLines = 0f
                    ),
                    inlineCodeConfig = InlineCodeConfig(
                        backgroundColor = Color.GRAY,
                        outlineWidth = 0f,
                        verticalPadding = 1f,
                    )
                )
            ).constrain {
                width = 100.percent
                y = SiblingConstraint(2f)
            } childOf this

            val inspectorExternal = BasicState(false)

            val toggleRow by UIContainer().constrain {
                y = SiblingConstraint(10f)
                width = 100.percent
                height = ChildBasedMaxSizeConstraint()
            }.addChildren(
                UIText("Detached Inspector: "),
                CompactToggle(inspectorExternal).constrain {
                    x = SiblingConstraint(5f)
                    y = CenterConstraint()
                }
            ) childOf this


            inspectorExternal.onSetValue {
                inspector.setDetached(it)
            }

            val inspectorFeaturesDescription by UIWrappedText(
                "The inspector allows you to configure the underlying states in components and constraints that" +
                    " implement the StateRegistry. "
            ).constrain {
                width = 100.percent
                y = SiblingConstraint(10f)
            } childOf this

            val inspectorHotkeyDescription by UIWrappedText(
                "You can also use hotkeys to interact with the inspector while using your UI.\n" +
                    "'C' activtes the constraints tab\n" +
                    "'V' activates the values tab\n" +
                    "'B' activates the states tab\n" +
                    "'S' activates the selection tool\n" +
                    "'M' activates the measure tool\n" +
                    "'N' disables the measure tool\n" +
                    "'D' toggles Elementa debug mode\n"
            ).constrain {
                width = 100.percent
                y = SiblingConstraint(10f)
            } childOf this

            inspector childOf window

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
