package com.example.examplemod

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.impl.Platform.Companion.platform
import gg.essential.universal.UScreen
import java.awt.Color

/**
 * List of buttons to open a specific example gui.
 * See ExampleGui (singular) for a well-commented example gui.
 */
class ExamplesGui : WindowScreen(ElementaVersion.V2) {
    private val container by ScrollComponent().constrain {
        y = 3.pixels()
        width = 100.percent()
        height = 100.percent() - (3 * 2).pixels()
    } childOf window

    init {
        for ((name, action) in examples) {
            val button = UIBlock().constrain {
                x = CenterConstraint()
                y = SiblingConstraint(padding = 3f)
                width = 200.pixels()
                height = 20.pixels()
                color = Color(255, 255, 255, 102).toConstraint()
            }.onMouseEnter {
                animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 150).toConstraint())
                }
            }.onMouseLeave {
                animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 102).toConstraint())
                }
            }.onMouseClick {
                try {
                    platform.currentScreen = action()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } childOf container

            UIText(name).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            } childOf button
        }
    }

    companion object {
        val examples = mutableMapOf<String, () -> UScreen>(
            "ExampleGui" to ::ExampleGui,
            "ComponentsGui" to ::ComponentsGui,
        )
    }
}
