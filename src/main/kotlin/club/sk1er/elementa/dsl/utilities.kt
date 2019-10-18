package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.ConstantColorConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import net.minecraft.client.Minecraft
import java.awt.Color

fun String.width() = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this)

@JvmOverloads fun Int.pixels(alignOpposite: Boolean = false) = PixelConstraint(this.toFloat(), alignOpposite)
@JvmOverloads fun Float.pixels(alignOpposite: Boolean = false) = PixelConstraint(this, alignOpposite)

fun Color.asConstraint() = ConstantColorConstraint(this)