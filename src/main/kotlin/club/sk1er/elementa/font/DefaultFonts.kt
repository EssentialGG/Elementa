package club.sk1er.elementa.font

import club.sk1er.elementa.font.data.Font

object DefaultFonts {
    private val MINECRAFT_FONT = Font.fromResource("/fonts/Minecraft-Regular")
    @JvmStatic
    val MINECRAFT = FontRenderer(MINECRAFT_FONT)

    private val JETBRAINS_MONO_FONT = Font.fromResource("/fonts/JetBrainsMono-Regular")
    @JvmStatic
    val JETBRAINS_MONO = FontRenderer(JETBRAINS_MONO_FONT)
}
