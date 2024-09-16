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
        var unregister: (() -> Unit)? = null
        unregister = onChange(ReferenceHolder.Weak) { value ->
            if (accept(value)) {
                unregister?.invoke()
                unregister = null
                continuation.resume(value)
            }
        }
        continuation.invokeOnCancellation {
            // Note: we cannot call `unregister` here because `invokeOnCancellation` makes no guarantee about which
            // thread we run on, and `unregister` isn't thread safe.
            // So we'll instead merely drop our reference to the unregister function and leave it to State's weakness
            // properties to clean up the registration.
            // This does mean our callback will continue to be invoked, but `CancellableCoroutine` is fine with that
            // because cancellation may race with `resume` in pretty much any code.
            unregister = null
        }
    }
}
