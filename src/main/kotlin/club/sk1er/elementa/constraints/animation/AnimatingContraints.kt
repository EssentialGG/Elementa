package club.sk1er.elementa.constraints.animation

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.ColorConstraint
import club.sk1er.elementa.constraints.PositionConstraint
import club.sk1er.elementa.constraints.SizeConstraint

class AnimatingConstraints(
    component: UIComponent,
    private val oldConstraints: UIConstraints
) : UIConstraints(component) {

    var completeAction: () -> Unit = {}

    init {
        this.xConstraint = oldConstraints.xConstraint
        this.yConstraint = oldConstraints.yConstraint
        this.widthConstraint = oldConstraints.widthConstraint
        this.heightConstraint = oldConstraints.heightConstraint
        this.colorConstraint = oldConstraints.colorConstraint
    }

    fun begin() {
        component.animateTo(this)
    }

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

    fun setColorAnimation(strategy: AnimationStrategy, time: Float, newConstraint: ColorConstraint) = apply {
        val totalFrames = time * Window.ANIMATION_FPS

        colorConstraint = ColorAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.colorConstraint,
            newConstraint
        )
    }

    fun onComplete(method: () -> Unit) {
        completeAction = method
    }

    fun onComplete(method: Runnable) {
        completeAction = { method.run() }
    }

    override fun animationFrame() {
        super.animationFrame()

        var anyLeftAnimating = false

        val x = xConstraint
        if (x is XAnimationComponent) {
            if (x.complete()) xConstraint = x.newConstraint
            else anyLeftAnimating = true
        }

        val y = yConstraint
        if (y is YAnimationComponent) {
            if (y.complete()) yConstraint = y.newConstraint
            else anyLeftAnimating = true
        }

        val width = widthConstraint
        if (width is WidthAnimationComponent) {
            if (width.complete()) widthConstraint = width.newConstraint
            else anyLeftAnimating = true
        }

        val height = heightConstraint
        if (height is HeightAnimationComponent) {
            if (height.complete()) heightConstraint = height.newConstraint
            else anyLeftAnimating = true
        }

        val color = colorConstraint
        if (color is ColorAnimationComponent) {
            if (color.complete()) colorConstraint = color.newConstraint
            else anyLeftAnimating = true
        }

        if (!anyLeftAnimating) {
            component.setConstraints(UIConstraints(component).apply {
                xConstraint = this@AnimatingConstraints.xConstraint
                yConstraint = this@AnimatingConstraints.yConstraint
                widthConstraint = this@AnimatingConstraints.widthConstraint
                heightConstraint = this@AnimatingConstraints.heightConstraint
                colorConstraint = this@AnimatingConstraints.colorConstraint
            })
            completeAction()
        }
    }

    override fun setX(constraint: PositionConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setY(constraint: PositionConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setWidth(constraint: SizeConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setHeight(constraint: SizeConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setColor(constraint: ColorConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")
}