package club.sk1er.elementa.constraints.animation

interface AnimationStrategy {
    fun getValue(percentComplete: Float): Float
}

enum class Animations : AnimationStrategy {
    LINEAR {
        override fun getValue(percentComplete: Float): Float {
            return percentComplete
        }
    },
    OUT_QUAD {
        override fun getValue(percentComplete: Float): Float {
            return -percentComplete * (percentComplete - 2)
        }
    },
    IN_QUAD {
        override fun getValue(percentComplete: Float): Float {
            return percentComplete * percentComplete
        }
    },
    IN_OUT_QUAD {
        override fun getValue(percentComplete: Float): Float {
            var t = percentComplete * 2
            if (t < 1) return 0.5f * t * t
            t--
            return -0.5f * (t * (t - 2) - 1)
        }
    }
}