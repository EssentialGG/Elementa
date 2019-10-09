package club.sk1er.elementa.constraints.animation

class AnimationComponent(
    private val strategy: AnimationStrategy,
    private val startValue: Float,
    private val targetValue: Float,
    private val totalFrames: Int
) {
    private var elapsedFrames = 0

    fun animationFrame() {
        strategy.animationFrame()
    }

    fun getValue() = strategy.getValue(
        startValue,
        targetValue,
        totalFrames,
        elapsedFrames
    )
}