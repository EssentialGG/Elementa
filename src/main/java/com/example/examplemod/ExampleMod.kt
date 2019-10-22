package com.example.examplemod

import club.sk1er.elementa.effects.StencilEffect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@Mod(modid = ExampleMod.MOD_ID, version = ExampleMod.MOD_VERSION)
class ExampleMod {
    @EventHandler
    fun init(event: FMLInitializationEvent) {
        StencilEffect.enableStencil()

        MinecraftForge.EVENT_BUS.register(this)
        ClientCommandHandler.instance.registerCommand(ExampleCommand())
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (gui != null) {
            Minecraft.getMinecraft().displayGuiScreen(gui)
            gui = null
        }
    }

    companion object {
        const val MOD_ID = "examplemod"
        const val MOD_VERSION = "1.0"
        var gui: GuiScreen? = null
    }
}