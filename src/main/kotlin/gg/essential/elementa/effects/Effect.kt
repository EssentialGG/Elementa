package gg.essential.elementa.effects

import gg.essential.elementa.UIComponent

/**
 * Basic interface all effects need to follow.
 *
 * This is where you can affect any drawing done.
 */
abstract class Effect {
    protected lateinit var boundComponent: UIComponent

    fun bindComponent(component: UIComponent) {
        if (::boundComponent.isInitialized && boundComponent != component)
            throw IllegalStateException("Attempt to bind component of a ${this::class.simpleName} " +
                "which already has a bound component")
        boundComponent = component
    }
    /**
     * Called once inside of the component's afterInitialization function
     */
    open fun setup() {}

    /**
     * Called in the component's animationFrame function
     */
    open fun animationFrame() {}

    /**
     * Set up all drawing, turn on shaders, etc.
     */
    open fun beforeDraw() {}

    /**
     * Called after this component draws but before it's children are drawn.
     */
    open fun beforeChildrenDraw() {}

    /**
     * Clean up all of this feature's GL states, etc.
     */
    open fun afterDraw() {}
}
