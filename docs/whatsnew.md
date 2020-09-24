# v2.0.0

## Components

### TextInput

There are two newly updated TextInput components now: [UITextInput](../src/main/kotlin/club/sk1er/elementa/components/input/UITextInput.kt)
and [UIMultilineTextInput](../src/main/kotlin/club/sk1er/elementa/components/input/UIMultilineTextInput.kt). These are
very powerful and useful classes. Read more about these components and how to use them [here](components.md#textinput).

### Inspector

The Inspector component is a great tool for debugging what is happening in your Elementa GUIs. It gives you the ability
to see the component hierarchy, view the bounding box of components, view what constraints are being used, and much more.
Read more about how to use the Inspector [here](components.md#inspector).

### Markdown

Elementa v2.0.0 now has a full-fledged Markdown parser and renderer. This is a very useful component for rendering
rich text. Read more about the features of the `MarkdownComponent` and how to use it [here](components.md#markdown).

### GraphComponent

The [GraphComponent](../src/main/kotlin/club/sk1er/elementa/components/graph/GraphComponent.kt) allows the user to 
display an extremely customizable point graph. Nearly every aspect of the way the graph is drawn is configurable. Read more
[here](components.md#graphcomponent)

### TreeListComponent

Added a new [TreeListComponent](../src/main/kotlin/club/sk1er/elementa/components/TreeListComponent.kt) component. It is 
used to display tree-like information in a collapsible list-like hierarchy. This is the primary component used by the 
Inspector, and is highly customizable. Read more [here](components.md#TreeListComponent)

### TreeGraphComponent

Not to be confused with the `GraphComponent`, which displays plot data, the 
[TreeGraphComponent](../src/main/kotlin/club/sk1er/elementa/components/TreeGraphComponent.kt) displays nodes in a 
top-down tree view. It shows the exact same information as the `TreeListComponent` but in a different visual style. Read
more [here](components.md#TreeGraphComponent).

### WindowScreen

There is now a [WindowScreen](../src/main/kotlin/club/sk1er/elementa/WindowScreen.kt) class provided for an easy way to
avoid the boilerplate of calling all of the `Window` events manually. Rather than extending `GuiScreen` (or `Screen` in
new Minecraft versions) in your GUI class, you can simply extend `WindowScreen` and not worry about overriding the
`keyTyped`, `mouseReleased`, etc. methods. Additionally, it automatically declares a `Window` instance for you to use,
and sets up the default key event listeners to that `Window` instance, allowing your users to press `<escape>` to leave
the GUI.

### UIComponent

#### Children Insertion Helpers

The utility functions `insertChildAt`, `insertChildBefore`, `insertChildAfter`, and `replaceChild`, have been added to
easily place components inside a parent's child hierarchy at useful positions. 

#### Hide API

By default, when calling `UIComponent#hide`, it will wait for the next animation frame before actually removing that
child component from the hierarchy. While this is necessary to give the `beforeHideAnimation` a chance to run,
sometimes it isn't what you want, so now the `hide` function has an `instantly` parameter which, when set to true,
will skip any animations and immediately remove the component.

#### Floating API

Sometimes, when popping up a modal or a tooltip, that component should receive click events first, no matter where
it might be in the component hierarchy, because you have programmed it in a way where you _know_ it is on top of
everything else. In order to facilitate this, you can call `UIComponent#setFloating(true)`. This means that the
component will receive priority on all click events until you set it to be no longer floating.

### SVGComponent

The SVG component now supports dynamically changing its underlying SVG, as show in the following example:

```kotlin
svgComponent.setSVG(SVGParser.parseFromResource("/image.svg"))
``` 

### ScrollComponent

The ScrollComponent now supports hiding the scrollbar when all the scroll pane's children fit inside of itself, i.e.
when there is nothing to actually scroll. To enable this option, pass the `hideWhenUseless = true` argument to the
`ScrollComponent#setScrollBarComponent` function.

The ScrollComponent also has overridden many parent `UIComponent` functions to work properly, such as `childrenOfType`,
`removeChild`, etc.

Additionally, the ability to sort children has been added via the `ScrollComponent#sortChildren` function.

Added `ScrollComponent#setEmptyText(String)` to dynamically change the text displayed when the `ScrollComponent` has
no children.

### ImageProvider

ImageProvider has been refactored, allowing for SVGComponents to now be valid image providers. This also means
that when a `UIImage` fails to load, an SVG failure icon appears.

### UIShape

Added `addVertices` function to add multiple vertices at once.

### UIBlock

Added secondary constructor that accepts an initial `ColorConstraint`.

## Constraints

### Simple Constraint DSL

Previously, constructing custom constraints was a hassle, as they had a lot of boilerplate code overriding parent
variables and such. However, in v2.0.0 there is now an easy to use DSL to construct one-time constraints:

```kotlin
width = basicWidthConstraint { component ->
    component.getHeight() + 10f
}
```

### PixelConstraint

Pixel constraint now allows changing of the pixel value dynamically with `PixelConstraint#setValue(Float)`.

### MousePositionConstraint

Added the [MousePositionConstraint](../src/main/kotlin/club/sk1er/elementa/constraints/MousePositionConstraint.kt) which
will evaluate to the current mouse X/Y position.

### ChildBasedRangeConstraint

Added the [ChildBasedRangeConstraint](../src/main/kotlin/club/sk1er/elementa/constraints/ChildBasedConstraints.kt) which
evaluates the width/height difference between the component's two furthest children. This constraint is tricky to use
as this component and all of its children need to have their X/Y position not reliant on any parental width/height.

### AnimatingConstraints

There is now a way to delay an animation from completing for a certain amount of time after it has actually completed
via the `AnimationConstraints#setExtraDelay(Float)` function.

Renamed the old function `AnimationConstraints#complete` to `AnimationConstraints#isComplete` to clarify any confusion.

## Effects

### Outline Effect

Added option to draw the outline after all children have drawn via the `drawAfterChildren: Boolean` constructor
parameter. 