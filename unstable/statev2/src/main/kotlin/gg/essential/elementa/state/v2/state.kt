package gg.essential.elementa.state.v2


import gg.essential.elementa.state.v2.ReferenceHolder
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * The base for all Elementa State objects.
 *
 * State objects are essentially just a wrapper around a value. However, the ability to be deeply
 * integrated into an Elementa component allows some nice functionality.
 *
 * The primary advantage of using state is that a single state object can be shared between multiple
 * components or constraints. This allows one value update to be seen by multiple components or
 * constraints. For example, if a component has many text children, and they all share the same
 * color state variable, then whenever the value of the state object is updated, all of the text
 * components will instantly change color.
 *
 * Another advantage arises when using Kotlin, as States can be delegated to. For more information,
 * see delegation.kt.
 */
interface State<out T> {
  /** Get the value of this State object */
  fun get(): T

  /**
   * Register a listener which will be called whenever the value of this State object changes
   *
   * The listener registration is weak by default. This means that no strong reference to the
   * listener is kept in this State object and your listener may be garbage collected if no other
   * strong references to it exist. Once a listener is garbage collected, it will (obviously) no
   * longer receive updates.
   *
   * Keeping a strong reference to your own listener is easy to forget, so this method requires you
   * to explicitly pass in an object which will maintain a strong reference to your listener for
   * you. With that, your listener will stay active **at least** as long as the given [owner] is
   * alive (unless the returned callback in invoked).
   *
   * In general, the lifetime of your listener should match the lifetime of the passed [owner],
   * usually the thing (e.g. [UIComponent]) the listener is modifying. If the owner far outlives
   * your listener, you may be leaking memory because the owner will keep all those listeners and
   * anything they reference alive far beyond the point where they are needed. If your listener
   * outlives the owner, then it may become inactive sooner than you expected and whatever it is
   * updating might no longer update properly.
   *
   * If you wish to manually keep your listener alive, pass [ReferenceHolder.Weak] as the owner.
   *
   * @return A callback which, when invoked, removes this listener
   */
  fun onSetValue(owner: ReferenceHolder, listener: (T) -> Unit): () -> Unit
}

/* ReferenceHolder is defined in Elementa as:
/**
 * Holds strong references to listeners to prevent them from being garbage collected.
 * @see State.onSetValue
 */
interface ReferenceHolder {
  fun holdOnto(listener: Any): () -> Unit

  object Weak : ReferenceHolder {
    override fun holdOnto(listener: Any): () -> Unit = {}
  }
}
 */

/** A [State] with a value that can be changed via [set] */
@JvmDefaultWithoutCompatibility
interface MutableState<T> : State<T> {
  /**
   * Update the value of this State object.
   *
   * After the value has been updated, all listeners of this State object are notified.
   *
   * The provided lambda must be a pure function which will return the new value for this State give
   * the current value.
   *
   * Note that while most basic State implementations will call the lambda and notify listeners
   * immediately, there is no general requirement for them to do so, and specialized State
   * implementations may delay either or both to e.g. batch multiple updates together.
   */
  fun set(mapper: (T) -> T)

  /**
   * Update the value of this State object.
   *
   * After the value has been updated, all listeners of this State object are notified.
   *
   * Note that while most basic State implementations will update and notify listeners immediately,
   * there is no general requirement for them to do so, and specialized State implementations may
   * delay either or both to e.g. batch multiple updates together.
   *
   * @see [set]
   */
  fun set(value: T) = set { value }
}

/** A [State] delegating to a configurable target [State] */
interface DelegatingState<T> : State<T> {
  fun rebind(newState: State<T>)
}

/** A [MutableState] delegating to a configurable target [MutableState] */
@JvmDefaultWithoutCompatibility
interface DelegatingMutableState<T> : MutableState<T> {
  fun rebind(newState: MutableState<T>)
}

/** Creates a new [State] with the given value. */
fun <T> stateOf(value: T): State<T> = ImmutableState(value)

/** Creates a new [MutableState] with the given initial value. */
fun <T> mutableStateOf(value: T): MutableState<T> = BasicState(value)

/** Creates a new [DelegatingState] with the given target [State]. */
fun <T> stateDelegatingTo(state: State<T>): DelegatingState<T> = DelegatingStateImpl(state)

/** Creates a new [DelegatingMutableState] with the given target [MutableState]. */
fun <T> mutableStateDelegatingTo(state: MutableState<T>): DelegatingMutableState<T> =
    DelegatingMutableStateImpl(state)

/** Creates a [State] which derives its value in a user-defined way from one or more other states */
fun <T> derivedState(
    initialValue: T,
    builder: (owner: ReferenceHolder, derivedState: MutableState<T>) -> Unit,
): State<T> {
  return ReferenceHoldingBasicState(initialValue).apply { builder(this, this) }
}

/** A simple, immutable implementation of [State] */
private class ImmutableState<T>(private val value: T) : State<T> {
  override fun get(): T = value
  override fun onSetValue(owner: ReferenceHolder, listener: (T) -> Unit): () -> Unit = {}
}

/** A simple implementation of [MutableState], containing only a backing field */
private open class BasicState<T>(private var valueBacker: T) : MutableState<T> {
  private val referenceQueue = ReferenceQueue<Any>()
  private val listeners = mutableListOf<ListenerEntry<T>>()

  /**
   * Contains the size of the [listeners] list which we currently iterate over.
   * We must not directly modify these entries as that may mess up the iteration, anything after those entries is fair
   * game though.
   * Additions always happen at the end of the list, so those are trivial.
   * For removals we instead set the [ListenerEntry.removed] flag and let the iteration code clean up the entry when
   * it passes over it.
   * We can't solely rely on that for all cleanup because we only iterate the listener list when the value of the state
   * changes, so if it doesn't, we need to clean up entries immediately.
   */
  private var liveSize = 0

  override fun get() = valueBacker

  override fun onSetValue(owner: ReferenceHolder, listener: (T) -> Unit): () -> Unit {
    cleanupStaleListeners()
    val ownerCallback = WeakReference(owner.holdOnto(Pair(this, listener)))
    return ListenerEntry(this, listener, ownerCallback).also { listeners.add(it) }
  }

  override fun set(mapper: (T) -> T) {
    val oldValue = valueBacker
    val newValue = mapper(oldValue)
    if (oldValue == newValue) {
      return
    }

    valueBacker = newValue

    // Iterate over listeners while allowing for concurrent add to the end of the list (newly added entries will not get
    // called) and concurrent remove from anywhere in the list (via `removed` flag in each entry, or directly for newly
    // added listeners). See [liveSize] docs.
    liveSize = listeners.size
    var i = 0
    while (i < liveSize) {
      val entry = listeners[i]
      if (entry.removed) {
        listeners.removeAt(i)
        liveSize--
      } else {
        entry.get()?.invoke(newValue)
        i++
      }
    }
    liveSize = 0
  }

  private fun cleanupStaleListeners() {
    while (true) {
      val reference = referenceQueue.poll() ?: break
      (reference as ListenerEntry<*>).invoke()
    }
  }

  private class ListenerEntry<T>(
      private val state: BasicState<T>,
      listenerCallback: (T) -> Unit,
      private val ownerCallback: WeakReference<() -> Unit>,
  ) : WeakReference<(T) -> Unit>(listenerCallback, state.referenceQueue), () -> Unit {
    var removed = false

    override fun invoke() {
      // If we do not currently iterate over the listener list, we can directly remove this entry from the list,
      // otherwise we merely mark it as deleted and let the iteration code take care of it.
      val index = state.listeners.indexOf(this@ListenerEntry)
      if (index >= state.liveSize) {
        state.listeners.removeAt(index)
      } else {
        removed = true
      }

      ownerCallback.get()?.invoke()
    }
  }
}

/** Base class for implementations of Delegating(Mutable)State classes. */
private open class DelegatingStateBase<T, S : State<T>>(protected var delegate: S) : State<T> {
  private val referenceQueue = ReferenceQueue<Any>()
  private var listeners = mutableListOf<ListenerEntry<T>>()

  override fun get(): T = delegate.get()

  override fun onSetValue(owner: ReferenceHolder, listener: (T) -> Unit): () -> Unit {
    cleanupStaleListeners()
    val ownerCallback = WeakReference(owner.holdOnto(Pair(this, listener)))
    val removeCallback = delegate.onSetValue(ReferenceHolder.Weak, listener)
    return ListenerEntry(this, listener, removeCallback, ownerCallback).also { listeners.add(it) }
  }


  fun rebind(newState: S) {
    val oldState = delegate
    if (oldState == newState) {
      return
    }

    delegate = newState

    listeners =
        listeners.mapNotNullTo(mutableListOf()) { entry ->
          entry.removeCallback()
          val listenerCallback = entry.get() ?: return@mapNotNullTo null
          val removeCallback = newState.onSetValue(ReferenceHolder.Weak, listenerCallback)
          ListenerEntry(this, listenerCallback, removeCallback, entry.ownerCallback)
        }

    val oldValue = oldState.get()
    val newValue = newState.get()
    if (oldValue != newValue) {
      listeners.forEach { it.get()?.invoke(newValue) }
    }
  }

  private fun cleanupStaleListeners() {
    while (true) {
      val reference = referenceQueue.poll() ?: break
      (reference as ListenerEntry<*>).invoke()
    }
  }

  private class ListenerEntry<T>(
      private val state: DelegatingStateBase<T, *>,
      listenerCallback: (T) -> Unit,
      val removeCallback: () -> Unit,
      val ownerCallback: WeakReference<() -> Unit>,
  ) : WeakReference<(T) -> Unit>(listenerCallback, state.referenceQueue), () -> Unit {
    override fun invoke() {
      state.listeners.remove(this@ListenerEntry)
      removeCallback()
      ownerCallback.get()?.invoke()
    }
  }
}

/** Default implementation of [DelegatingState] */
private class DelegatingStateImpl<T>(delegate: State<T>) :
    DelegatingStateBase<T, State<T>>(delegate), DelegatingState<T>

/** Default implementation of [DelegatingMutableState] */
private class DelegatingMutableStateImpl<T>(delegate: MutableState<T>) :
    DelegatingStateBase<T, MutableState<T>>(delegate), DelegatingMutableState<T> {
  override fun set(mapper: (T) -> T) {
    delegate.set(mapper)
  }
}

/** A [BasicState] which additionally implements [ReferenceHolder] */
private class ReferenceHoldingBasicState<T>(value: T) : BasicState<T>(value), ReferenceHolder {
  private val heldReferences = mutableListOf<Any>()

  override fun holdOnto(listener: Any): () -> Unit {
    heldReferences.add(listener)
    return { heldReferences.remove(listener) }
  }
}

/** A simple implementation of [ReferenceHolder] */
class ReferenceHolderImpl : ReferenceHolder {
  private val heldReferences = mutableListOf<Any>()

  override fun holdOnto(listener: Any): () -> Unit {
    heldReferences.add(listener)
    return { heldReferences.remove(listener) }
  }
}
