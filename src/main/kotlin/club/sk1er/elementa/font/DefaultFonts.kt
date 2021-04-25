package club.sk1er.elementa.font

import club.sk1er.elementa.VanillaFontRenderer

object DefaultFonts   {

    @JvmStatic
    val vanillaFontRenderer: FontProvider = VanillaFontRenderer()

    @JvmStatic
    val elementaMinecraftFontRenderer: FontProvider = ElementaFonts.MINECRAFT;

    @JvmStatic
    val jetbrainsMonoFontRenderer: FontProvider = ElementaFonts.JETBRAINS_MONO;


}