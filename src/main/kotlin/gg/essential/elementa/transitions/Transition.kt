package gg.essential.elementa.transitions

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.animation.AnimatingConstraints
import gg.essential.elementa.dsl.animate

/**
 * A Transition is just a wrapper around a general animation
 * that can be applied to many components.
 *
 * Transitions are designed to be ran in sequence or parallel
 * with other transitions. To maximize the ability of a transition
 * to be used with other transitions, it should be relatively simple.
 *
 * Elementa comes with many simple transitions, which are all
 * able to be used with each other, as well as custom user
 * transitions.
 */
abstract class Transition {
    private val parallelTransitions = mutableListOf<Transition>()
    private var sequenceTransition: Transition? = null

    /**
     * Register a transition that will run _after_ this transition
     * finishes.
     *
     * Only one transition can be chained. If this transition already
     * has a chained transition, an IllegalStateException will be
     * thrown.
     */
    fun chain(transition: Transition) = apply {
        if (sequenceTransition != null)
            throw IllegalStateException("Cannot chain multiple transitions")

        sequenceTransition = transition
    }

    /**
     * Register a transition that will run _parallel_ to this
     * transition.
     *
     * The length of the parallel transition does not have to
     * match this transition; afterTransition will be ran
     * after whichever transition takes the longest.
     */
    fun with(transition: Transition) = apply {
        parallelTransitions.add(transition)
    }

    /**
     * Run the transition on a component.
     *
     * @param component The component which will be transitioned
     * @param callback A callback to be ran after this entire transition
     *                 has finished. This will be ran after all chained
     *                 transitions have completed
     */
    @JvmOverloads
    fun transition(component: UIComponent, callback: (() -> Unit)? = null) {
        beforeTransition(component)
        parallelTransitions.forEach {
            it.beforeTransition(component)
        }

        component.animate {
            doTransition(component, this)

            parallelTransitions.forEach { transition ->
                transition.doTransition(component, this)
            }

            onComplete {
                afterTransition(component)
                parallelTransitions.forEach {
                    it.afterTransition(component)
                }

                if (sequenceTransition == null) {
                    callback?.invoke()
                } else {
                    sequenceTransition!!.transition(component, callback)
                }
            }
        }
    }

    /**
     * Run the transition on a component.
     *
     * @param component The component which will be transitioned
     * @param callback A callback to be ran after this entire transition
     *                 has finished. This will be ran after all chained
     *                 transitions have completed
     */
    fun transition(component: UIComponent, callback: Runnable) {
        transition(component) { callback.run() }
    }

    /**
     * Ran before the actual animation acts on the component. This
     * is useful for setting up any required initial state.
     */
    protected open fun beforeTransition(component: UIComponent) {}

    /**
     * Responsible for actually animating the component.
     *
     * All parallel transitions receive the same AnimatingConstraints instance.
     * This means that if two parallel transitions call the same transition
     * function, only the later one will have an effect.
     */
    protected abstract fun doTransition(component: UIComponent, constraints: AnimatingConstraints)

    /**
     * Ran after the animation acts on the component. This is useful
     * for cleaning up any final state.
     */
    protected open fun afterTransition(component: UIComponent) {}
}
