## Components

All the drawing in Elementa is done via UIComponents. There is a root component named `Window`
that MUST be in the hierarchy of all components, thus making it the top of the component tree. 
All components have exactly `1` parent, and all components have `0-n` children.

To create a component, simply instantiate an existing implementation such as `UIBlock`, 
or extend `UIComponent` yourself.

```java
// Manually create and store a window instance. The Window is the entry point for Elementa's event system,
// in that you must call events on the window instance manually, the most common of which would be Window#draw.
// This call must be made every frame or else the library will never render your components. If your Gui extends
// Elementa's WindowScreen, this step will already be done and a window will be provided.
Window window = new Window();

// Here we are creating an instance of one of the simplest components available, a UIBlock.
// Next, we have to add it to our hierarchy in some way, and in this instance we want it to be
// a child of the Window. Now that it is in the hierarchy, it will be drawn when we render our Window.
UIComponent box = new UIBlock().setChildOf(window);
```

## Constraints

All components have a set of constraints that determine its X/Y position, width/height, and color.
The default set of constraints sets a component's x, y, width, height to be 0, and color to be Color.WHITE.

A key thing to realize with these components is that everything is relative to its parent. When we
center a component, it will be in the center of its _direct_ parent, whether it is the Window or
perhaps another UIBlock.

This also showcases exactly how declarative the library is. Our code is saying that we would like our box
to be in the center of our parent, and that is all we need to do. No code to figure out how to position it there,
no code to calculate. We simply describe exactly what we want, and Elementa will do the rest for you.

```java
UIComponent box = new UIBlock()
    .setX(new CenterConstraint())
    .setY(new PixelConstraint(10f))
    .setWidth(new PixelConstraint(0f))
    .setHeight(new PixelConstraint(36f));
```

## Effects

Additionally, a component can have a list of effects, special modifiers that can affect the rendering of
a component or its children. One of the most common effects is the `ScissorEffect`. When enabled for
an arbitrary component, this effect restricts all of its children to be drawn inside its own boundaries.
Anything drawn outside that area will simply be cut off. Any component that is not a child (direct or indirect)
of the component where the effect is enabled will not have their rendering affected.

```java
UIComponent box = new UIBlock().enableEffect(new ScissorEffect());
```

## Animations

Elementa also provides a strong animation API. When you make an animation, you set all the
new constraints you would like to animate to, as well as the length (and optionally, delay)
of the animation.

When animating, you have a wide variety of animation strategies (algorithms) to choose from, and you can
of course implement more yourself. All the built-in animation strategies come from
the `Animations` enum.

```java
AnimatingConstraints anim = box.makeAnimation();
anim.setWidthAnimation(Animations.OUT_EXP, 0.5f, new ChildBasedSizeConstraint(2f));
anim.onCompleteRunnable(() -> {
    // Trigger new animation or anything.
});
box.animateTo(anim);
``` 

## Basic Events

Elementa also provides some basic events that can run your animations, or anything else of your choosing.

```java
// Runs a single time when the mouse moves from a state of not hovering to hovering.
box.onMouseEnterRunnable(() -> {
    // Animate, set color, run business logic, etc.
    // Animate, set color, etc.
    
});

AnimatingConstraints anim = box.makeAnimation();
anim.setWidthAnimation(Animations.OUT_EXP, 0.5f, new ChildBasedSizeConstraint(2f));
// This will run when the animation is complete.
// If this animation had multiple "animation components",
// this would trigger when they were all complete.
anim.onCompleteRunnable(() -> {
    // Trigger new animation or anything.
});
box.animateTo(anim);
```

There are many more events than solely those two, and they can be found throughout `UIComponent`.
Keep in mind that all events stem from the Window component, and events must be manually
called on the Window. For example, in order to receive an `onMouseClick` event,
you MUST call Window#mouseClick. In a GuiScreen, this would be done by overriding the `mouseClicked`
method.

## All together

This is a basic excerpt of code from an Elementa GUI. To see a more fleshed out
example, look to the `JavaTestGui` class.

```java
public class TestGui extends WindowScreen {
    UIComponent box = new UIBlock()
        .setX(new CenterConstraint())
        .setY(new PixelConstraint(10f))
        .setWidth(new PixelConstraint(0f))
        .setHeight(new PixelConstraint(36f))
        .setChildOf(getWindow())
        .enableEffect(new ScissorEffect());

    public TestGui() {
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
```
