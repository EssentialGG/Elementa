package club.sk1er.elementa.state

import club.sk1er.elementa.constraints.ConstantColorConstraint
import club.sk1er.elementa.constraints.PixelConstraint
import club.sk1er.elementa.constraints.RelativeConstraint
import java.awt.Color

fun State<Float>.pixels(
    alignOpposite: Boolean = false,
    alignOutside: Boolean = false
) = PixelConstraint(0f, alignOpposite, alignOutside).bindValue(this)

fun State<Float>.percent() = RelativeConstraint().bindValue(this)

fun State<Color>.asConstraint() = ConstantColorConstraint(Color.WHITE).bindColor(this)
