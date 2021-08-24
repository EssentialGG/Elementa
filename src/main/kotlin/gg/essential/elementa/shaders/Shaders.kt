package gg.essential.elementa.shaders

import gg.essential.universal.UGraphics

object Shaders {
    val newShaders: Boolean get() = UGraphics.areShadersSupported()
}
