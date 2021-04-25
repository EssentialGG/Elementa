package club.sk1er.elementa.font.data

import club.sk1er.mods.core.universal.UGraphics
import com.google.gson.JsonParser
import net.minecraft.client.renderer.texture.DynamicTexture
import java.io.InputStream

class Font(
    val fontInfo: FontInfo,
    private val atlas: InputStream
) {
    private lateinit var texture: DynamicTexture

    fun getTexture(): DynamicTexture {
        if (!::texture.isInitialized) {
            texture = UGraphics.getTexture(atlas)
        }

        return texture
    }

    companion object {
        fun fromResource(path: String): Font {
            val json = this::class.java.getResourceAsStream("$path.json")
            val fontInfo = FontInfo.fromJson(JsonParser().parse(json.reader()).asJsonObject)

            return Font(fontInfo, this::class.java.getResourceAsStream("$path.png"))
        }
    }
}

