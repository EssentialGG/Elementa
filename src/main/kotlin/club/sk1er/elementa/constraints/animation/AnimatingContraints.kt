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
        this.radius = oldConstraints.radius
        this.textScale = oldConstraints.textScale
        this.color = oldConstraints.color
    }

    fun begin() {
        component.animateTo(this)
    }

    @JvmOverloads
    fun setXAnimation(strategy: AnimationStrategy, time: Float, newConstraint: XConstraint, delay: Float = 0f) = apply {
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
    fun setYAnimation(strategy: AnimationStrategy, time: Float, newConstraint: YConstraint, delay: Float = 0f) = apply {
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
    fun setWidthAnimation(strategy: AnimationStrategy, time: Float, newConstraint: WidthConstraint, delay: Float = 0f) = apply {
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
    fun setHeightAnimation(strategy: AnimationStrategy, time: Float, newConstraint: HeightConstraint, delay: Float = 0f) = apply {
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
    fun setRadiusAnimation(strategy: AnimationStrategy, time: Float, newConstraint: RadiusConstraint, delay: Float = 0f) = apply {
        val totalFrames = time * Window.of(component).animationFPS
        val totalDelay = delay * Window.of(component).animationFPS

        radius = RadiusAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.radius,
            newConstraint,
            totalDelay.toInt()
        )
    }

    @JvmOverloads
    fun setTextScaleAnimation(strategy: AnimationStrategy, time: Float, newConstraint: HeightConstraint, delay: Float = 0f) = apply {
        val totalFrames = time * Window.of(component).animationFPS
        val totalDelay = delay * Window.of(component).animationFPS

        textScale = HeightAnimationComponent(
            strategy,
            totalFrames.toInt(),
            oldConstraints.textScale,
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

    fun onCompleteRunnable(method: Runnable) = apply {
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

        val radius = radius
        if (radius is RadiusAnimationComponent) {
            if (radius.complete()) this.radius = radius.newConstraint
            else anyLeftAnimating = true
        }

        val textScale = textScale
        if (textScale is HeightAnimationComponent) {
            if (textScale.complete())  this.textScale = textScale.newConstraint
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
                this.radius = this@AnimatingConstraints.radius
                this.textScale = this@AnimatingConstraints.textScale
                this.color = this@AnimatingConstraints.color
            })
            completeAction()
        }
    }
}