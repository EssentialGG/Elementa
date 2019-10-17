package club.sk1er.elementa.constraints.animation

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.Window
import club.sk1er.elementa.constraints.*

class AnimatingConstraints(
    component: UIComponent,
    private val oldConstraints: UIConstraints
) : UIConstraints(component) {

    var completeAction: () -> Unit = {}

    init {
        this.x = oldConstraints.x
        this.y = oldConstraints.y
        this.width = oldConstraints.width
        this.height = oldConstraints.height
        this.color = oldConstraints.color
    }

    fun begin() {
        component.animateTo(this)
    }

    @JvmOverloads
    fun setXAnimation(strategy: AnimationStrategy, time: Float, newConstraint: PositionConstraint, delay: Float = 0f) = apply {
        val totalFrames = time * Window.of(component).animationFPS
        val totalDelay = delay * Window.of(component).animationFPS

        x = XAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.x,
            newConstraint,
            totalDelay.toInt()
        )
    }

    @JvmOverloads
    fun setYAnimation(strategy: AnimationStrategy, time: Float, newConstraint: PositionConstraint, delay: Float = 0f) = apply {
        val totalFrames = time * Window.of(component).animationFPS
        val totalDelay = delay * Window.of(component).animationFPS

        y = YAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.y,
            newConstraint,
            totalDelay.toInt()
        )
    }

    @JvmOverloads
    fun setWidthAnimation(strategy: AnimationStrategy, time: Float, newConstraint: SizeConstraint, delay: Float = 0f) = apply {
        val totalFrames = time * Window.of(component).animationFPS
        val totalDelay = delay * Window.of(component).animationFPS

        width = WidthAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.width,
            newConstraint,
            totalDelay.toInt()
        )
    }

    @JvmOverloads
    fun setHeightAnimation(strategy: AnimationStrategy, time: Float, newConstraint: SizeConstraint, delay: Float = 0f) = apply {
        val totalFrames = time * Window.of(component).animationFPS
        val totalDelay = delay * Window.of(component).animationFPS

        height = HeightAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.height,
            newConstraint,
            totalDelay.toInt()
        )
    }

    @JvmOverloads
    fun setColorAnimation(strategy: AnimationStrategy, time: Float, newConstraint: ColorConstraint, delay: Float = 0f) = apply {
        val totalFrames = time * Window.of(component).animationFPS
        val totalDelay = delay * Window.of(component).animationFPS

        color = ColorAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.color,
            newConstraint,
            totalDelay.toInt()
        )
    }

    fun onComplete(method: () -> Unit) = apply {
        completeAction = method
    }

    fun onComplete(method: Runnable) = apply {
        completeAction = method::run
    }

    override fun animationFrame() {
        super.animationFrame()

        var anyLeftAnimating = false

        val x = x
        if (x is XAnimationComponent) {
            if (x.complete()) this.x = x.newConstraint
            else anyLeftAnimating = true
        }

        val y = y
        if (y is YAnimationComponent) {
            if (y.complete()) this.y = y.newConstraint
            else anyLeftAnimating = true
        }

        val width = width
        if (width is WidthAnimationComponent) {
            if (width.complete()) this.width = width.newConstraint
            else anyLeftAnimating = true
        }

        val height = height
        if (height is HeightAnimationComponent) {
            if (height.complete()) this.height = height.newConstraint
            else anyLeftAnimating = true
        }

        val color = color
        if (color is ColorAnimationComponent) {
            if (color.complete()) this.color = color.newConstraint
            else anyLeftAnimating = true
        }

        if (!anyLeftAnimating) {
            component.setConstraints(UIConstraints(component).apply {
                this.x = this@AnimatingConstraints.x
                this.y = this@AnimatingConstraints.y
                this.width = this@AnimatingConstraints.width
                this.height = this@AnimatingConstraints.height
                this.color = this@AnimatingConstraints.color
            })
            completeAction()
        }
    }

    override fun setX(constraint: XConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setY(constraint: YConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setWidth(constraint: WidthConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setHeight(constraint: HeightConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")

    override fun setColor(constraint: ColorConstraint) =
        throw UnsupportedOperationException("Can't call setter methods on an animation")
}