package club.sk1er.elementa.font

import club.sk1er.elementa.font.data.Font

object DefaultFonts {
    @JvmStatic
    val MINECRAFT_FONT = Font.fromResource("/fonts/Minecraft-Regular")
    @JvmStatic
    val MINECRAFT = FontRenderer(MINECRAFT_FONT)

    @JvmStatic
    val FIRA_FONT = Font.fromResource("/fonts/FiraMono-Regular")
    @JvmStatic
    val FIRA_MONO = FontRenderer(FIRA_FONT)

    @JvmStatic
    val FIRA_A_FONT = Font.fromResource("/fonts/mono_a")
    @JvmStatic
    val FIRA_MONO_A = FontRenderer(FIRA_A_FONT)

    @JvmStatic
    val JETBRAINS_MONO_FONT = Font.fromResource("/fonts/JetBrainsMono-Regular")
    @JvmStatic
    val JETBRAINS_MONO = FontRenderer(JETBRAINS_MONO_FONT)

//    val MINECRAFT_A = FontRenderer("/fonts/msdf.png")
//    val FIRA_A = FontRenderer("/fonts/mono_a.png")

    fun load() {
//        MINECRAFT.load()
//        MINECRAFT_A.load()
//        FIRA_A.load()
    }
}
