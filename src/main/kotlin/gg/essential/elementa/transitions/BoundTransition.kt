package gg.essential.elementa.transitions

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.animation.AnimatingConstraints

/**
 * A normal transition which is bound to a single component. This
 * is useful if a transition needs to store some persistent state
 * about the component it is acting on.
 *
 * This class is very similar to Effect in that it has a boundComponent
 * field which stores the component this transition is bound to.
 * This class can only be used with a single component. If it is used on
 * multiple components, it will throw an IllegalStateException.
 */
abstract class BoundTransition : Transition() {
    protected lateinit var boundComponent: UIComponent

    final override fun beforeTransition(component: UIComponent) {
        if (::boundComponent.isInitialized)
            throw IllegalStateException("BoundTransition cannot be used with multiple components")
        boundComponent = component
        beforeTransition()
    }

    final override fun doTransition(component: UIComponent, constraints: AnimatingConstraints) {
        if (component != boundComponent)
            throw IllegalStateException("Expected $component to be the same as boundComponent $boundComponent")
        doTransition(constraints)
    }

    final override fun afterTransition(component: UIComponent) {
        if (component != boundComponent)
            throw IllegalStateException("Expected $component to be the same as boundComponent $boundComponent")
        afterTransition()
    }

    open fun beforeTransition() {}

    abstract fun doTransition(constraints: AnimatingConstraints)

    open fun afterTransition() {}
}
