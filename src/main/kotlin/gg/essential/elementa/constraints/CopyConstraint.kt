package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import java.awt.Color
import java.lang.IllegalStateException

class CopyConstraintFloat(private val textScale: Boolean = false) : PositionConstraint, SizeConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    override fun getXPositionImpl(component: UIComponent): Float {
        return constrainTo?.constraints?.getX()
            ?: throw IllegalStateException("CopyConstraint must be applied to another component.")
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return constrainTo?.constraints?.getY()
            ?: throw IllegalStateException("CopyConstraint must be applied to another component.")
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return constrainTo?.constraints?.getWidth()
            ?: throw IllegalStateException("CopyConstraint must be applied to another component.")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        val to = constrainTo ?: throw IllegalStateException("CopyConstraint must be applied to another component.")

        when (type) {
            ConstraintType.X -> {
                to.constraints.x.visitImpl(visitor, type)
            }
            ConstraintType.Y -> {
                to.constraints.y.visitImpl(visitor, type)
            }
            ConstraintType.WIDTH -> {
                to.constraints.width.visitImpl(visitor, type)
            }
            ConstraintType.HEIGHT -> {
                to.constraints.height.visitImpl(visitor, type)
            }

            ConstraintType.RADIUS -> {
                to.constraints.radius.visitImpl(visitor, type)
            }
            ConstraintType.TEXT_SCALE -> {
                to.constraints.textScale.visitImpl(visitor, type)
            }

            else -> throw IllegalArgumentException(type.prettyName)
        }

    }

    override fun getHeightImpl(component: UIComponent): Float {
        return if (textScale) {
            constrainTo?.constraints?.getTextScale()
                ?: throw IllegalStateException("CopyConstraint must be applied to another component.")
        } else constrainTo?.constraints?.getHeight()
            ?: throw IllegalStateException("CopyConstraint must be applied to another component.")
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return constrainTo?.constraints?.getRadius()
            ?: throw IllegalStateException("CopyConstraint must be applied to another component.")
    }
}

class CopyConstraintColor : ColorConstraint {

    override var cachedValue = Color.WHITE
    override var recalculate = true
    override var constrainTo: UIComponent? = null


    override fun getColorImpl(component: UIComponent): Color {
        return constrainTo?.constraints?.getColor()
            ?: throw IllegalStateException("CopyConstraint must be applied to another component.")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {

    }

}