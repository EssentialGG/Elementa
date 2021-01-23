package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.ConstantColorConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.constraints.RelativeWindowConstraint
import club.sk1er.mods.core.universal.UGraphics
import net.minecraft.client.Minecraft
import java.awt.Color

fun Char.width(textScale: Float = 1f) = UGraphics.getCharWidth(this) * textScale

fun String.width(textScale: Float = 1f) = UGraphics.getStringWidth(this) * textScale

@JvmOverloads
fun Number.pixels(alignOpposite: Boolean = false, alignOutside: Boolean = false): PixelConstraint {
    return PixelConstraint(this.toFloat(), alignOpposite, alignOutside)
}

// For 1.pixel()
@JvmOverloads
fun Number.pixel(alignOpposite: Boolean = false, alignOutside: Boolean = false) = pixels(alignOpposite, alignOutside)

fun Number.percent() = RelativeConstraint(this.toFloat() / 100f)
fun Number.percentOfWindow() = RelativeWindowConstraint(this.toFloat() / 100f)

fun Color.toConstraint() = ConstantColorConstraint(this)

operator fun Color.component1() = red
operator fun Color.component2() = green
operator fun Color.component3() = blue
operator fun Color.component4() = alpha
