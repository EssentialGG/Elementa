package gg.essential.elementa.font

import gg.essential.elementa.VanillaFontRenderer

object DefaultFonts {
    @JvmStatic
    val VANILLA_FONT_RENDERER: FontProvider = VanillaFontRenderer()

    @JvmStatic
    val ELEMENTA_MINECRAFT_FONT_RENDERER: FontProvider = ElementaFonts.MINECRAFT

    @JvmStatic
    val JETBRAINS_MONO_FONT_RENDERER: FontProvider = ElementaFonts.JETBRAINS_MONO

    @JvmStatic
    val MINECRAFT_FIVE: FontProvider = ElementaFonts.MINECRAFT_FIVE
}