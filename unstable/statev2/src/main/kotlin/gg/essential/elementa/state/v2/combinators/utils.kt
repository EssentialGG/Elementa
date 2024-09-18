package gg.essential.elementa.state.v2.combinators

import gg.essential.elementa.state.v2.MutableState

fun MutableState<Int>.reorder(vararg mapping: Int) =
    bimap({ mapping[it] }, { mapping.indexOf(it) })
