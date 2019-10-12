package club.sk1er.elementa.constraints.animation

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

interface AnimationStrategy {
    fun getValue(percentComplete: Float): Float
}

enum class Animations : AnimationStrategy {
    LINEAR {
        override fun getValue(percentComplete: Float): Float {
            return percentComplete
        }
    },
    IN_QUAD {
        override fun getValue(percentComplete: Float): Float {
            return percentComplete.pow(2)
        }
    },
    OUT_QUAD {
        override fun getValue(percentComplete: Float): Float {
            return -percentComplete * (percentComplete - 2)
        }
    },
    IN_OUT_QUAD {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete * 2
            if (t < 1) return 0.5f * t.pow(2)
            t--
            return -0.5f * (t * (t - 2) - 1)
        }
    },
    IN_CUBIC {
        override fun getValue(percentComplete: Float): Float {
            return percentComplete.pow(3)
        }
    },
    OUT_CUBIC {
        override fun getValue(percentComplete: Float): Float {
            val t = percentComplete - 1
            return t.pow(3) + 1
        }
    },
    IN_OUT_CUBIC {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete * 2
            if (t < 1) return 0.5f * t.pow(3)
            t -= 2
            return 0.5f * (t.pow(3) + 2)
        }
    },
    IN_QUART {
        override fun getValue(percentComplete: Float): Float {
            return percentComplete.pow(4)
        }
    },
    OUT_QUART {
        override fun getValue(percentComplete: Float): Float {
            val t = percentComplete - 1
            return -(t.pow(4) - 1)
        }
    },
    IN_OUT_QUART {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete * 2
            if (t < 1) return 0.5f * t.pow(4)
            t -= 2
            return -0.5f * (t.pow(4) - 2)
        }
    },
    IN_QUINT {
        override fun getValue(percentComplete: Float): Float {
            return percentComplete.pow(5)
        }
    },
    OUT_QUINT {
        override fun getValue(percentComplete: Float): Float {
            val t = percentComplete - 1
            return t.pow(5) + 1
        }
    },
    IN_OUT_QUINT {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete * 2
            if (t < 1) return 0.5f * t.pow(5)
            t -= 2
            return 0.5f * (t.pow(5) + 2)
        }
    },
    IN_SIN {
        override fun getValue(percentComplete: Float): Float {
            return (-cos(percentComplete * (Math.PI / 2)) + 1).toFloat()
        }
    },
    OUT_SIN {
        override fun getValue(percentComplete: Float): Float {
            return (sin(percentComplete * (Math.PI / 2))).toFloat()
        }
    },
    IN_OUT_SIN {
        override fun getValue(percentComplete: Float): Float {
            return (-0.5 * (cos(Math.PI * percentComplete) - 1)).toFloat()
        }
    },
    IN_EXP {
        override fun getValue(percentComplete: Float): Float {
            return 2f.pow(10 * (percentComplete - 1))
        }
    },
    OUT_EXP {
        override fun getValue(percentComplete: Float): Float {
            return -(2f.pow(-10 * percentComplete)) + 1
        }
    },
    IN_OUT_EXP {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete * 2
            if (t < 1) return 0.5f * 2f.pow(10 * (t - 1))
            t--
            return 0.5f * (-(2f.pow(-10 * t)) + 2)
        }
    },
    IN_CIRCULAR {
        override fun getValue(percentComplete: Float): Float {
            return -(sqrt(1 - percentComplete.pow(2)) - 1)
        }
    },
    OUT_CIRCULAR {
        override fun getValue(percentComplete: Float): Float {
            val t = percentComplete - 1
            return sqrt(1 - t.pow(2))
        }
    },
    IN_OUT_CIRCULAR {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete * 2
            if (t < 1) return -0.5f * (sqrt(1 - t.pow(2)) - 1)
            t -= 2
            return 0.5f * (sqrt(1 - t.pow(2)) + 1)
        }
    }
}