package com.example.examplemod

import gg.essential.elementa.effects.StencilEffect
import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.GuiMainMenu

//#if FORGE
//#if MC<=11202
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
//#else
//$$ import net.minecraftforge.eventbus.api.SubscribeEvent
//$$ import net.minecraftforge.event.TickEvent
//$$ import net.minecraftforge.common.MinecraftForge
//$$ import net.minecraftforge.fml.common.Mod
//$$
//#endif
//#else
//#if FABRIC
//$$ import net.fabricmc.api.ClientModInitializer
//$$ import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
//#endif
//#endif

//#if FABRIC
//$$ class ExampleMod : ClientModInitializer {
//#else
//#if MC<=11202
@Mod(modid = ExampleMod.MOD_ID, version = ExampleMod.MOD_VERSION)
//#else
//$$ @Mod(value = ExampleMod.MOD_ID)
//#endif
class ExampleMod {
//#endif
    //#if FABRIC
    //$$ override fun onInitializeClient() {
    //$$     StencilEffect.enableStencil()
    //$$     ClientTickEvents.START_CLIENT_TICK.register { tick() }
    //$$ }
    //#else
    //#if MC<=11202
    @EventHandler
    fun init(event: FMLInitializationEvent) {
        StencilEffect.enableStencil()
        MinecraftForge.EVENT_BUS.register(this)
    }
    //#else
    //$$ init {
    //$$     StencilEffect.enableStencil()
    //$$     MinecraftForge.EVENT_BUS.register(this)
    //$$ }
    //$$
    //#endif

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) = tick()
    //#endif

    private fun tick() {
        if (UMinecraft.getMinecraft().currentScreen is GuiMainMenu) {
            UMinecraft.getMinecraft().displayGuiScreen(ExamplesGui())
        }
    }

    companion object {
        const val MOD_ID = "examplemod"
        const val MOD_VERSION = "1.0"
    }
}
