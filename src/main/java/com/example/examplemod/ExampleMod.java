package com.example.examplemod;

import club.sk1er.elementa.UIConstraints;
import club.sk1er.elementa.components.UIBlock;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.constraints.PixelConstraint;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        UIBlock rectangle = new UIBlock();
        UIConstraints constraints = rectangle.getConstraints();
        constraints.setX(new PixelConstraint(20));

        Window.INSTANCE.addChild(rectangle);
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent event) {
        Window.INSTANCE.draw();
    }
}
