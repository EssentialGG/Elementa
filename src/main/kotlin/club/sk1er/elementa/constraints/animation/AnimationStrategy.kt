package club.sk1er.elementa.constraints.animation

interface AnimationStrategy {
    fun getValue(start: Float, end: Float, totalFrames: Int, elapsedFrames: Int): Float

    fun animationFrame()
}