package gg.essential.elementa.constraints.animation

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Provides a mapping from 0f - 1f to 0f - 1f (however, this output value can technically go past those bounds).
 */
interface AnimationStrategy {
    fun getValue(percentComplete: Float): Float
}

/**
 * Most of the basic animations that someone would want to use.
 *
 * If you're not sure what these algorithms look like,
 * use the [https://easings.net/en] website as a reference.
 */
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
    },
    IN_ELASTIC {
        override fun getValue(percentComplete: Float): Float {
            val t = percentComplete - 1
            return -(2f.pow(10 * t)) * sin((t - 0.075f) * (2 * Math.PI.toFloat()) / 0.3f)
        }
    },
    OUT_ELASTIC {
        override fun getValue(percentComplete: Float): Float {
            return 2f.pow(-10 * percentComplete) * sin((percentComplete - 0.075f) * (2 * Math.PI.toFloat()) / 0.3f ) + 1
        }
    },
    IN_OUT_ELASTIC {
        override fun getValue(percentComplete: Float): Float {
            val t = percentComplete * 2f - 1
            if (t < 0) return 0.5f * -(2f.pow(10 * t)) * sin((t - 0.1125f) * (2 * Math.PI.toFloat()) / 0.45f)
            return 0.5f * 2f.pow(-10 * t) * sin((t - 0.1125f) * (2 * Math.PI.toFloat()) / 0.45f) + 1
        }
    },
    IN_BOUNCE {
        override fun getValue(percentComplete: Float): Float {
            return 1 - OUT_BOUNCE.getValue(1 - percentComplete)
        }
    },
    OUT_BOUNCE {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete
            return when {
                t < 1 / 2.75f -> 7.5625f * t * t
                t < 2 / 2.75f -> {
                    t -= 1.5f / 2.75f
                    7.5625f * t * t + 0.75f
                }
                t < 2.5 / 2.75 -> {
                    //return c*(7.5625f*(t-=(2.25f/2.75f))*t + .9375f) + b;
                    t -= 2.25f / 2.75f
                    7.5625f * t * t + 0.9375f
                }
                else -> {
                    t -= 2.625f / 2.75f
                    7.5625f * t * t + 0.984375f
                }
            }
        }
    },
    IN_OUT_BOUNCE {
        override fun getValue(percentComplete: Float): Float {
            if (percentComplete < 0.5f) return IN_BOUNCE.getValue(percentComplete * 2) * 0.5f
            return OUT_BOUNCE.getValue(percentComplete * 2 - 1) * 0.5f + 0.5f
        }
    }
}