package gg.essential.elementa.state.v2

fun <T> State<State<T>>.flatten() = memo { this@flatten()() }
