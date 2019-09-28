package com.example.examplemod;

import club.sk1er.elementa.Constraint;
import club.sk1er.elementa.UIElement;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    final UIElement window = new UIElement(5, 5, 100, 200, new Color(0, 0, 144, 166));
    final UIElement extra = new UIElement(5, 5, 10, 10, new Color(255, 0, 31, 117));



    final UIElement test2 = new UIElement(

    );

    @EventHandler
    public void init(FMLInitializationEvent event) {
        extra.setParent(window);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent event) {
        window.draw();
    }
}
