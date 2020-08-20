package club.sk1er.elementa.components.inspector

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.components.TreeNode
import club.sk1er.elementa.components.TreeView
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.*
import club.sk1er.elementa.dsl.*
import java.awt.Color

class InfoBlock(private val inspector: Inspector) : UIContainer() {
    private var cachedComponent: UIComponent? = null

    private val tabContainer = UIContainer().constrain {
        width = ChildBasedSizeConstraint() + 15.pixels()
        height = ChildBasedMaxSizeConstraint() + 10.pixels()
    } childOf this

    private val contentContainer = UIContainer().constrain {
        y = SiblingConstraint()
        width = ChildBasedSizeConstraint()
        height = ChildBasedSizeConstraint()
    } childOf this

    private val constraintsContent = TreeView().constrain {
        x = 5.pixels()
        y = 5.pixels()
        width = ChildBasedMaxSizeConstraint()
        height = ChildBasedSizeConstraint()
    } childOf contentContainer

    private val valuesContent = UIContainer().constrain {
        x = 20.pixels()
        y = 5.pixels()
        width = ChildBasedMaxSizeConstraint()
        height = ChildBasedSizeConstraint()
    }

    private lateinit var constraintsText: UIText
    private lateinit var valuesText: UIText

    private val xValueText: UIText
    private val yValueText: UIText
    private val widthValueText: UIText
    private val heightValueText: UIText

    private var constraintsSelected = true

    init {
        constraintsText = UIText("Constraints").constrain {
            x = 5.pixels()
            y = 5.pixels()
            width = TextAspectConstraint()
            color = Color.WHITE.asConstraint()
        }.onMouseEnter {
            if (!constraintsSelected) {
                constraintsText.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color.WHITE.asConstraint())
                }
            }
        }.onMouseLeave {
            if (!constraintsSelected) {
                constraintsText.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 102).asConstraint())
                }
            }
        }.onMouseClick {
            if (!constraintsSelected) {
                constraintsSelected = true
                contentContainer.removeChild(valuesContent)
                contentContainer.addChild(constraintsContent)
                valuesText.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 102).asConstraint())
                }
            }
        } as UIText childOf tabContainer

        valuesText = UIText("Values").constrain {
            x = SiblingConstraint(10f)
            y = 5.pixels()
            width = TextAspectConstraint()
            color = Color(255, 255, 255, 102).asConstraint()
        }.onMouseEnter {
            if (constraintsSelected) {
                valuesText.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color.WHITE.asConstraint())
                }
            }
        }.onMouseLeave {
            if (constraintsSelected) {
                valuesText.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 102).asConstraint())
                }
            }
        }.onMouseClick {
            if (constraintsSelected) {
                constraintsSelected = false
                contentContainer.removeChild(constraintsContent)
                contentContainer.addChild(valuesContent)
                constraintsText.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(255, 255, 255, 102).asConstraint())
                }
            }
        } as UIText childOf tabContainer

        xValueText = UIText("0")
        yValueText = UIText("0")
        widthValueText = UIText("0")
        heightValueText = UIText("0")

        initializeText("x", xValueText) childOf valuesContent
        initializeText("y", yValueText) childOf valuesContent
        initializeText("width", widthValueText) childOf valuesContent
        initializeText("height", heightValueText) childOf valuesContent
    }

    private fun initializeText(name: String, valueText: UIText): UIContainer {
        val container = UIContainer().constrain {
            y = SiblingConstraint()
            width = ChildBasedSizeConstraint()
            height = ChildBasedMaxSizeConstraint() + 3.pixels()
        }

        UIText("$name: ").constrain {
            width = TextAspectConstraint()
        } childOf container

        valueText.constrain {
            x = SiblingConstraint()
            width = TextAspectConstraint()
        }  childOf container

        return container
    }

    private fun setNewConstraints(constraints: UIConstraints) {
        setConstraintNodes(constraints)

        constraints.addObserver { _, _ ->
            setConstraintNodes(constraints)
        }
    }

    private fun setConstraintNodes(constraints: UIConstraints) {
        val nodes = mutableListOf<TreeNode>()

        listOf(
            "X" to constraints.x,
            "Y" to constraints.y,
            "Width" to constraints.width,
            "Height" to constraints.height,
            "Radius" to constraints.radius
        ).forEach { (name, constraint) ->
            if (constraint !is PixelConstraint || constraint.value != 0f)
                nodes.add(getNodeFromConstraint(constraint, name))
        }

        constraints.textScale.also {
            if (it !is PixelConstraint || it.value != 1f)
                nodes.add(getNodeFromConstraint(it, "TextScale"))
        }

        constraints.color.also {
            if (it !is ConstantColorConstraint || it.color != Color.WHITE)
                nodes.add(getNodeFromConstraint(it, "Color"))
        }

        constraintsContent.setRoots(nodes)
    }

    private fun getNodeFromConstraint(constraint: SuperConstraint<*>, name: String? = null): TreeNode {
        val baseInfoNode = InfoBlockNode(constraint, name)

        return when (constraint) {
            is AdditiveConstraint -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.constraint1))
                add(getNodeFromConstraint(constraint.constraint2))
            }
            is MaxConstraint -> baseInfoNode.withChildren {
                add(getNodeFromConstraint(constraint.constraint))
                add(getNodeFromConstraint(constraint.maxConstraint))
            }
            is MinConstraint -> baseInfoNode.withChildren {
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

    override fun draw() {
        super.draw()

        if (cachedComponent != inspector.selectedNode?.targetComponent) {
            cachedComponent = inspector.selectedNode?.targetComponent
            cachedComponent?.let {
                setNewConstraints(it.constraints)
                it.addObserver { _, arg ->
                    if (arg is UIConstraints) {
                        setNewConstraints(arg)
                    }
                }
            }
        }

        if (!constraintsSelected && cachedComponent != null) {
            xValueText.setText("%.2f".format(cachedComponent!!.getLeft()))
            yValueText.setText("%.2f".format(cachedComponent!!.getTop()))
            widthValueText.setText("%.2f".format(cachedComponent!!.getWidth()))
            heightValueText.setText("%.2f".format(cachedComponent!!.getHeight()))
        }
    }
}