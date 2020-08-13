package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.UIConstraints
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.*
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.mods.core.universal.UniversalMouse
import club.sk1er.mods.core.universal.UniversalResolutionUtil
import java.awt.Color
import java.text.NumberFormat

class Inspector(
    rootComponent: UIComponent,
    backgroundColor: Color = Color(40, 40, 40),
    outlineColor: Color = Color(20, 20, 20),
    outlineWidth: Float = 2f
) : UIContainer() {
    private val rootNode = componentToNode(rootComponent)
    private val treeBlock: UIContainer
    private lateinit var treeView: TreeView
    private val container: UIComponent
    private var selectedNode: InspectorNode? = null
    private val infoBlockScroller: ScrollComponent
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
            width = ChildBasedSizeConstraint() + 10.pixels()
            height = ChildBasedSizeConstraint() + 10.pixels()
        }

        val rootWindow = Window.of(rootComponent)

        val treeBlockScroller = ScrollComponent().constrain {
            y = SiblingConstraint()
            width = RelativeConstraint(1f).to(treeBlock) as WidthConstraint
            height = RelativeConstraint(1f).to(treeBlock) max RelativeConstraint(1 / 3f).to(rootWindow)
        } childOf container

        treeBlock childOf treeBlockScroller

        treeView = TreeView(rootNode).constrain {
            x = 5.pixels()
            y = SiblingConstraint() + 5.pixels()
        } childOf treeBlock

        separator2 = UIBlock(outlineColor).constrain {
            y = SiblingConstraint()
            height = 2.pixels()
        }

        val infoBlock = InfoBlock().constrain {
            y = SiblingConstraint()
            width = ChildBasedMaxSizeConstraint() + 10.pixels()
            height = ChildBasedSizeConstraint() + 10.pixels()
        }

        infoBlockScroller = ScrollComponent().constrain {
            y = SiblingConstraint()
            width = RelativeConstraint(1f).to(infoBlock) as WidthConstraint
            height = RelativeConstraint(1f).to(infoBlock) max RelativeConstraint(1 / 3f).to(rootWindow)
        }

        infoBlock childOf infoBlockScroller
    }

    private fun componentToNode(component: UIComponent): InspectorNode {
        return InspectorNode(component).withChildren {
            component.children.forEach {
                add(componentToNode(it))
            }
        } as InspectorNode
    }

    private fun setSelectedNode(node: InspectorNode?) {
        if (node == null) {
            container.removeChild(separator2)
            container.removeChild(infoBlockScroller)
        } else if (selectedNode == null) {
            separator2 childOf container
            infoBlockScroller childOf container
        }
        selectedNode = node
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
            selectedNode?.targetComponent
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

        private val displayComponent: UIComponent

        init {
            val componentName = targetComponent.javaClass.simpleName.ifEmpty { "<unnamed>" }
            var wasHidden = false

            displayComponent = object : UIBlock(Color(0, 0, 0, 0)) {
                private val text = UIText(componentName).constrain {
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
                width = ChildBasedSizeConstraint()
                height = ChildBasedSizeConstraint()
            }.onMouseClick { event ->
                event.stopImmediatePropagation()

                selectedNode?.displayComponent?.setColor(Color(0, 0, 0, 0).asConstraint())

                if (selectedNode == this@InspectorNode) {
                    setSelectedNode(null)
                } else {
                    setSelectedNode(this@InspectorNode)
                    setColor(Color(32, 78, 138).asConstraint())
                }
            }
        }

        override fun toComponent(): UIComponent {
            return displayComponent
        }
    }

    inner class InfoBlock : UIContainer() {
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
            val baseInfoNode = InfoNode(constraint, name)

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

            if (cachedComponent != selectedNode?.targetComponent) {
                cachedComponent = selectedNode?.targetComponent
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

        private fun constraintHasChildren(constraint: SuperConstraint<*>) = when (constraint) {
            is AdditiveConstraint,
            is MaxConstraint,
            is MinConstraint,
            is SubtractiveConstraint -> true
            is AnimationComponent<*> -> true
            else -> false
        }

        inner class InfoNode<T>(private val constraint: SuperConstraint<T>, private val name: String? = null) :
            TreeNode() {
            override var arrowComponent: () -> TreeArrowComponent = {
                InspectorArrow(!constraintHasChildren(constraint))
            }

            override fun toComponent() = object : UIContainer() {
                val stringHolder: UIComponent

                init {
                    val name = constraint.javaClass.simpleName.let {
                        if (name == null) it else "$name: $it"
                    }
                    UIText(name).constrain {
                        x = SiblingConstraint()
                    } childOf this

                    val properties = when (constraint) {
                        is AlphaAspectColorConstraint -> listOf(constraint::color, constraint::value)
                        is AspectConstraint -> listOf(constraint::value)
                        is ChildBasedSizeConstraint -> listOf(constraint::padding)
                        is ConstantColorConstraint -> listOf(constraint::color)
                        is CramSiblingConstraint -> listOf(constraint::padding)
                        is PixelConstraint -> listOf(
                            constraint::value,
                            constraint::alignOpposite,
                            constraint::alignOutside
                        )
                        is RainbowColorConstraint -> listOf(constraint::alpha, constraint::speed)
                        is RelativeConstraint -> listOf(constraint::value)
                        is ScaledTextConstraint -> listOf(constraint::scale)
                        is SiblingConstraint -> listOf(constraint::padding, constraint::alignOpposite)
                        else -> listOf()
                    }

                    fun toString(o: Any) = when (o) {
                        is Color -> "Color(${o.red}, ${o.green}, ${o.blue}, ${o.alpha})"
                        is Double, is Float -> "%.2f".format(o)
                        else -> o.toString()
                    }

                    stringHolder = UIContainer().constrain {
                        x = 13.pixels()
                        y = SiblingConstraint()
                        width = ChildBasedMaxSizeConstraint()
                        height = ChildBasedSizeConstraint()
                    } childOf this

                    if (constraint is AnimationComponent<*>) {
                        createStringComponent("§7Strategy: ${constraint.strategy}§r")
                        val percentComplete = constraint.elapsedFrames.toFloat() / (constraint.totalFrames + constraint.delayFrames)
                        createStringComponent("§7Completion Percentage: ${percentFormat.format(percentComplete)}§r")
                        createStringComponent("§7Paused: ${constraint.animationPaused}§r")
                    }

                    properties.forEach {
                        createStringComponent("§7${it.name}: ${toString(it.get())}§r")
                    }
                }

                fun createStringComponent(text: String) {
                    UIText(text).constrain {
                        y = SiblingConstraint()
                    } childOf stringHolder
                }

                override fun animationFrame() {
                    super.animationFrame()

                    if (constraint is AnimationComponent<*>) {
                        val strings = stringHolder.childrenOfType<UIText>()
                        val percentComplete = constraint.elapsedFrames.toFloat() / (constraint.totalFrames + constraint.delayFrames)
                        strings[1].setText("§7Completion Percentage: ${percentFormat.format(percentComplete)}§r")
                        strings[2].setText("§7Paused: ${constraint.animationPaused}§r")
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

    companion object {
        private val percentFormat: NumberFormat = NumberFormat.getPercentInstance()
    }
}