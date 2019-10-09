package com.example.examplemod;

import club.sk1er.elementa.UIComponent;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.components.*;
import club.sk1er.elementa.constraints.*;
import club.sk1er.elementa.helpers.Padding;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    private boolean mouseState = false;

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

        if (!Mouse.isCreated()) return;
        if (Mouse.isButtonDown(0) == mouseState) return;
        Window.INSTANCE.click();
        mouseState = Mouse.isButtonDown(0);
    }

    private void createUI() {
        Window.INSTANCE.clearChildren();

        UIBlock settings = new UIBlock(new Color(0, 172, 193, 255));
        settings.getConstraints()
                .setX(new PixelConstraint(5))
                .setY(new PixelConstraint(5))
                .setWidth(new RelativeConstraint(0.3f))
                .setHeight(new FillConstraint(-5));

        UIBlock top = new UIBlock(new Color(0, 124, 145, 255));
        top.getConstraints()
                .setWidth(new FillConstraint())
                .setHeight(new PixelConstraint(20));

        UIText title = new UIText("Settings");
        title.getConstraints()
                .setX(new PixelConstraint(5))
                .setY(new CenterConstraint());

        UIBlock button = new UIBlock(new Color(93, 222, 244, 255));
        button.getConstraints()
                .setX(new PixelConstraint(5, true))
                .setY(new PixelConstraint(5))
                .setWidth(new PixelConstraint(10))
                .setHeight(new PixelConstraint(10));
        button.onClick(() -> {
            Window.INSTANCE.removeChild(settings);
            System.out.println("CLICKED");
        });

        top.addChild(button);
        top.addChild(title);

        settings.addChild(top);
        settings.addChild(createSlider("Slider 1", new PixelConstraint(30)));
        settings.addChild(createSlider("Second Slider", new PixelConstraint(65)));

        UIBlock blocky = new UIBlock(Color.RED);
        blocky.getConstraints()
            .setX(new SiblingConstraint(new Padding(2)))
            .setY(new SiblingConstraint(new Padding(5)))
            .setWidth(new PixelConstraint(75))
            .setHeight(new PixelConstraint(35));

        UIBlock blocky2 = new UIBlock(Color.GREEN);
        blocky2.getConstraints()
            .setX(new CramSiblingConstraint(new Padding(5)))
            .setY(new CramSiblingConstraint(new Padding(5)))
            .setWidth(new PixelConstraint(75))
            .setHeight(new PixelConstraint(15));

        UIBlock blocky3 = new UIBlock(Color.GRAY);
        blocky3.getConstraints()
            .setX(new CramSiblingConstraint(new Padding(5)))
            .setY(new CramSiblingConstraint(new Padding(5)))
            .setWidth(new PixelConstraint(15))
            .setHeight(new PixelConstraint(15));

        UIContainer cont = new UIContainer();
        cont.getConstraints()
            .setHeight(new ChildBasedSizeConstraint())
            .setWidth(new FillConstraint())
            .setX(new SiblingConstraint())
            .setY(new SiblingConstraint());
        cont.addChildren(blocky, blocky2, blocky3);

        settings.addChild(cont);

        Window.INSTANCE.addChild(settings);

        UIImage image = new UIImage("./images/logo.png", "https://avatars3.githubusercontent.com/u/10331479?s=460&v=4");
        image.getConstraints()
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(3))
                .setWidth(new RelativeConstraint(0.2f))
                .setHeight(new AspectConstraint(1));

//        Window.INSTANCE.addChild(image);
    }

    private UIComponent createSlider(String text, PositionConstraint yConstraint) {
        UIContainer container = new UIContainer();
        container.getConstraints()
                .setX(new PixelConstraint(15))
                .setY(yConstraint)
                .setWidth(new FillConstraint(-15))
                .setHeight(new PixelConstraint(30));

        UIText title = new UIText(text);

        UIBlock back = new UIBlock(new Color(64, 64, 64, 255));
        back.getConstraints()
                .setY(new PixelConstraint(12))
                .setWidth(new FillConstraint())
                .setHeight(new PixelConstraint(5));

        UIBlock grab = new UIBlock(new Color(0, 0, 0, 255));
        grab.getConstraints()
                .setX(new PixelConstraint(0))
                .setY(new PixelConstraint(-2))
                .setWidth(new PixelConstraint(3))
                .setHeight(new PixelConstraint(9));

        container.addChildren(title, back.addChild(grab));

        return container;
    }
}
