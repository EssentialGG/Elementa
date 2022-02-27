package gg.essential.elementa.font.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

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
    val yOrigin: String,
    val baseCharHeight: Float,
    val belowLineHeight: Float,
    val shadowHeight: Float
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
    val planeBounds: PlaneBounds? = null,
    val atlasBounds: AtlasBounds? = null
)

data class PlaneBounds(
    @SerializedName("left")
    private val _left: Float,
    @SerializedName("bottom")
    private val _bottom: Float,
    @SerializedName("right")
    private val _right: Float,
    @SerializedName("top")
    private val _top: Float
) {
    /**
     * msdfgen exports the plane locations with .025 subtracted from the
     * Y coordinate of each glyph, so we must correct for this
     */
    val left: Float
        get() = _left
    val bottom: Float
        get() = _bottom + 0.025f
    val right: Float
        get() = _right
    val top: Float
        get() = _top + 0.025f
}


data class AtlasBounds(
    @SerializedName("left")
    private val _left: Float,
    @SerializedName("bottom")
    private val _bottom: Float,
    @SerializedName("right")
    private val _right: Float,
    @SerializedName("top")
    private val _top: Float
) {
    /**
     * msdfgen exports UV locations in the middle of pixels.
     * This causes the rendering to occur slightly of from
     * where you would expect it and incorrect texel mapping.
     */
    val left: Float
        get() = _left + .5f
    val bottom: Float
        get() = _bottom + .5f
    val right: Float
        get() = _right + .5f
    val top: Float
        get() = _top + .5f
}
