package club.sk1er.elementa.font

import club.sk1er.elementa.font.data.Font

object DefaultFonts {
    @JvmStatic
    val MINECRAFT_FONT = Font.fromPath("/fonts/Minecraft-Regular")
    @JvmStatic
    val MINECRAFT = FontRenderer(MINECRAFT_FONT)

//    val MINECRAFT_A = FontRenderer("/fonts/msdf.png")
//    val FIRA_A = FontRenderer("/fonts/mono_a.png")

    fun load() {
//        MINECRAFT.load()
//        MINECRAFT_A.load()
//        FIRA_A.load()
    }
}
