package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.ConstantColorConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import net.minecraft.client.Minecraft
import java.awt.Color

fun String.width() = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this)

fun Int.pixels() = PixelConstraint(this.toFloat())
fun Float.pixels() = PixelConstraint(this)

fun Color.asConstraint() = ConstantColorConstraint(this)