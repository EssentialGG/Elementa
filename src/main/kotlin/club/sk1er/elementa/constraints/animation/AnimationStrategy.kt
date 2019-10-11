package club.sk1er.elementa.constraints.animation

interface AnimationStrategy {
    fun getValue(percentComplete: Float): Float
}

object LinearStrategy : AnimationStrategy {
    override fun getValue(percentComplete: Float): Float {
        return percentComplete
    }
}