package club.sk1er.elementa.dsl

import net.minecraft.client.Minecraft

fun String.width() = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this)