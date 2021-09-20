package gg.essential.elementa.shaders

import gg.essential.universal.UGraphics

@Deprecated("Use UniversalCraft's UShader instead.")
object Shaders {
    val newShaders: Boolean get() = UGraphics.areShadersSupported()
}
