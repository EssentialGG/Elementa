# Components

Elementa provides a wide array of default UIComponents that can combine
to create any number of awesome GUIs. Here they will be described in detail,
and have examples given for how to use them effectively. All the example code
here is compiled into a GUI [here](../src/main/java/com/example/examplemod/ComponentsGui.kt)
that can be played around with and modified to see how each component works.

What the entire playground GUI looks like:

![Components GUI Photo](https://i.imgur.com/bw2VLua.png)

- [UIContainer](#uicontainer)
- [UIBlock](#uiblock)
- [UIText](#uitext)
- [UIWrappedText](#uiwrappedtext)
- [UIRoundedRectangle](#uiroundedrectangle)
- [GradientComponent](#gradient) 
- [UICircle](#uicircle)
- [UIShape](#uishape)
- [UIImage](#uiimage)
- [BlurHashImage](#blurhashimage)
- [UITextInput](#textinput)
- [ScrollComponent](#scrollcomponent)
- [MarkdownComponent](#markdown)
- [SVGComponent](#svg)
- [PlotComponent](#PlotComponent)
- [TreeListComponent](#TreeListComponent)
- [Inspector](#inspector)

### UIContainer

The [UIContainer](../src/main/kotlin/gg/essential/elementa/components/UIContainer.kt) component is the simplest of
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

[UIBlock](../src/main/kotlin/gg/essential/elementa/components/UIBlock.kt) is another extremely basic, but frequently used 
component. It simply renders a monochromatic rectangle (with the color white by default).

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
[UIText](../src/main/kotlin/gg/essential/elementa/components/UIText.kt) being the simpler,
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
the [UIWrappedText](../src/main/kotlin/gg/essential/elementa/components/UIWrappedText.kt) component will do the trick.
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

[UIRoundedRectangle](../src/main/kotlin/gg/essential/elementa/components/UIRoundedRectangle.kt)s are an alternative
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

### Gradient
[GradientComponent](../src/main/kotlin/gg/essential/elementa/components/GradientComponent.kt)s are another alternative 
to `UIBlock` components that take two colours instead of one and will fade from one to the other in a 
[gradient](https://en.wikipedia.org/wiki/Gradient) style. Additionally, the component can optionally 
take a fade direction.

```kotlin
GradientComponent(Color.ORANGE, Color.BLACK).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()
    width = 50.pixels()
    height = 50.pixels()
} childOf this
```

![GradientComponent Example](https://i.imgur.com/XeuO5MZ.png)

### UICircle

[UICircle](../src/main/kotlin/gg/essential/elementa/components/UICircle.kt)s are an interesting component in that they
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

The [UIShape](../src/main/kotlin/gg/essential/elementa/components/UIShape.kt) component also works differently from
most other components. A shape itself has no position, nor a size. Instead, it has a series of
[UIPoint](../src/main/kotlin/gg/essential/elementa/components/UIPoint.kt) elements that describe the shape it should draw.
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
[UIImage](../src/main/kotlin/gg/essential/elementa/components/UIImage.kt) that can render a simple png, jpeg, etc.
The semantics of this component are basically the same as those of the `UIBlock`.

There are multiple ways to load images, including from URL, from a file, or from a jar resource. All image
loading is asynchronous and will not pause the GUI while loading. Until they have loaded, a placeholder
loading image will render in place of the image. It is possible to provide a custom placeholder image by
providing a custom [ImageProvider](../src/main/kotlin/gg/essential/elementa/components/image/ImageProvider.kt).
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

A [BlurHashImage](../src/main/kotlin/gg/essential/elementa/components/image/BlurHashImage.kt) is a placeholder or
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

### TextInput

Plenty of GUIs will require the user to provide keyboard input, often in the form of a text input.
To fulfill this need, Elementa provides [UITextInput](../src/main/kotlin/gg/essential/elementa/components/input/UITextInput.kt)
and [UIMultilineTextInput](../src/main/kotlin/gg/essential/elementa/components/input/UIMultilineTextInput.kt). Both
of these input components are extremely powerful: they support cursor movement, selection via both keyboard and mouse,
copy/paste, undo/redo, and so much more! In order to activate these components, simply give them window focus,
and they will handle the rest. Pressing `<esc>`, on these components or clicking off of them will automatically
deactivate them as well. Enabling `ScissorEffect` on these components is unnecessary, as they already have it enabled
by default.

The first Text Input component is a single-line text input, similar to the type of text box you would find
being used for your browser's search bar. With this type of input, overflowing text makes the box scroll
sideways, and moves the earlier text off to the left.

```kotlin
val box1 = UIBlock(Color(50, 50, 50)).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = 12.pixels()
} childOf this

val textInput1 = UITextInput("My single line text input!").constrain {
    x = 2.pixels()
    y = 2.pixels()

    width = RelativeConstraint(1f) - 6.pixels()
} childOf box1

box1.onMouseClick { textInput1.grabWindowFocus() }
```

The other type of Text Input is a multi-line text input component. This is the type of text box used for
Discord's message box. It supports text wrapping across lines, new-lines, scrolling vertically, and more.

```kotlin
val box2 = UIBlock(Color(50, 50, 50)).constrain {
    x = 2.pixels()
    y = SiblingConstraint() + 5.pixels()

    width = 100.pixels()
    height = ChildBasedSizeConstraint() + 4.pixels()
} childOf this

val textInput2 = UIMultilineTextInput("My multiline text input!").constrain {
    x = 2.pixels()
    y = 2.pixels()

    width = RelativeConstraint(1f) - 6.pixels()
}.setMaxLines(4) childOf box2

box2.onMouseClick { textInput2.grabWindowFocus() }
```

Note: Make sure you are passing mouse & keyboard events to your window if your inputs are not working. (Or just use 
[WindowScreen](../src/main/kotlin/gg/essential/elementa/WindowScreen.kt)!)

The inputs before selecting or typing:

![UITextInput Example before typing](https://i.imgur.com/PKvkoUT.png)

The text inputs after typing:

![UITextInput Example after typing](https://i.imgur.com/gBIH4bn.png)

It's worth booting up the `ComponentsGui` playground to see how these inputs work and feel.

### ScrollComponent

Oftentimes we will need to put an arbitrary amount of components into a certain area, and in
order to make sure they are all visible, we need to be able to scroll in that area. Luckily, Elementa
again provides an extremely easy way to accomplish this, a
[ScrollComponent](../src/main/kotlin/gg/essential/elementa/components/ScrollComponent.kt). Scroll components have a fixed
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

### Markdown

A [MarkdownComponent](../src/main/kotlin/gg/essential/elementa/markdown/MarkdownComponent.kt) is used to render
any Markdown document natively. This is a great way to display rich text in your GUI, whether it be changelogs or
whatever you require. Simply pass your markdown document to the `MarkdownComponent`'s constructor, where it is then
parsed and ready to be rendered!

```kotlin
 MarkdownComponent(
    """
        # Markdown!
        
        This is pretty cool. We can now render arbitrary markdown beautifully.
        
        ```
        We even have code :)
        ```
    """.trimIndent()
).constrain {
    x = 2.pixels()
    y = SiblingConstraint(padding = 2f)
    width = 200.pixels()
    height = 100.pixels()
} childOf this
```

`MarkdownComponent` in action:

![MarkdownComponent Example](https://i.imgur.com/VATxhMk.png)

### SVG

An [SVGComponent](../src/main/kotlin/gg/essential/elementa/components/SVGComponent.kt) is used to render
(simple!) SVG documents natively. This is extremely useful for high resolution icons in your GUI, though keep in mind,
the Elementa SVG parser/renderer are very simple, and support an extremely limited subset of the SVG standard. To ensure
your icon will properly render, please use icons from [TablerIcons](https://github.com/tabler/tabler-icons).

```kotlin
SVGComponent.ofResource("/svg/test.svg").constrain {
    x = 2.pixels()
    y = SiblingConstraint(padding = 2f)
    width = 50.pixels()
    height = 50.pixels()
} childOf this
```

The test.svg file is the following:

```svg
<svg width="24"
     height="24"
     viewBox="0 0 24 24"
     stroke-width="2"
     stroke="currentColor"
     fill="none"
     stroke-linecap="round"
     stroke-linejoin="round">

    <circle cx="12" cy="12" r="10" />
    <circle cx="12" cy="12" r="4" />
    <line x1="21.17" y1="8" x2="12" y2="8" />
    <line x1="3.95" y1="6.06" x2="8.54" y2="14" />
    <line x1="10.88" y1="21.94" x2="15.46" y2="14" />
</svg>
```

When you select an icon from TablerIcons, it is best practice to simply copy the above file into your project's
resources folder, replacing the inner `<circle>` and `<line>` elements with your chosen icon's inner svg elements.
If you encounter an issue with the line-caps looking odd at certain scales, you may wish to remove the `stroke-linecap`
and `stroke-linejoin` attributes from the topmost `<svg>` element in your SVG file.

`SVGComponent` in action:

![SVGComponent Example](https://i.imgur.com/Rp5khlc.png)

### PlotComponent

The [PlotComponent](../src/main/kotlin/gg/essential/elementa/components/plot/PlotComponent.kt), as its name implies,
allow the user to display a graph of information to the user. The great thing about this component is that it is
extremely customizable. Everything can be changed, from the axis labels to the line widths. Lets look at a basic 
example:

```kotlin
PlotComponent(listOf(
    GraphPoint(0, 6),
    GraphPoint(1, 1.3),
    GraphPoint(2, 8.5),
    GraphPoint(3, 3),
    GraphPoint(4, 5),
    GraphPoint(5, 10),
    GraphPoint(6, 0)
))
```

With no styling applied, using only the defaults provided by the component, we get a pretty good looking graph:

![basic graph component](https://i.imgur.com/miJmnqz.png)

However, let's apply some basic styling to improve the look:

```kotlin
PlotComponent(
    listOf(
        GraphPoint(0, 6),
        GraphPoint(1, 1.3),
        GraphPoint(2, 8.5),
        GraphPoint(3, 3),
        GraphPoint(4, 5),
        GraphPoint(5, 10),
        GraphPoint(6, 0)
    ),
    xBounds = Bounds(0, 6, 7, showLabels = true, labelColor = Color(101, 101, 101)),
    yBounds = Bounds(0, 10, 4, showLabels = true, labelColor = Color(101, 101, 101)),
    style = PlotStyle(
        lineStyle = LineStyle(color = Color(1, 165, 82), width = 3f),
        padding = Padding(10, 6, 10, 4)
    )
)
```

Now we can see the true power of this component -- with only six lines of styling, we now get this:

![cool graph component](https://i.imgur.com/KqTlBZ1.png)

The `PlotStyle` class has many more configuration options that aren't shown above. Check it out 
[here](../src/main/kotlin/gg/essential/elementa/components/plot/PlotStyle.kt)!

### TreeListComponent

The [TreeListComponent](../src/main/kotlin/gg/essential/elementa/components/TreeListComponent.kt) is a component which 
allows the display of information in a tree. It allows the user to provide a tree-like UIComponent hierarchy, as well as 
a component to use as the "arrow" (the icon that is clicked to expand or contract a node), and takes care of the 
component layout. 

To start, you will need a class that inherits from the abstract 
[TreeNode](../src/main/kotlin/gg/essential/elementa/components/TreeListComponent.kt) class. A node that simply displays 
some text
would look like the following:

```kotlin
class TextNode(private val text: String) : TreeNode() {
    override fun getPrimaryComponent(): UIComponent {
        return UIText(text).constrain {
            x = SiblingConstraint()
        }   
    }
    
    override fun getArrowComponent(): TreeArrowComponent {
        return SimpleArrowComponent()
    }
}
```

Note that we also have to provide a component to serve as the clickable open/close button of the tree -- which we call 
the [TreeArrowComponent](../src/main/kotlin/gg/essential/elementa/components/TreeListComponent.kt) -- in the method 
`getArrowComponent`. A `TreeArrowComponent` is simply a component with two abstract functions: `open` and `close`, which
are called when the user changes the state of that particular `TreeNode`. This is important for showing the user which 
nodes are expanded and which nodes are not. Note that no arrow component will be rendered if a node has no children

Now that we have a node class, let's create a `TreeListComponent`. We provide a Kotlin DSL for creating a Node structure 
easily:

```kotlin
val rootNode = TextNode("root node").withChildren {
    add(TextNode("item 1"))
    add(TextNode("item 2").withChildren {
        add(TextNode("sub-item 1"))
        add(TextNode("sub-item 2"))
        add(TextNode("sub-item 3"))
    })
    add(TextNode("item 3"))
}

val TreeListComponent = TreeListComponent(rootNode).constrain {
    // ...
}
```

Note that you can provide a list of `TreeNode`s to the `TreeListComponent` constructor to have multiple roots. All nodes of the
tree start in a closed position.

The `TreeListComponent` is convenient because the user only has to worry about the layout of each particular node. The user
does not have to worry about aligning the children, or even aligning the arrow component.

A few things to note:
- You can provide a list of `TreeNode`s to the `TreeListComponent` constructor to have multiple roots.
- All nodes of the tree start in a closed position, and the `close` methods of the `TreeArrowComponent`s are _not_ 
initially called.
- Components returned from `getPrimaryComponent` must have constraints that do not depends on their parent. The 
`TreeNode` makes heavy use of child-based constraints, and thus the children must be absolutely resolvable.

### TreeGraphComponent 

The [TreeGraphComponent](../src/main/kotlin/gg/essential/elementa/components/TreeGraphComponent.kt) has a similar API as 
the `TreeListComponent`, and the exact same use case, however it displays information in a top-down visual style instead
of a collapsible list. It can be used in place of the `TreeListComponent` when there is ample room to display it, as it 
takes up quite a bit of space. 

### Inspector

The [Inspector](../src/main/kotlin/gg/essential/elementa/components/inspector/Inspector.kt) is a very handy tool used for
debugging/inspecting your Elementa GUIs. It is meant to be similar to a browser's "Inspect Element" tool. It uses a
[TreeListComponent](../src/main/kotlin/gg/essential/elementa/components/TreeListComponent.kt) to display the component hierarchy starting
from a specific root component. The `Inspector` can also be used to show the current position, size, color, etc.
constraints of components, as well as their value as of the current frame.

In order to create an Inspector, use the code below where the first parameter to `Inspector` is the root node to inspect,
often the window. Though make sure to remove said code when deploying your GUI to production, as most users will not
enjoy such a distraction :)

```kotlin
Inspector(window).constrain {
    x = 10.pixels(true)
    y = 10.pixels(true)
} childOf window
```

The `Inspector` in action:

![Inspector Example](https://i.imgur.com/l7eku4p.png)