# v1.7.1 --> v2.0.0

Elementa v2.0.0 is a huge, breaking update to the Elementa library. Most things have changed at least a little, so this
document should be referenced to see how to migrate your project to this new version.

## Events

A lot of the event architecture got changed in v2. Previously, all components received keyboard events, mouse events
weren't cancellable, etc. All of this has been rectified in the new version. Now, most events work similarly to how
they do in your browser, with the DOM. The events that have changed will be covered in detail below.

### Click Events

Click events now follow the practice of "Event Bubbling". This means that when you click your mouse in an Elementa
Window, it will use hit testing to determine the deepest element you clicked on. From there, the click event fires on
that element, then its parent, and so on. Each of these components can have event listeners, and in your event listener
you can cancel the event from continuing upwards on the parent chain with the `UIEvent#stopPropogation` function. To
learn about how this works in the DOM, read [here](https://javascript.info/bubbling-and-capturing#bubbling). While this
example does use HTML, the same effect applies to your component hierarchy, and should give you a good intuition on how
it works.

With this change, you can also have multiple click event listeners on a single component. The first click listeners
registered have priority over any listeners registered later. You can cancel the event from firing later sibling
listeners with the `UIEvent#stopPropogationImmediately` function.

An example of the new click listeners from the [ExampleGui](../src/main/java/com/example/examplemod/ExampleGui.kt) class:

```kotlin
component.onMouseClick { event ->
    // Now, we need to modify our state to say that we are actively dragging
    // this note around.
    isDragging = true

    // Here we are storing the absolute position of our mouse.
    // The UIClickEvent provides us the ability to directly access those properties.
    // It also offers [relativeX] and [relativeY] properties if necessary.
    dragOffset = event.absoluteX to event.absoluteY
}
```

### Keyboard Events

In v2.0.0, keyboard events no longer fire on every component in a GUI, rather, they are only fired on the component
that currently has "focus". Read more about focus [here](#focus). Keyboard events also do not bubble, meaning that
only the component with focus will ever receive keyboard events, not even the focused component's parents.

## Window

A couple things have changed with the `Window` class.

For one, you no longer need to call `mouseDrag` on your Window instance, mouse dragging is handled internally
by Elementa now to provide higher quality drag animations.

### Focus

Elementa now has the concept of "Window focus". When first constructing a `Window`, focus defaults to being on the
`Window` itself. This means that if you press `<escape>` immediately upon opening your GUI, the `window` will receive
that event. If you are using the `WindowScreen` class, this will then close your GUI.

However, focus doesn't always remain on the window. Components can call `UIComponent#grabWindowFocus` to request focus.
For example, this would be useful to put in the `onMouseClick` listener of a text input component, like so
(code from the [ExampleGui](../src/main/java/com/example/examplemod/ExampleGui.kt) class):

```kotlin
textArea.onMouseClick {
    // When we click inside of this text area, we want to activate it. To do so, we need to make sure
    // that this text input has the Window's focus. This means that the Window will route keyboard
    // events to our component while it is focused. Later, when we click away from this text input area,
    // we will automatically lose focus.
    // Both the [UITextInput] and [UIMultilineTextInput] classes automatically activate/deactivate
    // themselves when they receive/lose focus respectively, so there is no need to manually add
    // [onFocus] or [onFocusLost] listeners, unless you wish to override the default behavior.
    grabWindowFocus()
}
```

Now, since the TextInput has the Window's focus, all key events will be passed to it, allowing for you to type into the
box!

You can manually release a component from having the Window's focus at any time with the
`UIComponent#releaseWindowFocus` function, although it would rarely be necessary. That is to say, when you click on
your GUI while a component has focus, and your click does not land on any other component that requests the Window's
focus, focus will be automatically released from the previous component. Additionally, the TextInput classes will
automatically release window focus when pressing `<escape>`. The benefits of this focus system are immense: as an easy
example, pressing `<escape>` in a text input box no longer instantly closes the gui, rather, it will first deactivate
the text input.

## Components

### UITextInput

The old UITextInput class has been replaced with two new classes: `UITextInput` and `UIMultilineTextInput`. The former
is for single-line input boxes, and the latter is for multi-line input boxes. Read more about these components in
general [here](whatsnew.md#textinput).

Changes in the usage of these components are as follows:
- Setting the input to be active manually is no longer necessary, simply just grab window focus on the input.
- `input.active = true` now becomes `input.setActive(true)`
- `input.text = "..."` now becomes `input.setText("...")`

### UIShape

The function `UIShape#addVertex` now returns the `this` instance of `UIShape`.

## Constraints

The `.max` and `.min` DSL functions have been renamed to `.coerceAtMost` and `.coerceAtLeast`, which nicely matches the
Kotlin stdlib naming. 

## Kotlin Extension Methods

`asConstraint` have been renamed to `toConstraint`
