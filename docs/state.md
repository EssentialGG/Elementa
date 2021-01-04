# State

## What is `State`?

Elementa 2.0.0 comes with a state management API, which has the `State` class at its core.
Simply put, a `State` object is just a wrapper around a value. The real power of this class
comes when it is integrated into Elementa components. Let's take a look at an example, using
`UIText` with `State`:

```kotlin
val textState = BasicState("foo")
val colorState = BasicState(Color.RED)

val textComponent = UIText().bindText(textState).constrain {
    color = colorState.asConstraint()
}
```

This creates a `UIText` component which gets both its text and color constraint from two custom
state objects. Here is an example of how these states can be modified:

```kotlin
textState.set("bar")
colorState.set(Color.BLUE)
``` 


There are two concepts to understand here. First of all, `UIText` internally 
uses a state variable to store the string that it needs to display. This is different from 
Elementa versions < 2.0.0, where it was just a simple string property. When we call `.bindText`,
we set the `UIText`'s text state to the state we pass in. That way, when we change `textState`,
it is reflected in our GUI. 

Second, the reason that `colorState` can be used as a constraint is due to the 
`State<Color>.asConstraint()` extension method. There is a similar extension method for 
`State<Float>.pixels()`, as well as `State<Float>.percent()`. This is possible because
`ConstantColorConstraint`, `PixelConstraint`, and `RelativeConstraint` all use `State` objects
internally, similar to `UIText`.

## Shared State

The primary advantage of using `State` objects is their ability to be shared. The same `State`
object can be passed to multiple different components or constraints, and any changes to that
`State` object will be reflected wherever it is used. Let's look at an example:

```kotlin
val myTextContainer = UIContainer().constrain {
    // ...
}

val textColor = BasicState(Color.WHITE)
val baseNumber = BasicState(0)

for (i in 0 until 10) {
    UIText().bindText(baseNumber.map { (it + i).toString() }).constrain {
        // ...
        color = textColor.asConstraint()
    } childOf myTextContainer
}
```

In this very contrived example, we have two instances of shared state. If we change the value of
`textColor` using its `set` method, then the color of all 10 of our `UIText` components will 
update. This example also demonstrates a very useful state function: `State.map`. It accepts a 
mapping function and returns a new state. This mapped state will update any time its original 
state updates. So in the above example, if we were to call `baseNumber.set(10)`, then instead 
of our text components displaying the numbers 0 through 9, they would display 10 through 19.

## Delegation

Elementa has a couple top-level functions which allow state to be delegated to, which allows one
to use normal field access and assignment instead of `State.get()`/`State.set()`. There are three
top-level functions, each corresponding to the three types of State objects.

#### `state()`

The top-level `state()` function allows delegation to a `BasicState` object.

```kotlin
object Test {
    private var foo by state(1)
    
    fun main() {
        println(foo) // prints 1
        foo = 10
        println(foo) // prints 10
    }
}
```

#### `map()`

The top-level `map()` function allows mapped delegation to another `State` object.

```kotlin
object Test {
    private var foo by state(1)
    private val bar by map(::foo) { it * 2 }
    
    // Will not compile -- the first argument has to be a delegated property!
    // private val baz by map(state(1)) { it * 2 }
    
    fun main() {
        println(bar) // prints 2
        foo = 10
        println(bar) // prints 20
    }
}
```

#### `zip()`

The top-level `zip()` function allows zipped delegation to two other `State` objects.

```kotlin
object Test {
    private var foo by state(1)
    private var bar by state(2)
    private val baz by zip(::foo, ::bar)

    // Will not compile -- the arguments have to be delegated properties!
    // private val qux by zip(state(1), state(2))
    
    fun main() {
        println(baz) // prints (1, 2)
        foo = 10
        println(baz) // prints (10, 2)
    }
}
```

#### Delegation limitations

Unfortunately, due to restrictions imposed by Kotlin, delegated state properties cannot be used
in local variables. This means that any use of `by` with the above delegation functions must be
at a class- or object-level.
