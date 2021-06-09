package gg.essential.elementa.utils

var elementaDev: Boolean = false
    set(value) {
        if (devPropSet) {
            field = value
        }
    }

var elementaDebug: Boolean = false
    set(value) {
        if (debugPropSet) {
            field = value
        }
    }

private val devPropSet = System.getProperty("elementa.dev")?.toBoolean() ?: false
private val debugPropSet = System.getProperty("elementa.debug")?.toBoolean() ?: false
