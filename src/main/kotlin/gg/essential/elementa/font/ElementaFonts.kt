package gg.essential.elementa.font

import gg.essential.elementa.font.data.Font

object ElementaFonts {
    private val MINECRAFT_FONT = Font.fromResource("/fonts/Minecraft-Regular")
    private val MINECRAFT_BOLD_RAW = Font.fromResource("/fonts/Minecraft-Bold")
    @JvmStatic
    val MINECRAFT = FontRenderer(MINECRAFT_FONT, boldFont = MINECRAFT_BOLD_RAW)


    @JvmStatic
    val MINECRAFT_BOLD = FontRenderer(MINECRAFT_BOLD_RAW)

    private val JETBRAINS_MONO_FONT = Font.fromResource("/fonts/JetBrainsMono-Regular")
    @JvmStatic
    val JETBRAINS_MONO = FontRenderer(JETBRAINS_MONO_FONT)


}
