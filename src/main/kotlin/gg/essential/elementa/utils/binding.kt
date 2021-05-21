package gg.essential.elementa.utils

fun <A, R> ((A) -> R).bind(arg1: A): () -> R = { this(arg1) }

fun <A, B, R> ((A, B) -> R).bind(arg1: A): (B) -> R = { arg2 -> this(arg1, arg2) }
fun <A, B, R> ((A, B) -> R).bind(arg1: A, arg2: B): () -> R = { this(arg1, arg2) }

fun <A, B, C, R> ((A, B, C) -> R).bind(arg1: A): (B, C) -> R = { arg2, arg3 -> this(arg1, arg2, arg3) }
fun <A, B, C, R> ((A, B, C) -> R).bind(arg1: A, arg2: B): (C) -> R = { arg3 -> this(arg1, arg2, arg3) }
fun <A, B, C, R> ((A, B, C) -> R).bind(arg1: A, arg2: B, arg3: C): () -> R = { this(arg1, arg2, arg3) }

fun <A, B, C, D, R> ((A, B, C, D) -> R).bind(arg1: A): (B, C, D) -> R = { arg2, arg3, arg4 -> this(arg1, arg2, arg3, arg4) }
fun <A, B, C, D, R> ((A, B, C, D) -> R).bind(arg1: A, arg2: B): (C, D) -> R = { arg3, arg4 -> this(arg1, arg2, arg3, arg4) }
fun <A, B, C, D, R> ((A, B, C, D) -> R).bind(arg1: A, arg2: B, arg3: C): (D) -> R = { arg4 -> this(arg1, arg2, arg3, arg4) }
fun <A, B, C, D, R> ((A, B, C, D) -> R).bind(arg1: A, arg2: B, arg3: C, arg4: D): () -> R = { this(arg1, arg2, arg3, arg4) }

fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bind(arg1: A): (B, C, D, E) -> R = { arg2, arg3, arg4, arg5 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bind(arg1: A, arg2: B): (C, D, E) -> R = { arg3, arg4, arg5 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bind(arg1: A, arg2: B, arg3: C): (D, E) -> R = { arg4, arg5 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bind(arg1: A, arg2: B, arg3: C, arg4: D): (E) -> R = { arg5 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bind(arg1: A, arg2: B, arg3: C, arg4: D, arg5: E): () -> R = { this(arg1, arg2, arg3, arg4, arg5) }

fun <A, R> ((A) -> R).bindLast(arg1: A): () -> R = this.bind(arg1)

fun <A, B, R> ((A, B) -> R).bindLast(arg2: B): (A) -> R = { arg1 -> this(arg1, arg2) }
fun <A, B, R> ((A, B) -> R).bindLast(arg1: A, arg2: B): () -> R = this.bind(arg1, arg2)

fun <A, B, C, R> ((A, B, C) -> R).bindLast(arg3: C): (A, B) -> R = { arg1, arg2 -> this(arg1, arg2, arg3) }
fun <A, B, C, R> ((A, B, C) -> R).bindLast(arg2: B, arg3: C): (A) -> R = { arg1 -> this(arg1, arg2, arg3) }
fun <A, B, C, R> ((A, B, C) -> R).bindLast(arg1: A, arg2: B, arg3: C): () -> R = this.bind(arg1, arg2, arg3)

fun <A, B, C, D, R> ((A, B, C, D) -> R).bindLast(arg4: D): (A, B, C) -> R = { arg1, arg2, arg3 -> this(arg1, arg2, arg3, arg4) }
fun <A, B, C, D, R> ((A, B, C, D) -> R).bindLast(arg3: C, arg4: D): (A, B) -> R = { arg1, arg2 -> this(arg1, arg2, arg3, arg4) }
fun <A, B, C, D, R> ((A, B, C, D) -> R).bindLast(arg2: B, arg3: C, arg4: D): (A) -> R = { arg1 -> this(arg1, arg2, arg3, arg4) }
fun <A, B, C, D, R> ((A, B, C, D) -> R).bindLast(arg1: A, arg2: B, arg3: C, arg4: D): () -> R = this.bind(arg1, arg2, arg3, arg4)

fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bindLast(arg5: E): (A, B, C, D) -> R = { arg1, arg2, arg3, arg4 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bindLast(arg4: D, arg5: E): (A, B, C) -> R = { arg1, arg2, arg3 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bindLast(arg3: C, arg4: D, arg5: E): (A, B) -> R = { arg1, arg2 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bindLast(arg2: B, arg3: C, arg4: D, arg5: E): (A) -> R = { arg1 -> this(arg1, arg2, arg3, arg4, arg5) }
fun <A, B, C, D, E, R> ((A, B, C, D, E) -> R).bindLast(arg1: A, arg2: B, arg3: C, arg4: D, arg5: E): () -> R = this.bind(arg1, arg2, arg3, arg4, arg5)
