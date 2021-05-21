package gg.essential.elementa.font

import gg.essential.elementa.constraints.SuperConstraint
import java.awt.Color

interface FontProvider : SuperConstraint<FontProvider> {

    fun getStringWidth(string: String, pointSize: Float): Float

    fun drawString(
        string: String,
        color: Color,
        x: Float,
        y: Float,
        originalPointSize: Float, //Unused for MC font
        scale: Float,
        shadow: Boolean = true,
        shadowColor: Color? = null
    )

}