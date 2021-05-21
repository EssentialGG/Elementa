package gg.essential.elementa.state

import gg.essential.elementa.constraints.ConstantColorConstraint
import gg.essential.elementa.constraints.PixelConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import java.awt.Color

fun State<Float>.pixels(
    alignOpposite: Boolean = false,
    alignOutside: Boolean = false
) = PixelConstraint(0f, alignOpposite, alignOutside).bindValue(this)

fun State<Float>.percent() = RelativeConstraint().bindValue(this)

fun State<Color>.toConstraint() = ConstantColorConstraint(Color.WHITE).bindColor(this)
