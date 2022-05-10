@file:JvmName("UtilitiesKt_platform")
package gg.essential.elementa.dsl

import gg.essential.elementa.font.DefaultFonts
import gg.essential.elementa.font.FontProvider
import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.util.text.ITextComponent

fun ITextComponent.width(textScale: Float = 1f, fontProvider: FontProvider = DefaultFonts.VANILLA_FONT_RENDERER) =
    UTextComponent(this).formattedText.width(textScale, fontProvider)
