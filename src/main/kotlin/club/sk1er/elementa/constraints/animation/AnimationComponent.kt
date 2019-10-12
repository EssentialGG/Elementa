package club.sk1er.elementa.constraints.animation

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.*
import java.awt.Color
import kotlin.math.roundToInt

sealed class AnimationComponent(
    private val strategy: AnimationStrategy,
    private val totalFrames: Int
) : SuperConstraint {
    private var elapsedFrames = 0

    override fun animationFrame() {
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
        val startX = oldConstraint.getXPosition(component, parent)
        val finalX = newConstraint.getXPosition(component, parent)

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
        val startX = oldConstraint.getYPosition(component, parent)
        val finalX = newConstraint.getYPosition(component, parent)

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
        val startX = oldConstraint.getXSize(component, parent)
        val finalX = newConstraint.getXSize(component, parent)

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
        val startX = oldConstraint.getYSize(component, parent)
        val finalX = newConstraint.getYSize(component, parent)

        return startX + ((finalX - startX) * getPercentComplete())
    }
}

class ColorAnimationComponent(
    strategy: AnimationStrategy,
    totalFrames: Int,
    private val oldConstraint: ColorConstraint,
    val newConstraint: ColorConstraint
) : AnimationComponent(strategy, totalFrames), ColorConstraint {
    override fun getColor(component: UIComponent, parent: UIComponent): Color {
        val startColor = oldConstraint.getColor(component, parent)
        val endColor = newConstraint.getColor(component, parent)
        val percentComplete = getPercentComplete()

        val newR = startColor.red + ((endColor.red - startColor.red) * percentComplete)
        val newG = startColor.green + ((endColor.green - startColor.green) * percentComplete)
        val newB = startColor.blue + ((endColor.blue - startColor.blue) * percentComplete)
        val newA = startColor.alpha + ((endColor.alpha - startColor.alpha) * percentComplete)

        return Color(newR.roundToInt(), newG.roundToInt(), newB.roundToInt(), newA.roundToInt())
    }
}