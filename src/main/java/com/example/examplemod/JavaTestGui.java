package com.example.examplemod;

import club.sk1er.elementa.UIComponent;
import club.sk1er.elementa.components.UIBlock;
import club.sk1er.elementa.components.Window;
import club.sk1er.elementa.constraints.CenterConstraint;
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint;
import club.sk1er.elementa.constraints.PixelConstraint;
import club.sk1er.elementa.constraints.animation.AnimatingConstraints;
import club.sk1er.elementa.constraints.animation.Animations;
import club.sk1er.elementa.effects.ScissorEffect;
import club.sk1er.mods.core.universal.UniversalScreen;

public class JavaTestGui extends UniversalScreen {
    Window window = new Window();

    UIComponent box = new UIBlock()
        .setX(new CenterConstraint())
        .setY(new PixelConstraint(10f))
        .setWidth(new PixelConstraint(0f))
        .setHeight(new PixelConstraint(36f))
        .setChildOf(window)
        .enableEffect(new ScissorEffect());

    public JavaTestGui() {
        box.onMouseEnterRunnable(() -> {
            // Animate, set color, etc.
            AnimatingConstraints anim = box.makeAnimation();
            anim.setWidthAnimation(Animations.OUT_EXP, 0.5f, new ChildBasedSizeConstraint(2f));
            anim.onCompleteRunnable(() -> {
                // Trigger new animation or anything.
            });
            box.animateTo(anim);
        });
    }
}
