package gg.essential.elementa.state.v2

import gg.essential.elementa.state.v2.ReferenceHolder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** Waits until this [State] has a value which [equals] the given [value]. */
suspend fun <T> State<T>.awaitValue(value: T): T = await { it == value }

/** Waits until this [State] has a non-null value. */
suspend fun <T> State<T?>.awaitNotNull(): T = await { it != null }!!

/** Waits until this [State] has a value for which [accept] returns `true` and returns that value. */
suspend fun <T> State<T>.await(accept: (T) -> Boolean): T {
    // Fast-path
    get().let { if (accept(it)) return it }

    // Slow path
    return suspendCancellableCoroutine { continuation ->
        // We need to have the continuation carry a reference to the unregister function (we do this via
        // invokeOnCancellation at the end of this block), so the effect is kept alive at least as long as the coroutine
        // has any interest in it.
        // The same reference also serves as a cancellation indicator: If it's been set to `null`, the continuation
        // was cancelled and we can unregister the effect the next time it's called.
        var referenceFromContinuation: Any? = null

        var unregister: (() -> Unit)? = null
        unregister = onChange(ReferenceHolder.Weak) { value ->
            if (referenceFromContinuation == null) {
                unregister?.invoke()
                unregister = null
                return@onChange
            }
            if (accept(value)) {
                unregister?.invoke()
                unregister = null
                continuation.resume(value)
            }
        }

        referenceFromContinuation = unregister
        continuation.invokeOnCancellation {
            // Note: we cannot call `unregister` here because `invokeOnCancellation` makes no guarantee about which
            // thread we run on, and `unregister` isn't thread safe.
            // So we'll instead merely drop our reference to the unregister function and leave it to State's weakness
            // properties (or the next onChange invocation) to clean up the registration.
            referenceFromContinuation = null
        }
    }
}

fun <T> State<T>.currentAndFutureValues(): StateIterable<T> = object : StateIterable<T> {
    override fun iterator(): StateIterator<T> {
        return object : StateIterator<T> {
            private var initial = true
            private var next: T = getUntracked()

            override suspend fun hasNext(): Boolean {
                if (initial) {
                    return true
                }
                next = await { it != next }
                return true
            }

            override fun next(): T {
                initial = false
                return next
            }
        }
    }
}

fun <T> State<T>.futureValues(excludingInitial: T = getUntracked()): StateIterable<T> = object : StateIterable<T> {
    override fun iterator(): StateIterator<T> {
        return object : StateIterator<T> {
            private var next: T = excludingInitial

            override suspend fun hasNext(): Boolean {
                next = await { it != next }
                return true
            }

            override fun next(): T {
                return next
            }
        }
    }
}

interface StateIterable<T> {
    operator fun iterator(): StateIterator<T>
}

interface StateIterator<T> {
    suspend operator fun hasNext(): Boolean
    operator fun next(): T
}
