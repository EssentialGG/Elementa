package gg.essential.elementa.state.v2

/**
 * Creates a state that derives its value using the given [block]. The value of any state may be accessed within this
 * block via [StateByScope.invoke]. These accesses are tracked and the block is automatically re-evaluated whenever any
 * one of them changes.
 *
 * Note that while this is generally easier to use than [derivedState], it also comes with greater overhead.
 */
fun <T> stateBy(block: StateByScope.() -> T): State<T> {
    val subscribed = mutableMapOf<State<*>, () -> Unit>()
    val observed = mutableSetOf<State<*>>()
    val scope = object : StateByScope {
        override fun <T> State<T>.invoke(): T {
            observed.add(this)
            return get()
        }
    }

    return derivedState(initialValue = block(scope)) { owner, derivedState ->
        fun updateSubscriptions() {
            for (state in observed) {
                if (state in subscribed) continue

                subscribed[state] = state.onSetValue(owner) {
                    val newValue = block(scope)
                    updateSubscriptions()
                    derivedState.set(newValue)
                }
            }

            subscribed.entries.removeAll { (state, unregister) ->
                if (state !in observed) {
                    unregister()
                    true
                } else {
                    false
                }
            }

            observed.clear()
        }
        updateSubscriptions()
    }
}

interface StateByScope {
    operator fun <T> State<T>.invoke(): T
}
