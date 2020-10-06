# Elementa

Elementa (from the name of the first book published on [Geometry by Euclid](http://farside.ph.utexas.edu/Books/Euclid/Elements.pdf)) is a library
that aims to make GUI creation extremely simple. It's based on a couple key concepts, some of which
may already be familiar to those who have worked with a browser's DOM.

The library is based around the idea of being [declarative](https://en.wikipedia.org/wiki/Declarative_programming).
This is a shift from how one would normally do graphics programming in Minecraft, or most other coding in general.
In Elementa, you do not have to write code to calculate _how_ to place a component at a certain point on the screen,
instead you simply have to describe _what_ you want.

## Dependency

In your repository block, add:
```groovy
maven {
    url = "https://repo.sk1er.club/repository/maven-public"
}
```

In your dependencies block, add:

```groovy
implementation "club.sk1er:Elementa:1.7.1-$mcVersion"
```

## 2.0.0 Snapshots

To use the latest snapshot, use the following dependency:

```groovy
implementation "club.sk1er:Elementa:129-$mcVersion-SNAPSHOT"
```

If you were previously using v1.7.1 of Elementa and are now on the v2.0.0 snapshots, please refer to the
[migration](docs/migration.md) document to know what has changed.

To learn about all the new features in v2.0.0, please read the [what's new](docs/whatsnew.md) document.

## Components

All the drawing in Elementa is done via UIComponents. There is a root component named `Window`
that MUST be in the hierarchy of all components, thus making it the top of the component tree. 
All components have exactly `1` parent, and all components have `0-n` children.

To create a component, simply instantiate an existing implementation such as `UIBlock`, 
or extend `UIComponent` yourself.

```kotlin
// Manually create and store a window instance. The Window is the entry point for Elementa's event system,
// in that you must call events on the window instance manually, the most common of which would be Window#draw.
// This call must be made every frame or else the library will never render your components. In the case of
// drawing in a GuiScreen, you would call this method from your overriden GuiScreen#drawScreen method.
val window = Window()

// Here we are creating an instance of one of the simplest components available, a UIBlock.
// Next, we have to add it to our hierarchy in some way, and in this instance we want it to be
// a child of the Window. Now that it is in the hierarchy, it will be drawn when we render our Window.
val box = UIBlock(Color.RED /* java.awt.Color */).childOf(window)
```

A showcase of all the components provided by Elementa:

![Playground GUI Photo](https://i.imgur.com/z9eJPik.png)

Read more about all of these components [here](docs/components.md).

## Constraints

All components have a set of constraints that determine its X/Y position, width/height, and color.
The default set of constraints sets a component's x, y, width, height to be 0, and color to be Color.WHITE.

A key thing to realize with these components is that everything is relative to its parent. When we
center a component, it will be in the center of its _direct_ parent, whether it is the Window or
perhaps another UIBlock.

This also showcases exactly how declarative the library is. Our code is saying that we would like our box
to be in the center of our parent, and that is all we need to do. No code to figure out how to position it there,
no code to calculate. We simply describe exactly what we want, and Elementa will do the rest for you.

```kotlin
val box = UIBlock()
    .constrain {
      x = CenterConstraint()
      y = PixelConstraint(10f)
      width = PixelConstraint(0f)
      height = PixelConstraint(36f)
    }
```

## Effects

Additionally, a component can have a list of effects, special modifiers that can affect the rendering of
a component or its children. The most commonly used effect as of now is the `ScissorEffect`. When enabled for
an arbitrary component, this effect restricts all of it's children to be drawn inside of its own boundaries.
Anything drawn outside of that area will simply be cut off. Any component that is not a child (direct or indirect)
of the component where the effect is enabled will not have their rendering affected.

```kotlin
val box = UIBlock() effect ScissorEffect()
```

## Animations

Elementa also provides a strong animation API. When you make an animation, you set all of the
new constraints you would like to animate to, as well as the length (and optionally, delay)
of the animation.

When animating, you have a wide variety of animation algorithms to choose from, and you can
feel free to implement custom ones yourself. All of the built-in animation algorithms come from
the `Animations` enum.

```kotlin
box.animate {
    // Algorithm, length, new constraint, and optionally, delay.
    // All times are in seconds.
    setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint(2f))
}
``` 

## Basic Events

Elementa also provides some basic events that can run your animations, or anything else of your choosing.

```kotlin
box.animate {
    setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint(2f))
    
    // This will run when the animation is complete.
    // If this animation had multiple "animation components",
    // this would trigger when they were all complete.
    onComplete {
        // Trigger new animation or anything.    
    }
}

// Runs a single time when the mouse moves from a state of not hovering to hovering.
box.onMouseEnter {
    // Animate, set color, run business logic, etc.
}
```

There are many more events than solely those two, and they can be found throughout `UIComponent`.
Keep in mind that all events stem from the Window component, and events must be manually
called on the Window. For example, in order to receive an `onMouseClick` event,
you MUST call Window#mouseClick. In a GuiScreen, this would be done by overriding the `mouseClicked`
method.

## All together

This is a basic excerpt of code from an Elementa GUI. To see a more fleshed out
example, look to the [ExampleGui class](src/main/java/com/example/examplemod/ExampleGui.kt).

```kotlin
val window = Window()

val box = UIBlock()
    .constrain {
      x = CenterConstraint()
      y = PixelConstraint(10f)
      width = PixelConstraint(0f)
      height = PixelConstraint(36f)
    }
    .childOf(window)
    .enableEffects(ScissorEffect())

box.animate {
    setWidthAnimation(Animations.OUT_EXP, 0.5f, ChildBasedSizeConstraint(2f))

    onComplete {
        // Trigger new animation or anything.    
    }
}

box.onMouseEnter {
    // Animate, set color, etc.
}
```
