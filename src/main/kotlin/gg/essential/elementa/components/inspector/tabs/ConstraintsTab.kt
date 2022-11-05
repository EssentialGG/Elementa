package gg.essential.elementa.components.inspector.tabs

import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.components.TreeListComponent
import gg.essential.elementa.components.TreeNode
import gg.essential.elementa.components.inspector.InfoBlockNode
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.*
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.provideDelegate
import org.jetbrains.annotations.ApiStatus
import java.awt.Color

@ApiStatus.Internal
class ConstraintsTab : InspectorTab("Constraints") {

    private val constraintsContent by TreeListComponent().constrain {
        width = ChildBasedMaxSizeConstraint()
        height = ChildBasedSizeConstraint()
    } childOf this

    private val currentRoots = mutableMapOf<ConstraintType, TreeNode?>()


    private fun setNewConstraints(constraints: UIConstraints) {
        setConstraintNodes(constraints)

        constraints.addObserver { _, arg ->
            if (arg !is ConstraintType)
                return@addObserver
            val constraint = when (arg) {
                ConstraintType.X -> constraints.x
                ConstraintType.Y -> constraints.y
                ConstraintType.WIDTH -> constraints.width
                ConstraintType.HEIGHT -> constraints.height
                ConstraintType.RADIUS -> constraints.radius
                ConstraintType.COLOR -> constraints.color
                ConstraintType.TEXT_SCALE -> constraints.textScale
                ConstraintType.FONT_PROVIDER -> constraints.fontProvider
            }

            when (arg) {
                ConstraintType.COLOR -> {
                    currentRoots[arg] =
                        if (constraint !is ConstantColorConstraint || constraint.color != Color.WHITE) getNodeFromConstraint(
                            constraint,
                            arg.prettyName
                        ) else null
                }
                ConstraintType.TEXT_SCALE -> {
                    currentRoots[ConstraintType.TEXT_SCALE] =
                        if (constraint !is PixelConstraint || constraint.value != 1f) getNodeFromConstraint(
                            constraint,
                            arg.prettyName
                        ) else null
                }
                else -> {
                    currentRoots[arg] =
                        if (constraint !is PixelConstraint || constraint.value != 0f) getNodeFromConstraint(
                            constraint,
                            arg.prettyName
                        ) else null
                }
            }

            constraintsContent.setRoots(currentRoots.values.filterNotNull())
        }
    }

    private fun setConstraintNodes(constraints: UIConstraints) {
        currentRoots.clear()

        listOf(
            constraints.x to ConstraintType.X,
            constraints.y to ConstraintType.Y,
            constraints.width to ConstraintType.WIDTH,
            constraints.height to ConstraintType.HEIGHT,
            constraints.radius to ConstraintType.RADIUS
        ).forEach { (constraint, type) ->
            currentRoots[type] =
                if (constraint !is PixelConstraint || constraint.value != 0f) getNodeFromConstraint(
                    constraint,
                    type.prettyName
                ) else null
        }

        constraints.textScale.also {
            currentRoots[ConstraintType.TEXT_SCALE] =
                if (it !is PixelConstraint || it.value != 1f) getNodeFromConstraint(it, "TextScale") else null
        }

        constraints.color.also {
            currentRoots[ConstraintType.COLOR] =
                if (it !is ConstantColorConstraint || it.color != Color.WHITE) getNodeFromConstraint(
                    it,
                    "Color"
                ) else null
        }

        constraintsContent.setRoots(currentRoots.values.filterNotNull())
    }

    private fun getNodeFromConstraint(constraint: SuperConstraint<*>, name: String? = null): TreeNode {
        val baseInfoNode = InfoBlockNode(constraint, name)

        return when (constraint) {
            is AdditiveConstraint -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.constraint1))
                add(getNodeFromConstraint(constraint.constraint2))
            }
            is CoerceAtMostConstraint -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.constraint))
                add(getNodeFromConstraint(constraint.maxConstraint))
            }
            is CoerceAtLeastConstraint -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.constraint))
                add(getNodeFromConstraint(constraint.minConstraint))
            }
            is SubtractiveConstraint -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.constraint1))
                add(getNodeFromConstraint(constraint.constraint2))
            }
            is XAnimationComponent -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.oldConstraint, name = "From"))
                add(getNodeFromConstraint(constraint.newConstraint, name = "To"))
            }
            is YAnimationComponent -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.oldConstraint, name = "From"))
                add(getNodeFromConstraint(constraint.newConstraint, name = "To"))
            }
            is WidthAnimationComponent -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.oldConstraint, name = "From"))
                add(getNodeFromConstraint(constraint.newConstraint, name = "To"))
            }
            is HeightAnimationComponent -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.oldConstraint, name = "From"))
                add(getNodeFromConstraint(constraint.newConstraint, name = "To"))
            }
            is RadiusAnimationComponent -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.oldConstraint, name = "From"))
                add(getNodeFromConstraint(constraint.newConstraint, name = "To"))
            }
            is ColorAnimationComponent -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.oldConstraint, name = "From"))
                add(getNodeFromConstraint(constraint.newConstraint, name = "To"))
            }
            else -> baseInfoNode
        }
    }

    override fun updateWithComponent(component: UIComponent) {
        setNewConstraints(component.constraints)
        component.addObserver { _, arg ->
            if (arg is UIConstraints) {
                setNewConstraints(arg)
            }
        }
    }

    override fun updateValues() {
    }
}