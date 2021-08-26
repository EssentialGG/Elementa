package gg.essential.elementa.utils

internal val devPropSet = System.getProperty("elementa.dev")?.toBoolean() ?: false
private val debugPropSet = System.getProperty("elementa.debug")?.toBoolean() ?: false

var elementaDev: Boolean = devPropSet
    set(value) {
        if (devPropSet) {
            field = value
        }
    }

var elementaDebug: Boolean = debugPropSet
    set(value) {
        if (debugPropSet) {
            field = value
        }
    }
