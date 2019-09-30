package club.sk1er.elementa

import club.sk1er.elementa.animations.AnimationPhase

class Animator {
    val animations = mutableListOf<AnimationPhase>()

    fun getCurrentX(): Float = animations[0].constraints.getX() // TODO
    fun getCurrentY(): Float = animations[0].constraints.getY() // TODO

    fun getCurrentWidth(): Float = animations[0].constraints.getWidth() // TODO
    fun getCurrentHeight(): Float = animations[0].constraints.getHeight() // TODO
}