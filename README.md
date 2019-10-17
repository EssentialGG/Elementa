# Elementa

Elementa (from the name of the first book published on Geometry by Euclid) is a library
that aims to make GUI creation extremely simple. It's based on a couple key concepts, some that
may already be familiar from the browser's DOM.

## Components

All of the drawing in Elementa is done via UIComponents. There is a parent component, `Window`
that forms the root of the component tree. All components have exactly `1` parent, and all components have
`0-n` children.

To create a component, simply instantiate an existing implementation, such as `UIBlock`, 
or extend `UIComponent` yourself.

```kotlin
// Manually create and store a window instance. If this were for a GuiScreen,
// you would need to manually call window.draw() every frame, as well as for the other events.
val window = Window()

val box = UIBlock().childOf(window)
```

## Constraints

All components have a set of constraints that determine its X/Y position, width/height, and color.
The default set of constraints sets a component's x, y, width, height, and color to be 0.

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

Additionally, a component can have a list of effects. Effects deal with special rendering effects.
Currently, there exists only one, `ScissorEffect`, that restricts all drawing to be inside of said
component's bounds.

```kotlin
val box = UIBlock()
    .enableEffects(ScissorEffect())
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
    // Algorithm, length, new constraint
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
box.onHover {
    // Animate, set color, etc.
}
```

There are more examples than solely those two, and they can be found throughout `UIComponent`.

## All together

This is a basic excerpt of code from an Elementa GUI. To see a more fleshed out
example, look to the `SettingsGui` class.

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

box.onHover {
    // Animate, set color, etc.
}
```