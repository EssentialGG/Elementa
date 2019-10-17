package club.sk1er.elementa.effects

import club.sk1er.elementa.UIComponent

/**
 * Basic interface all effects need to follow.
 *
 * This is where you can affect any drawing done.
 */
interface Effect {
    /**
     * Set up all drawing, turn on shaders, etc.
     */
    fun beforeDraw(component: UIComponent)

    /**
     * Clean up all of this feature's GL states, etc.
     */
    fun afterDraw(component: UIComponent)
}