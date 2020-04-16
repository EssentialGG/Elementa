# Components

Elementa provides a wide array of default UIComponents that can combine
to create any number of awesome GUIs. Here they will be described in detail,
and have examples given for how to use them effectively.

- [UIContainer](#UIContainer)


### UIContainer

The UIContainer component is the simplest of all components as it does
not do any rendering whatsoever. It simply serves to be a "holder" or 
parent to a group of children components. It can be considered analogous
to a `<div>` element in the HTML world.
 
 
For example, if I wished to right-align a series of components, it makes
a lot of sense to simply wrap said components in a UIContainer and right-align
the container.

```kotlin
val bar = UIBlock(Color.WHITE).constrain {
    width = 150.pixels()
    height = 50.pixels()
} childOf window

val container = UIContainer().constrain {
    x = 0.pixels(true)
    width = ChildBasedSizeConstraint() + 6.pixels()
    height = ChildBasedMaxSizeConstraint()
} childOf bar

repeat(3) {
    UIBlock(Color.RED).constrain {
        x = SiblingConstraint() + 2.pixels()
        width = 25.pixels()
        height = 25.pixels()
    } childOf container
}
```

The code above produces the following result:
![UIContainer Example](https://i.imgur.com/NvZIFU6.png)


### UIBlock

UIBlock is another basic, but frequently used component.