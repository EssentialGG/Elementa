package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.mods.core.universal.UniversalMouse
import club.sk1er.mods.core.universal.UniversalResolutionUtil
import java.awt.Color
import java.lang.IllegalStateException

class Inspector(
    rootComponent: UIComponent,
    backgroundColor: Color = Color(40, 40, 40, 255),
    outlineColor: Color = Color(20, 20, 20, 255),
    outlineWidth: Float = 2f
) : UIContainer() {
    private val rootNode = componentToNode(rootComponent)
    private val treeBlock: UIContainer
    private val treeView: TreeView
    private val container: UIComponent
    private var selectedComponent: UIComponent? = null
    private val infoBlock: InfoBlock
    private val separator1: UIBlock
    private val separator2: UIBlock

    private var clickPos: Pair<Float, Float>? = null
    private val outlineEffect = OutlineEffect(outlineColor, outlineWidth, drawAfterChildren = true)

    private var isClickSelecting = false

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        container = UIBlock(backgroundColor).constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        } effect outlineEffect childOf this

        val titleBlock = UIContainer().constrain {
            x = CenterConstraint()
            width = ChildBasedSizeConstraint() + 30.pixels()
            height = ChildBasedMaxSizeConstraint() + 20.pixels()
        }.onMouseClick {
            clickPos = if (it.relativeX < 0 || it.relativeY < 0 || it.relativeX > getWidth() || it.relativeY > getHeight()) {
                null
            } else {
                it.relativeX to it.relativeY
            }
        }.onMouseRelease {
            clickPos = null
        }.onMouseDrag { mouseX, mouseY, button ->
            if (clickPos == null)
                return@onMouseDrag

            if (button == 0) {
                this@Inspector.constrain {
                    x = (this@Inspector.getLeft() + mouseX - clickPos!!.first).pixels()
                    y = (this@Inspector.getTop() + mouseY - clickPos!!.second).pixels()
                }
            }
        } childOf container

        val title = UIText("Inspector").constrain {
            x = 10.pixels()
            y = CenterConstraint()
            width = TextAspectConstraint()
            height = 14.pixels()
        } childOf titleBlock

        SVGComponent.ofResource("/svg/click.svg").constrain {
            x = SiblingConstraint(10f)
            y = CenterConstraint()
            width = AspectConstraint(1f)
            height = RelativeConstraint(1f).to(title) as HeightConstraint
        }.onMouseClick { event ->
            event.stopPropagation()
            isClickSelecting = true
        } childOf titleBlock

        separator1 = UIBlock(outlineColor).constrain {
            y = SiblingConstraint()
            height = 2.pixels()
        } childOf container

        treeBlock = UIContainer().constrain {
            y = SiblingConstraint()
            width = ChildBasedSizeConstraint() + 10.pixels()
            height = ChildBasedSizeConstraint() + 10.pixels()
        } childOf container

        treeView = TreeView(rootNode).constrain {
            x = 5.pixels()
            y = SiblingConstraint() + 5.pixels()
        } childOf treeBlock

        separator2 = UIBlock(outlineColor).constrain {
            y = SiblingConstraint()
            height = 2.pixels()
        }

        infoBlock = InfoBlock().constrain {
            y = SiblingConstraint()
            width = ChildBasedSizeConstraint() + 10.pixels()
            height = ChildBasedSizeConstraint() + 10.pixels()
        }
    }

    private fun componentToNode(component: UIComponent): InspectorNode {
        return InspectorNode(component).withChildren {
            component.children.forEach {
                add(componentToNode(it))
            }
        } as InspectorNode
    }

    private fun setSelectedComponent(component: UIComponent?) {
        if (component == null) {
            container.removeChild(separator2)
            container.removeChild(infoBlock)
        } else if (selectedComponent == null) {
            separator2 childOf container
            infoBlock childOf container
        }
        selectedComponent = component
    }

    override fun draw() {
        separator1.setWidth(container.getWidth().pixels())
        separator2.setWidth(container.getWidth().pixels())

        if (isClickSelecting) {
            val res = Window.of(rootNode.targetComponent).scaledResolution
            val mouseX = UniversalMouse.getScaledX().toFloat()
            val mouseY = res.scaledHeight - UniversalMouse.getTrueY() * res.scaledHeight /
                    UniversalResolutionUtil.getInstance().windowHeight - 1f
            val hitComponent = rootNode.targetComponent.hitTest(mouseX, mouseY)

            // TODO: Implement some kind of way to hook into a UIComponent to intercept events,
            //  allowing for us to stop clicks when in the selecting mode.
            if (hitComponent == this || hitComponent.isChildOf(this)) null
            else hitComponent
        } else {
            selectedComponent
        }?.also {
            UIBlock.drawBlock(
                Color(129, 212, 250, 100),
                it.getLeft().toDouble(),
                it.getTop().toDouble(),
                it.getRight().toDouble(),
                it.getBottom().toDouble()
            )
        }

        super.draw()
    }

    class InspectorArrow(private val empty: Boolean) : TreeArrowComponent() {
        private val closedIcon = SVGComponent.ofResource("/svg/square-plus.svg").constrain {
            width = 10.pixels()
            height = 10.pixels()
        }
        private val openIcon = SVGComponent.ofResource("/svg/square-minus.svg").constrain {
            width = 10.pixels()
            height = 10.pixels()
        }

        init {
            constrain {
                width = 10.pixels()
                height = 10.pixels()
            }

            if (!empty)
                closedIcon childOf this
        }

        override fun open() {
            if (!empty)
                replaceChild(openIcon, closedIcon)
        }

        override fun close() {
            if (!empty)
                replaceChild(closedIcon, openIcon)
        }
    }

    inner class InspectorNode(val targetComponent: UIComponent) : TreeNode() {
        override var arrowComponent: () -> TreeArrowComponent = { InspectorArrow(targetComponent.children.isEmpty()) }

        override fun toComponent(): UIComponent {
            val componentName = targetComponent.javaClass.simpleName
            var wasHidden = false

            return object : UIContainer() {
                private val text = UIText(componentName).constrain {
                    x = 5.pixels()
                    width = TextAspectConstraint()
                } childOf this

                override fun animationFrame() {
                    super.animationFrame()

                    val isCurrentlyHidden = targetComponent.parent != targetComponent && !targetComponent.parent.children.contains(
                        targetComponent
                    )
                    if (isCurrentlyHidden && !wasHidden) {
                        wasHidden = true
                        text.setText("§r$componentName §7§o(Hidden)")
                    } else if (!isCurrentlyHidden && wasHidden) {
                        wasHidden = false
                        text.setText(componentName)
                    }
                }
            }.constrain {
                x = SiblingConstraint()
                y = 2.pixels()
                width = ChildBasedSizeConstraint() + 5.pixels()
                height = ChildBasedSizeConstraint()
            }.onMouseClick { event ->
                event.stopImmediatePropagation()

                setSelectedComponent(if (selectedComponent == targetComponent) {
                    null
                } else targetComponent)
            }
        }
    }

    inner class InfoBlock : UIContainer() {
        private var cachedComponent: UIComponent? = null
        private val constraintsTree = TreeView().constrain {
            x = 5.pixels()
            y = 5.pixels()
        } childOf this

        private fun getConstraintNodes(component: UIComponent): List<TreeNode> {
            val constraints = component.getConstraints()
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

            return nodes
        }

        private fun getNodeFromConstraint(constraint: SuperConstraint<*>, name: String? = null): TreeNode {
            if (!constraintHasChildren(constraint))
                return InfoNode(constraint, name)

            return when (constraint) {
                is AdditiveConstraint -> InfoNode(constraint, name).withChildren {
                    add(getNodeFromConstraint(constraint.constraint1))
                    add(getNodeFromConstraint(constraint.constraint2))
                }
                is MaxConstraint -> InfoNode(constraint, name).withChildren {
                    add(getNodeFromConstraint(constraint.constraint))
                    add(getNodeFromConstraint(constraint.maxConstraint))
                }
                is MinConstraint -> InfoNode(constraint, name).withChildren {
                    add(getNodeFromConstraint(constraint.constraint))
                    add(getNodeFromConstraint(constraint.minConstraint))
                }
                is SubtractiveConstraint -> InfoNode(constraint, name).withChildren {
                    add(getNodeFromConstraint(constraint.constraint1))
                    add(getNodeFromConstraint(constraint.constraint2))
                }
                else -> throw IllegalStateException()
            }
        }

        override fun draw() {
            super.draw()

            if (cachedComponent != selectedComponent) {
                cachedComponent = selectedComponent
                if (cachedComponent != null)
                    constraintsTree.setRoots(getConstraintNodes(cachedComponent!!))
            }
        }

        private fun constraintHasChildren(constraint: SuperConstraint<*>) = when (constraint) {
            is AdditiveConstraint,
            is MaxConstraint,
            is MinConstraint,
            is SubtractiveConstraint-> true
            else -> false
        }

        inner class InfoNode<T>(private val constraint: SuperConstraint<T>, private val name: String? = null) : TreeNode() {
            override var arrowComponent: () -> TreeArrowComponent = {
                InspectorArrow(!constraintHasChildren(constraint))
            }

            override fun toComponent() = object : UIContainer() {
                init {
                    val name = constraint.javaClass.simpleName.let {
                        if (name == null) it else "$name: $it"
                    }
                    UIText(name).constrain {
                        x = 5.pixels()
                    } childOf this

                    val strings = when (constraint) {
                        is AlphaAspectColorConstraint -> listOf(constraint::color, constraint::value)
                        is AspectConstraint -> listOf(constraint::value)
                        is ChildBasedSizeConstraint -> listOf(constraint::padding)
                        is ConstantColorConstraint -> listOf(constraint::color)
                        is CramSiblingConstraint -> listOf(constraint::padding)
                        is PixelConstraint -> listOf(constraint::value, constraint::alignOpposite, constraint::alignOutside)
                        is RainbowColorConstraint -> listOf(constraint::alpha, constraint::speed)
                        is RelativeConstraint -> listOf(constraint::value)
                        is ScaledTextConstraint -> listOf(constraint::scale)
                        is SiblingConstraint -> listOf(constraint::padding, constraint::alignOpposite)
                        else -> listOf()
                    }

                    fun toString(o: Any) = if (o is Color) "Color(${o.red}, ${o.green}, ${o.blue}, ${o.alpha})" else o.toString()

                    strings.forEach {
                        UIText("§7${it.name}: ${toString(it.get())}§r").constrain {
                            x = 13.pixels()
                            y = SiblingConstraint()
                        } childOf this
                    }
                }
            }.constrain {
                x = SiblingConstraint()
                y = 2.pixels()
                width = ChildBasedMaxSizeConstraint() + 10.pixels()
                height = ChildBasedSizeConstraint() + 5.pixels()
            }
        }
    }
}