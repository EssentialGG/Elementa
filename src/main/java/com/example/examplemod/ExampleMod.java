package com.example.examplemod;

import club.sk1er.elementa.UIConstraints;
import club.sk1er.elementa.components.UIBlock;
import club.sk1er.elementa.components.UIImage;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.constraints.AspectConstraint;
import club.sk1er.elementa.constraints.CenterConstraint;
import club.sk1er.elementa.constraints.PixelConstraint;
import club.sk1er.elementa.constraints.RelativeConstraint;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.awt.image.BufferedImage;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        UIBlock rectangle = new UIBlock(new Color(255, 255, 255, 100));
        UIConstraints constraints = rectangle.getConstraints();
        constraints.setX(new CenterConstraint());
        constraints.setY(new PixelConstraint(20));
        constraints.setWidth(new RelativeConstraint(0.1f));
        constraints.setHeight(new AspectConstraint(1));

        UIBlock anotherOne = new UIBlock(new Color(0, 0, 0, 100));
        constraints = anotherOne.getConstraints();
        constraints.setX(new PixelConstraint(5));
        constraints.setY(new CenterConstraint());
        constraints.setHeight(new RelativeConstraint(0.5f));
        constraints.setWidth(new AspectConstraint(0.5f));

        rectangle.addChild(anotherOne);

        UIImage image = new UIImage("./images/", "https://avatars3.githubusercontent.com/u/10331479?s=460&v=4");
        constraints = image.getConstraints();
        constraints.setX(new PixelConstraint(5));
        constraints.setY(new CenterConstraint());
        constraints.setWidth(new RelativeConstraint(0.2f));
        constraints.setHeight(new AspectConstraint(1));

        Window.INSTANCE.addChild(rectangle);
        Window.INSTANCE.addChild(image);
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent event) {
        Window.INSTANCE.draw();
    }
}
