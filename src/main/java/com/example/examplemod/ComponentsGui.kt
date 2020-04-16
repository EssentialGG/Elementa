package com.example.examplemod

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.ChildBasedMaxSizeConstraint
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint
import club.sk1er.elementa.constraints.CramSiblingConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.mods.core.universal.UniversalScreen
import java.awt.Color

class ComponentsGui : UniversalScreen() {
    val window = Window()

    init {
        ComponentType("UIContainer") {
            val bar = UIBlock(Color.WHITE).constrain {
                x = 2.pixels()
                y = SiblingConstraint() + 5.pixels()
                width = 150.pixels()
                height = 50.pixels()
            } childOf this

            val container = UIContainer().constrain {
                x = 0.pixels(true)
                width = ChildBasedSizeConstraint() + 6.pixels()
                height = ChildBasedMaxSizeConstraint()
            } childOf bar

            repeat(3) {
                UIBlock(Color.RED).constrain {
                    x = SiblingConstraint() + 2.pixels()
                    width = 25.pixels()
                    height = 25.pixels()
                } childOf container
            }
        } childOf window
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        drawDefaultBackground()

        window.draw()
    }

    class ComponentType(componentName: String, initBlock: ComponentType.() -> Unit) : UIContainer() {
        init {
            constrain {
                x = CramSiblingConstraint(10f) min 5.pixels()
                y = CramSiblingConstraint(10f) min 5.pixels()
                width = ChildBasedMaxSizeConstraint() + 5.pixels()
                height = ChildBasedSizeConstraint() + 10.pixels()
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