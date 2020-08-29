package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.ConstantColorConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import net.minecraft.client.Minecraft
import java.awt.Color

fun Char.width(textScale: Float = 1f) = UniversalGraphicsHandler.getCharWidth(this) * textScale

fun String.width(textScale: Float = 1f) = UniversalGraphicsHandler.getStringWidth(this) * textScale

@JvmOverloads
fun Int.pixels(alignOpposite: Boolean = false, alignOutside: Boolean = false): PixelConstraint {
    return PixelConstraint(this.toFloat(), alignOpposite, alignOutside)
}

@JvmOverloads
fun Float.pixels(alignOpposite: Boolean = false, alignOutside: Boolean = false): PixelConstraint {
    return PixelConstraint(this, alignOpposite, alignOutside)
}

fun Color.asConstraint() = ConstantColorConstraint(this)