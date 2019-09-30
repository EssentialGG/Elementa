package club.sk1er.elementa.animations

import club.sk1er.elementa.UIConstraints

data class AnimationPhase @JvmOverloads constructor(
    val constraints: UIConstraints,
    val animationStrategy: AnimationStrategy = NoAnimationStrategy()
)