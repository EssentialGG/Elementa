package club.sk1er.elementa

import club.sk1er.elementa.animations.AnimationPhase

abstract class UIComponent {
    private val children = mutableListOf<UIComponent>()
    private val animator = Animator()
    open lateinit var parent: UIComponent

    fun addChild(component: UIComponent) = apply {
        component.parent = this
        children.add(component)
    }

    fun addChildren(vararg components: UIComponent) = apply {
        components.forEach { addChild(it) }
    }

    fun removeChild(component: UIComponent) = apply {
        children.remove(component)
    }

    fun clearChildren() = apply  {
        children.clear()
    }

    fun addAnimationPhase(phase: AnimationPhase) = apply {
        animator.animations.add(phase.copy(constraints = phase.constraints.copy()))
    }

    open fun makeDefaultConstraints() = UIConstraints(this)

    fun setConstraints(constraints: UIConstraints) = apply {
        animator.animations.clear()
        animator.animations.add(AnimationPhase(constraints))
    }

    open fun getLeft() = animator.getCurrentX()

    open fun getTop() = animator.getCurrentY()

    open fun getRight() = getLeft() + animator.getCurrentWidth()

    open fun getBottom() = getTop() + animator.getCurrentHeight()

    open fun getWidth() = animator.getCurrentWidth()

    open fun getHeight() = animator.getCurrentHeight()

    open fun draw() {
        this.children.forEach(UIComponent::draw)
    }
}