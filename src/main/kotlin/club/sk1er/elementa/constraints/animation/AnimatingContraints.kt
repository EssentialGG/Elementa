package club.sk1er.elementa.constraints.animation

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.PositionConstraint
import club.sk1er.elementa.constraints.SizeConstraint
import javax.naming.OperationNotSupportedException

class AnimatingConstraints(
    component: UIComponent,
    private val oldConstraints: UIConstraints
) : UIConstraints(component) {
    fun setXAnimation(strategy: AnimationStrategy, time: Float, newConstraint: PositionConstraint) = apply {
        val totalFrames = time * Window.ANIMATION_FPS

        xConstraint = XAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.xConstraint,
            newConstraint
        )
    }

    fun setYAnimation(strategy: AnimationStrategy, time: Float, newConstraint: PositionConstraint) = apply {
        val totalFrames = time * Window.ANIMATION_FPS

        yConstraint = YAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.yConstraint,
            newConstraint
        )
    }

    fun setWidthAnimation(strategy: AnimationStrategy, time: Float, newConstraint: SizeConstraint) = apply {
        val totalFrames = time * Window.ANIMATION_FPS

        widthConstraint = WidthAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.widthConstraint,
            newConstraint
        )
    }

    fun setHeightAnimation(strategy: AnimationStrategy, time: Float, newConstraint: SizeConstraint) = apply {
        val totalFrames = time * Window.ANIMATION_FPS

        heightConstraint = HeightAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.heightConstraint,
            newConstraint
        )
    }

    fun animationFrame() {
        var anyLeftAnimating = false

        val x = xConstraint
        if (x is XAnimationComponent) {
            x.animationFrame()

            if (x.complete()) xConstraint = x.newConstraint
            else anyLeftAnimating = true
        }

        val y = yConstraint
        if (y is YAnimationComponent) {
            y.animationFrame()

            if (y.complete()) yConstraint = y.newConstraint
            else anyLeftAnimating = true
        }

        val width = widthConstraint
        if (width is WidthAnimationComponent) {
            width.animationFrame()

            if (width.complete()) widthConstraint = width.newConstraint
            else anyLeftAnimating = true
        }

        val height = heightConstraint
        if (height is HeightAnimationComponent) {
            height.animationFrame()

            if (height.complete()) heightConstraint = height.newConstraint
            else anyLeftAnimating = true
        }

        if (!anyLeftAnimating) {
            component.setConstraints(UIConstraints(component).apply {
                xConstraint = this@AnimatingConstraints.xConstraint
                yConstraint = this@AnimatingConstraints.yConstraint
                widthConstraint = this@AnimatingConstraints.widthConstraint
                heightConstraint = this@AnimatingConstraints.heightConstraint
            })
        }
    }

    override fun setX(constraint: PositionConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")

    override fun setY(constraint: PositionConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")

    override fun setWidth(constraint: SizeConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")

    override fun setHeight(constraint: SizeConstraint) =
        throw OperationNotSupportedException("Can't call setter methods on an animation")
}