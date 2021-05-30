package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor

/**
 * Tries to expand to fill all of the remaining width/height available in this component's
 * parent.
 *
 * When [useSiblings] is true, this constraint will have a size equal to the difference
 * between the parent's size and the sum of the sibling's size. When [useSiblings] is false,
 * it will only consider the position of this component and fill the rest of the space.
 */
class FillConstraint @JvmOverloads constructor(private val useSiblings: Boolean = true) : SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getWidthImpl(component: UIComponent): Float {
        val target = constrainTo ?: component.parent

        return if (useSiblings) {
            target.getWidth() - target.children.filter { it != component }.sumOf {
                it.getWidth().toDouble()
            }.toFloat()
        } else target.getRight() - component.getLeft()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        val target = constrainTo ?: component.parent

        return if (useSiblings) {
            target.getHeight() - target.children.filter { it != component }.sumOf {
                it.getHeight().toDouble()
            }.toFloat()
        } else target.getBottom() - component.getTop()
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        val target = constrainTo ?: component.parent

        return if (useSiblings) {
            target.getRadius() - target.children.filter { it != component }.sumOf {
                it.getRadius().toDouble()
            }.toFloat()
        } else (target.getRadius() - component.getLeft()) / 2f
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.WIDTH -> {
                visitor.visitParent(ConstraintType.WIDTH)

                if (useSiblings) {
                    val indexInParent = visitor.component.let { it.parent.children.indexOf(it) }
                    val numParentChildren = visitor.component.parent.children.size

                    for (i in 0 until numParentChildren) {
                        if (indexInParent != i)
                            visitor.visitSibling(ConstraintType.WIDTH, i)
                    }
                } else {
                    visitor.visitParent(ConstraintType.X)
                    visitor.visitSelf(ConstraintType.X)
                }
            }
            ConstraintType.HEIGHT -> {
                visitor.visitParent(ConstraintType.HEIGHT)

                if (useSiblings) {
                    val indexInParent = visitor.component.let { it.parent.children.indexOf(it) }
                    val numParentChildren = visitor.component.parent.children.size

                    for (i in 0 until numParentChildren) {
                        if (indexInParent != i)
                            visitor.visitSibling(ConstraintType.HEIGHT, i)
                    }
                } else {
                    visitor.visitParent(ConstraintType.Y)
                    visitor.visitSelf(ConstraintType.Y)
                }
            }
            ConstraintType.RADIUS -> {
                visitor.visitParent(ConstraintType.RADIUS)

                if (useSiblings) {
                    val indexInParent = visitor.component.let { it.parent.children.indexOf(it) }
                    val numParentChildren = visitor.component.parent.children.size

                    for (i in 0 until numParentChildren) {
                        if (indexInParent != i)
                            visitor.visitSibling(ConstraintType.RADIUS, i)
                    }
                } else {
                    visitor.visitSelf(ConstraintType.X)
                }
            }
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}
