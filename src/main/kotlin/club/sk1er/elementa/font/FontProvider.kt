package club.sk1er.elementa.font

import club.sk1er.elementa.constraints.SuperConstraint
import java.awt.Color

interface FontProvider: SuperConstraint<FontProvider> {

    fun getStringWidth(string: String, pointSize: Float): Float

    fun drawString(
        string: String,
        color: Color,
        x: Float,
        y: Float,
        originalPointSize: Float, //Unused for MC font
        shadow: Boolean = true,
        shadowColor: Color? = null
    )

}