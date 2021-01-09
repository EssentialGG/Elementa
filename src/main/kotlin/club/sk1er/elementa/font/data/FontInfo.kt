package club.sk1er.elementa.font.data

import com.google.gson.Gson
import com.google.gson.JsonObject

data class FontInfo(
    val atlas: Atlas,
    val metrics: Metrics,
    val glyphs: Map<Int, Glyph>
) {
    companion object {
        private val gson = Gson()

        fun fromJson(json: JsonObject): FontInfo {
            val atlas = gson.fromJson(json.getAsJsonObject("atlas"), Atlas::class.java)
            val metrics = gson.fromJson(json.getAsJsonObject("metrics"), Metrics::class.java)
            val glyphs = json.getAsJsonArray("glyphs").associate { glyphElement ->
                val glyph = gson.fromJson(glyphElement, Glyph::class.java)
                glyph.unicode to glyph
            }

            return FontInfo(atlas, metrics, glyphs)
        }
    }
}

data class Atlas(
    val type: String,
    val distanceRange: Float,
    val size: Float,
    val width: Float,
    val height: Float,
    val yOrigin: String
)

data class Metrics(
    val lineHeight: Float,
    val ascender: Float,
    val descender: Float,
    val underlineY: Float,
    val underlineThickness: Float
)

class Glyph(
    val unicode: Int,
    val advance: Float,
    val planeBounds: Bounds? = null,
    val atlasBounds: Bounds? = null
)

data class Bounds(
    val left: Float,
    val bottom: Float,
    val right: Float,
    val top: Float
)
