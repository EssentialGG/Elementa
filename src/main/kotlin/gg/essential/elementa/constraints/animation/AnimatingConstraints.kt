package gg.essential.elementa.constraints.animation

import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.*

class AnimatingConstraints(
    component: UIComponent,
    private val oldConstraints: UIConstraints
) : UIConstraints(component) {

    var completeAction: () -> Unit = {}
    private var extraDelayFrames = 0

    init {
        this.x = oldConstraints.x
        this.y = oldConstraints.y
        this.width = oldConstraints.width
        this.height = oldConstraints.height
        this.radius = oldConstraints.radius
        this.textScale = oldConstraints.textScale
        this.color = oldConstraints.color
        this.fontProvider = oldConstraints.fontProvider
    }

    fun begin() = apply {
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
    fun setWidthAnimation(strategy: AnimationStrategy, time: Float, newConstraint: WidthConstraint, delay: Float = 0f) =
        apply {
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
    fun setHeightAnimation(
        strategy: AnimationStrategy,
        time: Float,
        newConstraint: HeightConstraint,
        delay: Float = 0f
    ) = apply {
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
    fun setRadiusAnimation(
        strategy: AnimationStrategy,
        time: Float,
        newConstraint: RadiusConstraint,
        delay: Float = 0f
    ) = apply {
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
    fun setTextScaleAnimation(
        strategy: AnimationStrategy,
        time: Float,
        newConstraint: HeightConstraint,
        delay: Float = 0f
    ) = apply {
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
    fun setColorAnimation(strategy: AnimationStrategy, time: Float, newConstraint: ColorConstraint, delay: Float = 0f) =
        apply {
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

    /**
     * Sets the "extra delay" of this animation. This delay does not affect any of the actual changes in the animation,
     * such as color shift or position change, rather, it simply delays the completion of the animation. This is mostly
     * relevant for [UIComponent.animateBeforeHide] which hides the element when the animation is completed.
     */
    fun setExtraDelay(delay: Float) {
        extraDelayFrames = (delay * Window.of(component).animationFPS).toInt()
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
            if (x.isComplete()) this.x = x.newConstraint
            else anyLeftAnimating = true
        }

        val y = y
        if (y is YAnimationComponent) {
            if (y.isComplete()) this.y = y.newConstraint
            else anyLeftAnimating = true
        }

        val width = width
        if (width is WidthAnimationComponent) {
            if (width.isComplete()) this.width = width.newConstraint
            else anyLeftAnimating = true
        }

        val height = height
        if (height is HeightAnimationComponent) {
            if (height.isComplete()) this.height = height.newConstraint
            else anyLeftAnimating = true
        }

        val radius = radius
        if (radius is RadiusAnimationComponent) {
            if (radius.isComplete()) this.radius = radius.newConstraint
            else anyLeftAnimating = true
        }

        val textScale = textScale
        if (textScale is HeightAnimationComponent) {
            if (textScale.isComplete()) this.textScale = textScale.newConstraint
            else anyLeftAnimating = true
        }

        val color = color
        if (color is ColorAnimationComponent) {
            if (color.isComplete()) this.color = color.newConstraint
            else anyLeftAnimating = true
        }

        if (extraDelayFrames > 0) {
            anyLeftAnimating = true
            extraDelayFrames--
        }

        if (!anyLeftAnimating) {
            component.constraints = UIConstraints(component).apply {
                this.x = this@AnimatingConstraints.x
                this.y = this@AnimatingConstraints.y
                this.width = this@AnimatingConstraints.width
                this.height = this@AnimatingConstraints.height
                this.radius = this@AnimatingConstraints.radius
                this.textScale = this@AnimatingConstraints.textScale
                this.color = this@AnimatingConstraints.color
                this.fontProvider = this@AnimatingConstraints.fontProvider
            }
            completeAction()
        }
    }
}