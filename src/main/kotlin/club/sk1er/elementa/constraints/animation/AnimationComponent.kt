package club.sk1er.elementa.constraints.animation

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.HeightConstraint
import club.sk1er.elementa.constraints.WidthConstraint
import club.sk1er.elementa.constraints.XConstraint
import club.sk1er.elementa.constraints.YConstraint

sealed class AnimationComponent(
    private val strategy: AnimationStrategy,
    private val totalFrames: Int
) {
    private var elapsedFrames = 0

    fun animationFrame() {
        if (elapsedFrames >= totalFrames) return

        elapsedFrames++
    }

    fun complete() = elapsedFrames >= totalFrames

    fun getPercentComplete() = strategy.getValue(elapsedFrames.toFloat() / totalFrames.toFloat())
}

class XAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldConstraint: XConstraint,
    val newConstraint: XConstraint
) : AnimationComponent(strategy, totalFrames), XConstraint {
    override fun getXPosition(component: UIComponent, parent: UIComponent): Float {
        val startX = oldConstraint.getXPosition(component, component.parent)
        val finalX = newConstraint.getXPosition(component, component.parent)

        return startX + ((finalX - startX) * getPercentComplete())
    }
}

class YAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldConstraint: YConstraint,
    val newConstraint: YConstraint
) : AnimationComponent(strategy, totalFrames), YConstraint {
    override fun getYPosition(component: UIComponent, parent: UIComponent): Float {
        val startX = oldConstraint.getYPosition(component, component.parent)
        val finalX = newConstraint.getYPosition(component, component.parent)

        return startX + ((finalX - startX) * getPercentComplete())
    }
}

class WidthAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldConstraint: WidthConstraint,
    val newConstraint: WidthConstraint
) : AnimationComponent(strategy, totalFrames), WidthConstraint {
    override fun getXSize(component: UIComponent, parent: UIComponent): Float {
        val startX = oldConstraint.getXSize(component, component.parent)
        val finalX = newConstraint.getXSize(component, component.parent)

        return startX + ((finalX - startX) * getPercentComplete())
    }
}

class HeightAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldConstraint: HeightConstraint,
    val newConstraint: HeightConstraint
) : AnimationComponent(strategy, totalFrames), HeightConstraint {
    override fun getYSize(component: UIComponent, parent: UIComponent): Float {
        val startX = oldConstraint.getYSize(component, component.parent)
        val finalX = newConstraint.getYSize(component, component.parent)

        return startX + ((finalX - startX) * getPercentComplete())
    }
}