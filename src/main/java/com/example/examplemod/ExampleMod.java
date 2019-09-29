package com.example.examplemod;

import club.sk1er.elementa.UIConstraints;
import club.sk1er.elementa.components.UIBlock;
import club.sk1er.elementa.components.UIImage;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.constraints.*;
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

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        UIBlock settings = new UIBlock(new Color(0, 172, 193, 255));
        UIConstraints constraints = settings.getConstraints();
        constraints.setX(new PixelConstraint(5));
        constraints.setY(new CenterConstraint());
        constraints.setWidth(new RelativeConstraint(0.3f));
        constraints.setHeight(new FillConstraint(-10));

        UIBlock top = new UIBlock(new Color(0, 124, 145, 255));
        constraints = top.getConstraints();
        constraints.setWidth(new FillConstraint());
        constraints.setHeight(new PixelConstraint(20));

        UIBlock button = new UIBlock(new Color(93, 222, 244, 255));
        constraints = button.getConstraints();
        constraints.setX(new FillConstraint(-10));
        constraints.setY(new PixelConstraint(5));
        constraints.setWidth(new PixelConstraint(10));
        constraints.setHeight(new PixelConstraint(10));

        top.addChild(button);
        settings.addChild(top);
        settings.addChild(createSlider(new PixelConstraint(40)));
        settings.addChild(createSlider(new PixelConstraint(60)));

        Window.INSTANCE.addChild(settings);

        UIImage image = new UIImage("./images/logo.png", "https://avatars3.githubusercontent.com/u/10331479?s=460&v=4");
        constraints = image.getConstraints();
        constraints.setX(new CenterConstraint());
        constraints.setY(new PixelConstraint(3));
        constraints.setWidth(new RelativeConstraint(0.2f));
        constraints.setHeight(new AspectConstraint(1));

        Window.INSTANCE.addChild(image);
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent event) {
        Window.INSTANCE.draw();
    }

    private UIBlock createSlider(PositionConstraint yConstraint) {
        UIBlock slider = new UIBlock(new Color(64, 64, 64, 255));
        UIConstraints constraints = slider.getConstraints();
        constraints.setX(new CenterConstraint());
        constraints.setY(yConstraint);
        constraints.setWidth(new FillConstraint(-30));
        constraints.setHeight(new PixelConstraint(5));

        UIBlock grab = new UIBlock(new Color(0, 0, 0, 255));
        constraints = grab.getConstraints();
        constraints.setX(new PixelConstraint(0));
        constraints.setY(new PixelConstraint(-2));
        constraints.setWidth(new PixelConstraint(3));
        constraints.setHeight(new PixelConstraint(9));

        slider.addChild(grab);

        return slider;
    }
}
