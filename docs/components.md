# Components

Elementa provides a wide array of default UIComponents that can combine
to create any number of awesome GUIs. Here they will be described in detail,
and have examples given for how to use them effectively. All the example code
here is compiled into a GUI [here](../src/main/java/com/example/examplemod/ComponentsGui.kt)
that can be played around with and modified to see how each component works.

- [UIContainer](#uicontainer)
- [UIBlock](#uiblock)
- [UIText](#uitext)
- [UIWrappedText](#uiwrappedtext)
- [UIRoundedRectangle](#uiroundedrectangle)
- [UICircle](#uicircle)
- [UIShape](#uishape)
- [UIImage](#uiimage)
- [BlurHashImage](#blurhashimage)
- [UITextInput](#uitextinput)
- [ScrollComponent](#scrollcomponent)

### UIContainer

The [UIContainer](../src/main/kotlin/club/sk1er/elementa/components/UIContainer.kt) component is the simplest of
all components as it does not do any rendering whatsoever. It simply serves to be a "holder" or 
parent to a group of children components. It can be considered analogous
to a `<div>` element in the HTML world.

For example, if I wished to right-align a series of components, it makes
a lot of sense to simply wrap said components in a UIContainer and right-align
the container.

```kotlin
val bar = UIBlock().constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()
    width = 150.pixels()
    height = 50.pixels()
} childOf this

val container = UIContainer().constrain {
    x = 0.pixels(true)
    width = ChildBasedSizeConstraint(padding = 2f)
    height = ChildBasedMaxSizeConstraint()
} childOf bar effect OutlineEffect(Color.BLUE, 2f)

repeat(3) {
    UIBlock(Color.RED).constrain {
        x = SiblingConstraint(padding = 2f)
        width = 25.pixels()
        height = 25.pixels()
    } childOf container
}
```

The code above produces the following result:

![UIContainer Example](https://i.imgur.com/NvZIFU6.png)

With the UIContainer's bounding box outlined:

![UIContainer Outlined](https://i.imgur.com/aFs66wQ.png)

### UIBlock

[UIBlock](../src/main/kotlin/club/sk1er/elementa/components/UIBlock.kt) is another extremely basic, but frequently used component.
It simply renders a monochromatic rectangle (with the color white by default).

```kotlin
UIBlock().constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()
    width = 50.pixels()
    height = 50.pixels()
} childOf this
```

![UIBlock Example](https://i.imgur.com/ssjBSPJ.png)

### UIText

Text can be found in almost every GUI ever, and therefore has thorough support in Elementa.
In fact, we provide two different types of text displays, with
[UIText](../src/main/kotlin/club/sk1er/elementa/components/UIText.kt) being the simpler,
non-wrapping version. This means that text will not respect new-lines, nor will it wrap
around when the text is longer than its bounding width.

It is relevant to mention that `UIText` components do not need an explicit width or height,
as these values default to the actual width and height of the provided string.

```kotlin
UIText("This is my non-wrapping text").constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()
    
    // I have no need to set a width/height, UIText sets those to the
    // inherent width/height of the passed string.
} childOf this
```

Text can also be scaled up or down to fit the size you need.

```kotlin
UIText("I can scale!").constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()
    
    textScale = (1.5f).pixels()
} childOf this
```

Minecraft text also normally has a shadow behind it, but if you wish for your text
to have no shadow, you can disable it with a constructor parameter.

```kotlin
UIText("Shadowless...", shadow = false).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()
} childOf this
```

As you can see in the following picture, the text will not wrap,
it instead is making its parent expand to accommodate its entire width.
If the parent were a fixed-width component, the text would simply overflow
past the boundaries.

![UIText Example](https://i.imgur.com/kVJExr5.png)

### UIWrappedText

Sometimes however, you may wish to have text wrap or respect new-lines. In these cases,
the [UIWrappedText](../src/main/kotlin/club/sk1er/elementa/components/UIWrappedText.kt) component will do the trick.
UIWrappedText should have an explicit width constraint provided to it, otherwise it will behave as a normal
`UIText` component. Wrapped text support text scaling and disabling shadow just as normal text components do.

```kotlin
UIWrappedText("This is my text that is wrapping at 50 pixels!").constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 50.pixels()
} childOf this
```

Sometimes you will want your text to be centered on each wrapped line, so wrapped text provides
a constructor parameter for that, `centered`.

```kotlin
UIWrappedText("I'm going to wrap at 50 pixels, but centered :)", centered = true).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 50.pixels()
} childOf this
```

![UIWrappedText Example](https://i.imgur.com/PZZX4o0.png)

### UIRoundedRectangle

[UIRoundedRectangle](../src/main/kotlin/club/sk1er/elementa/components/UIRoundedRectangle.kt)s are an alternative
to simple `UIBlock` components that can provide a little flair if needed. All rounded rectangles take
a `radius` parameter to determine how much to round the corners of the rectangle.
Higher values indicate more rounded corners as seen below.

```kotlin
UIRoundedRectangle(2f).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 50.pixels()
} childOf this

UIRoundedRectangle(10f).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 50.pixels()
} childOf this
```

![UIRoundedRectangle Example](https://i.imgur.com/FL5R68P.png)

### UICircle

[UICircle](../src/main/kotlin/club/sk1er/elementa/components/UICircle.kt)s are an interesting component in that they
do not use the width & height constraints. Instead, they deal with the x, y, and radius constraints.
The `x` and `y` position of the circle specifies the center of the circle rather than the top-left corner
like most other components. The radius constraint can have the value of any other size (width/height) constraint.

```kotlin
UICircle().constrain {
    // These x & y positions describe the CENTER of the circle
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    // We do not specify the width & height of the circle, rather, we specify
    // its radius.
    radius = 10.pixels()
} childOf this
```

If our radius is a simple number, we can pass it to `UICircle`'s constructor for simplicity.
The previous and following should look exactly the same.

```kotlin
UICircle(10f).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()
} childOf this
```

The previous examples produce the following result:

![UICircle Example](https://i.imgur.com/b5FXjTA.png)

With outlines enabled to show the bounding box of the circles:

![UICircle Outline Example](https://i.imgur.com/jlDtsXE.png)

### UIShape

The [UIShape](../src/main/kotlin/club/sk1er/elementa/components/UIShape.kt) component also works differently from
most other components. A shape itself has no position, nor a size. Instead, it has a series of
[UIPoint](../src/main/kotlin/club/sk1er/elementa/components/UIPoint.kt) elements that describe the shape it should draw.
UIPoints are also interesting because they are an infinitesimally small point with no size, only a position.

Note: `UIPoint`s can be animated just like any other component, which means the shape itself is animatable.

```kotlin
val shapeHolder = UIContainer().constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 50.pixels()
    height = 40.pixels()
} childOf this

(UIShape() childOf shapeHolder).apply {
    // Must be called after [childOf] because [addVertex] requires a parent at call
    addVertex(UIPoint(
        x = 15.pixels(),
        y = 10.pixels()
    ))

    addVertex(UIPoint(
        x = 16.pixels(),
        y = 30.pixels()
    ))

    addVertex(UIPoint(
        x = 36.pixels(),
        y = 24.pixels()
    ))

    addVertex(UIPoint(
        x = 32.pixels(),
        y = 15.pixels()
    ))

    addVertex(UIPoint(
        x = 10.pixels(),
        y = 4.pixels()
    ))
}
```

![UIShape Example](https://i.imgur.com/z8iv68F.png)

### UIImage

Elementa also provides first-class support for all types of images. The simplest of these is the basic
[UIImage](../src/main/kotlin/club/sk1er/elementa/components/UIImage.kt) that can render a simple png, jpeg, etc.
The semantics of this component are basically the same as those of the `UIBlock`.

There are multiple ways to load images, including from URL, from a file, or from a jar resource. All image
loading is asynchronous and will not pause the GUI while loading. Until they have loaded, a placeholder
loading image will render in place of the image. It is possible to provide a custom placeholder image by
providing a custom [ImageProvider](../src/main/kotlin/club/sk1er/elementa/components/image/ImageProvider.kt).
A good, pre-existing placeholder option is a [BlurHashImage](#blurhashimage).

```kotlin
UIImage.ofURL(URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 50.pixels()
} childOf this
```

However, the width & height that I provided for the image makes it look squished. If I wished for the
image to maintain its aspect ratio, I simply need to specify either the width or height, and then have the other
be an `ImageAspectConstraint`.

```kotlin
UIImage.ofURL(URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 150.pixels()
    height = ImageAspectConstraint()
} childOf this
```

![UIImage Example](https://i.imgur.com/L3Zxau8.png)

### BlurHashImage

A [BlurHashImage](../src/main/kotlin/club/sk1er/elementa/components/image/BlurHashImage.kt) is a placeholder or
thumbnail style image that comes from the [BlurHash](https://blurha.sh) project.
You can read more about BlurHashes on their website, but in short, they are a simple blurred image that can be
described by a 20-30 character string, making them very efficient to pass along the network before loading an image.
This makes them a perfect placeholder for a [UIImage](#uiimage).

Firstly, I can make a static `BlurHashImage` that will act just like a `UIImage`, but comes from a BlurHash.

```kotlin
BlurHashImage("L4ESU,OD1e#:=GwwJSAr1M,r|]Ar").constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 50.pixels()
} childOf this
```

The other important use of a `BlurHashImage` is as a placeholder for a `UIImage` while it loads. The example
below will render the BlurHash image until the image from the provided URL finishes loading, at which time
the newly loaded image will render instead of the BlurHash image.

```kotlin
BlurHashImage.ofURL("L4ESU,OD1e#:=GwwJSAr1M,r|]Ar", URL("https://i.imgur.com/Pc6iMw3.png")).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 50.pixels()
} childOf this
```

The following image was taken after the bottom image had finished loading, but until then both the
top and bottom images looked identical. To see this dynamic loading in action, run the `ComponentsGui`! 

![BlurHashImage Example](https://i.imgur.com/Jb8phls.png)

### UITextInput

Plenty of GUIs will require the user to provide keyboard input, often in the form of a text input.
To fulfill this need, Elementa provides [UITextInput](../src/main/kotlin/club/sk1er/elementa/components/UITextInput.kt).
Text inputs by default try to wrap their text when they reach their maximum width, providing a multi-line input area.
When you wish to make a text input active, you simply need to set the `active` field to true. In addition,
text inputs can have placeholder text in place before the user begins typing.
These can be seen in the following example.

Note: Make sure you are passing mouse & keyboard events to your window if your inputs are not working.

```kotlin
val box1 = UIBlock(Color(50, 50, 50)).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 50.pixels()
} childOf this

val textInput1 = UITextInput("My placeholder text").constrain {
    x = 2.pixels()
    y = 2.pixels()

    width = RelativeConstraint(1f) - 2.pixels()
} childOf box1

box1.onMouseClick { _, _, _ ->
    textInput1.active = true
}
```

`UITextInputs` can also be non-wrapping, making them single-line inputs. Non-wrapped text inputs
intelligently reposition the text so that while typing, the end of the text is always visible.
This means we need to enable the `ScissorEffect` on our box component to avoid seeing the overflowing text. 

```kotlin
val box2 = UIBlock(Color(50, 50, 50)).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 12.pixels()
} childOf this effect ScissorEffect()

val textInput2 = UITextInput("My placeholder text", wrapped = false).constrain {
    x = 2.pixels()
    y = 2.pixels()

    width = RelativeConstraint(1f) - 2.pixels()
} childOf box2

box2.onMouseClick { _, _, _ ->
    textInput2.active = true
}
```

In this example, we would also like to deactivate text input 1 when text input 2 is activated, and vice versa.
This is as simple as setting active to false on the opposite component.

```kotlin
box1.onMouseClick { _, _, _ ->
    textInput1.active = true
    textInput2.active = false
}

box2.onMouseClick { _, _, _ ->
    textInput1.active = false
    textInput2.active = true
}
```

The inputs before selecting or typing:

![UITextInput Example before typing](https://i.imgur.com/g5bvkZu.png)

The text inputs after typing:

![UITextInput Example after typing](https://i.imgur.com/8jqEQvL.png)

It's worth booting up the `ComponentsGui` playground to see how this inputs work and feel.

### ScrollComponent

Oftentimes we will need to put an arbitrary amount of components into a certain area, and in
order to make sure they are all visible, we need to be able to scroll in that area. Luckily, Elementa
again provides an extremely easy way to accomplish this, a
[ScrollComponent](../src/main/kotlin/club/sk1er/elementa/components/ScrollComponent.kt). Scroll components have a fixed
height, and you can add children to them just like any other component. In theory, they should be treated just
like a `UIContainer`.

A basic scroll component with a few components would look like the following:

```kotlin
val scroll1 = ScrollComponent().constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 150.pixels()
    height = 75.pixels()
} childOf this

repeat(5) {
    UIBlock(Color.RED).constrain {
        x = CenterConstraint()
        y = SiblingConstraint(padding = 2f)

        width = 50.pixels()
        height = 25.pixels()
    } childOf scroll1
}
```

Scroll components are also very flexible, and can be filtered, emptied, etc. While these actions themselves
are beyond the scope of this document, they can result in an empty scroll area. Due to this, we can provide
the component with a piece of text to display when it is empty, like so:

```kotlin
ScrollComponent("I'm empty :(").constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 150.pixels()
    height = 75.pixels()
} childOf this
```

In addition, scroll components can easily have scroll bars added, in order to do so follow the instructions on
the `setScrollBarComponent` function in the `ScrollComponent` class.

What the scroll components look like normally:

![ScrollComponent Example](https://i.imgur.com/dUA01gl.png)

What the scroll components look like with debug outlines enabled:

![ScrollComponent Outline Example](https://i.imgur.com/KiZGsa7.png)