package com.example.examplemod;

import club.sk1er.elementa.UIComponent;
import club.sk1er.elementa.UIConstraints;
import club.sk1er.elementa.animations.AnimationPhase;
import club.sk1er.elementa.animations.NoAnimationStrategy;
import club.sk1er.elementa.components.*;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.constraints.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
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

        createUI();
    }

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent event) {
        createUI();
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent event) {
        Window.INSTANCE.draw();
    }

    private void createUI() {
        Window.INSTANCE.clearChildren();

        UIBlock settings = new UIBlock(new Color(0, 172, 193, 255));
        UIConstraints constraints = settings.makeDefaultConstraints();
        constraints.setX(new PixelConstraint(5));
        constraints.setY(new CenterConstraint());
        constraints.setWidth(new RelativeConstraint(0.3f));
        constraints.setHeight(new FillConstraint(-10));
        settings.addAnimationPhase(new AnimationPhase(constraints, new NoAnimationStrategy()));
        constraints.setX(new PixelConstraint(10));
        settings.addAnimationPhase(new AnimationPhase(constraints, new NoAnimationStrategy()));

        UIBlock top = new UIBlock(new Color(0, 124, 145, 255));
        constraints = top.makeDefaultConstraints();
        constraints.setWidth(new FillConstraint());
        constraints.setHeight(new PixelConstraint(20));
        top.setConstraints(constraints);

        UIText title = new UIText("Settings");
        constraints = title.makeDefaultConstraints();
        constraints.setX(new PixelConstraint(5));
        constraints.setY(new CenterConstraint());
        title.setConstraints(constraints);

        UIBlock button = new UIBlock(new Color(93, 222, 244, 255));
        constraints = button.makeDefaultConstraints();
        constraints.setX(new FillConstraint(-10));
        constraints.setY(new PixelConstraint(5));
        constraints.setWidth(new PixelConstraint(10));
        constraints.setHeight(new PixelConstraint(10));
        button.setConstraints(constraints);

        top.addChild(button);
        top.addChild(title);

        settings.addChild(top);
        settings.addChild(createSlider("Slider 1", new PixelConstraint(30)));
        settings.addChild(createSlider("Second Slider", new PixelConstraint(60)));

        Window.INSTANCE.addChild(settings);

        UIImage image = new UIImage("./images/logo.png", "https://avatars3.githubusercontent.com/u/10331479?s=460&v=4");
        constraints = image.makeDefaultConstraints();
        constraints.setX(new CenterConstraint());
        constraints.setY(new PixelConstraint(3));
        constraints.setWidth(new RelativeConstraint(0.2f));
        constraints.setHeight(new AspectConstraint(1));
        image.setConstraints(constraints);

        Window.INSTANCE.addChild(image);
    }

    private UIComponent createSlider(String text, PositionConstraint yConstraint) {
        UIContainer container = new UIContainer();
        UIConstraints constraints = container.makeDefaultConstraints();
        constraints.setX(new CenterConstraint());
        constraints.setY(yConstraint);
        constraints.setWidth(new FillConstraint(-30));
        constraints.setHeight(new PixelConstraint(30));
        container.setConstraints(constraints);

        UIText title = new UIText(text);
        title.setConstraints(title.makeDefaultConstraints().setX(new CenterConstraint()));

        UIBlock back = new UIBlock(new Color(64, 64, 64, 255));
        constraints = back.makeDefaultConstraints();
        constraints.setX(new CenterConstraint());
        constraints.setY(new PixelConstraint(12));
        constraints.setWidth(new FillConstraint());
        constraints.setHeight(new PixelConstraint(5));
        back.setConstraints(constraints);

        UIBlock grab = new UIBlock(new Color(0, 0, 0, 255));
        constraints = grab.makeDefaultConstraints();
        constraints.setX(new PixelConstraint(0));
        constraints.setY(new PixelConstraint(-2));
        constraints.setWidth(new PixelConstraint(3));
        constraints.setHeight(new PixelConstraint(9));
        grab.setConstraints(constraints);

        container.addChildren(title, back.addChild(grab));

        return container;
    }
}
