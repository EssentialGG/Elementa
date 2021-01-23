# Elementa Style Guide

This document specifies the best way to write clean and concise Elementa code. It is highly opinionated,
and reflects the style preferences of the project authors. It is of course not required to follow this
guide when writing Elementa code, but it is preferred. 

### Language

Elementa should be written exclusively in Kotlin, if possible. There are many features and extension
functions which are targeted specifically at Kotlin. Consider the following snippet of code:

```kotlin
val block by UIBlock(Color.RED).constrain {
    x = 10.pixels()
    y = 50.percent() 
} effect ScissorEffect() childOf parent
```

This could be a field on a Kotlin component. It uses lambda syntax for the `constraint` block, extension
functions for `pixels`, `percent`, `effect`, and `childOf`, and delegation using `by` to set the component's
name to "block" for use in the `Inspector`. Now consider the same field in a Java class:

```java
private UIBlock block = (UIBlock) new UIBlock(Color.RED)
        .setX(new PixelConstraint(10))
        .setY(new RelativeConstraint(0.5f))
        .enableEffect(new ScissorEffect());
```

This code is much harder to read. It requires a cast because `enableEffect` returns `UIComponent` rather than
`UIBlock` (as opposed to the `effect` extension function, which returns `T`, the type of the first argument).
It also doesn't set itself as a child of `parent` -- you would have to call `parent.addChild(block)` 
somewhere, probably in the constructor. And finally, its name will not be set to `block`; if you needed
that, you would have to set it manually. For these reasons, Kotlin is preferred.

### Class Fields and Organization

Often times, complex components can be hard to read if it is not organized properly. Here, we'll discuss some tips for
organization which will make your components easier to read for others.

#### Declaration of components as fields

Components should be initialized and constrained as fields, outside of any `init {}` or constructor declarations. If a
component has _simple_ (i.e. only a few lines) actions or listeners (i.e. `onMouseClick`, `onKeyType`, etc), they can be
registered in the field initializer. The reason for keeping declarations simple is that it is easier to read a component
if one can first get an overview of the different children it contains, and then at a later time read into the actual
logic of the component. For this reason, if a component has sufficiently complex actions or listeners, they should be 
registered in an `init` block. 

#### Field formatting

Component fields should be separated by one new line. Components often take up at least three lines due to `.constrain`
blocks, so making the separation between components clear is vital for readability. 

#### Separation of components

When possible, very large and complex components should be split up into smaller components. This is applicable to
any software, but deserves to be re-iterated. Reading three separate 200-line components is much easier than reading
one large 600-line component.

### DSL functions

Constraint DSL functions should be used exclusively over their class counterparts. This applies to the following 
DSL functions:

- `ABC.pixels()` should be used instead of `PixelConstraint(XYZ)`
- `ABC.percent()` should be used instead of `RelativeConstraint(XYZ)`
- `ABC.percentOfWindow()` should be used instead of `RelativeWindowConstraint(XYZ)`
- `Color(...).toConstraint()` should be used instead of `ConstantColorConstraint(Color(...))`

Additionally, the following infix functions should always be used as infix functions except in certain situations:

- `childOf`
- `effect`

It is appropriate to not use these functions in a statement when the target of the function side is `this`, i.e., it is 
fine to use the following as standalone statements:

- `addChild(aChild)` instead of `aChild childOf this`
- `enableEffect(anEffect)` instead of `this effect anEffect`

### Kotlin Style

Of course, this guide covers only Elementa-specific style. To write the cleanest code, you should read and follow the
official [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html).
