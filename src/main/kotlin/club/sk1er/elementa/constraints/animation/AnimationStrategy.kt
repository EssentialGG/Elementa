package club.sk1er.elementa.constraints.animation

interface AnimationStrategy {
    fun getValue(percentComplete: Float): Float
}

object LinearStrategy : AnimationStrategy {
    override fun getValue(percentComplete: Float): Float {
        return percentComplete
    }
}

object EaseOutStrategy : AnimationStrategy {
    override fun getValue(percentComplete: Float): Float {
        return -percentComplete * (percentComplete - 2)
    }
}

object EaseInStrategy : AnimationStrategy {
    override fun getValue(percentComplete: Float): Float {
        return percentComplete * percentComplete
    }
}

object EaseInOutStrategy : AnimationStrategy {
    override fun getValue(percentComplete: Float): Float {
        var t = percentComplete * 2
        if (t < 1) return 0.5f * t * t
        t--
        return -0.5f * (t * (t - 2) - 1)
    }
}