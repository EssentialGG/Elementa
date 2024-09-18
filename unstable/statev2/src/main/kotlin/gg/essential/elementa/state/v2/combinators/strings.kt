package gg.essential.elementa.state.v2.combinators

import gg.essential.elementa.state.v2.State

fun State<String>.contains(other: State<String>, ignoreCase: Boolean = false) =
    zip(other) { a, b -> a.contains(b, ignoreCase) }

fun State<String>.isEmpty() = map { it.isEmpty() }

fun State<String>.isNotEmpty() = map { it.isNotEmpty() }
