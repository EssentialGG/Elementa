package club.sk1er.elementa.state

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ColorConstraint
import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.MasterConstraint
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import java.awt.Color
import java.util.function.Consumer
import java.util.function.Function
import kotlin.reflect.KProperty

abstract class State<T> {
    protected val listeners = mutableListOf<(T) -> Unit>()

    abstract fun getValue(): T

    open fun setValue(value: T) {
        listeners.forEach { it(value) }
    }

    fun onSetValue(listener: (T) -> Unit): () -> Unit {
        listeners.add(listener)
        return { listeners.remove(listener) }
    }

    fun onSetValue(listener: Consumer<T>) = onSetValue { listener.accept(it) }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = getValue()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setValue(value)

    fun getOrDefault(defaultValue: T) = getValue() ?: defaultValue

    fun getOrElse(defaultProvider: () -> T) = getValue() ?: defaultProvider()

    fun <U> map(mapper: (T) -> U) = MappedState(this, mapper)

    fun <U> map(mapper: Function<T, U>): State<U> = map { mapper.apply(it) }

    fun <U> zip(otherState: State<U>): State<Pair<T, U>> = ZippedState(this, otherState)
}

open class BasicState<T>(private var value: T) : State<T>() {
    override fun getValue() = value

    override fun setValue(value: T) {
        this.value = value
        super.setValue(value)
    }
}

open class MappedState<T, U>(initialState: State<T>, private val mapper: (T) -> U) : BasicState<U>(mapper(initialState.getValue())) {
    private var cachedValue = mapper(initialState.getValue())
    private var removeListener = initialState.onSetValue {
        cachedValue = mapper(it)
    }

    fun rebind(newState: State<T>) {
        removeListener()
        removeListener = newState.onSetValue {
            cachedValue = mapper(it)
        }
        setValue(mapper(newState.getValue()))
    }
}

class ZippedState<T, U>(firstState: State<T>, secondState: State<U>) : State<Pair<T, U>>() {
    private var firstCachedValue = firstState.getValue()
    private var secondCachedValue = secondState.getValue()

    init {
        firstState.onSetValue {
            firstCachedValue = it
        }
        secondState.onSetValue {
            secondCachedValue = it
        }
    }

    override fun getValue(): Pair<T, U> = firstCachedValue to secondCachedValue
}

open class ListState<T>(private var list: List<T>) : State<List<T>>() {
    override fun getValue() = list

    override fun setValue(value: List<T>) {
        this.list = value
        super.setValue(value)
    }

    operator fun get(index: Int) = list[index]
}

class MutableListState<T>(private var list: MutableList<T>) : State<MutableList<T>>() {
    override fun getValue() = list

    override fun setValue(value: MutableList<T>) {
        this.list = value
        super.setValue(value)
    }

    operator fun get(index: Int) = list[index]

    operator fun set(index: Int, value: T) {
        list[index] = value
        listeners.forEach { it(list) }
    }
}

abstract class ConstraintState : State<Float>(), MasterConstraint {
    override var recalculate = false
    override var constrainTo: UIComponent? = null

    override fun getValue() = cachedValue

    override fun setValue(value: Float) {
        this.cachedValue = value
        super.setValue(value)
    }

    override fun getXPositionImpl(component: UIComponent) = cachedValue

    override fun getYPositionImpl(component: UIComponent) = cachedValue

    override fun getRadiusImpl(component: UIComponent) = cachedValue

    override fun getWidthImpl(component: UIComponent) = cachedValue

    override fun getHeightImpl(component: UIComponent) = cachedValue

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}

class MappedConstraintState(initialState: State<Float>) : ConstraintState() {
    override var cachedValue: Float = initialState.getValue()

    init {
        initialState.onSetValue {
            cachedValue = it
        }
    }
}

fun State<Float>.toConstraint() = MappedConstraintState(this)

class ColorConstraintState(override var cachedValue: Color) : State<Color>(), ColorConstraint {
    override var recalculate = false
    override var constrainTo: UIComponent? = null

    override fun getValue() = cachedValue

    override fun setValue(value: Color) {
        this.cachedValue = value
        super.setValue(value)
    }

    override fun getColorImpl(component: UIComponent) = cachedValue

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) { }
}
