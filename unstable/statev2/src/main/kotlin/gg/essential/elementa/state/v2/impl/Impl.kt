package gg.essential.elementa.state.v2.impl

import gg.essential.elementa.state.v2.ReferenceHolder
import gg.essential.elementa.state.v2.DelegatingMutableState
import gg.essential.elementa.state.v2.DelegatingState
import gg.essential.elementa.state.v2.MutableState
import gg.essential.elementa.state.v2.State

internal interface Impl {
    fun <T> mutableState(value: T): MutableState<T>

    fun <T> stateDelegatingTo(state: State<T>): DelegatingState<T>

    fun <T> mutableStateDelegatingTo(state: MutableState<T>): DelegatingMutableState<T>

    fun <T> derivedState(
        initialValue: T,
        builder: (owner: ReferenceHolder, derivedState: MutableState<T>) -> Unit,
    ): State<T>
}
