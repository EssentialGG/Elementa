package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.mods.core.universal.UniversalMouse
import club.sk1er.mods.core.universal.UniversalResolutionUtil
import java.awt.Color

class Inspector(
    rootComponent: UIComponent,
    backgroundColor: Color = Color(40, 40, 40, 255),
    outlineColor: Color = Color(20, 20, 20, 255),
    outlineWidth: Float = 2f
) : UIContainer() {
    private val rootNode = componentToNode(rootComponent)
    private val treeView: TreeView
    private var selectedComponent: UIComponent? = null
    private var clickPos: Pair<Float, Float>? = null

    private val outlineEffect = OutlineEffect(outlineColor, outlineWidth)

    private var isClickSelecting = false

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildSizeRangeConstraint()
        }

        val container = UIBlock(backgroundColor).constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildSizeRangeConstraint()
        } effect outlineEffect childOf this

        val titleBlock = UIContainer().constrain {
            x = CenterConstraint()
            width = ChildBasedSizeConstraint() + 20.pixels()
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
            x = CenterConstraint()
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

        val treeBlock = UIBlock(backgroundColor).constrain {
            y = SiblingConstraint()
            width = 150.pixels()
            height = 200.pixels()
        } effect outlineEffect childOf container

        treeView = TreeView(rootNode).constrain {
            x = 5.pixels()
            y = SiblingConstraint() + 5.pixels()
            width = ChildSizeRangeConstraint()
            height = 200.pixels()
        } childOf treeBlock
    }

    private fun componentToNode(component: UIComponent): InspectorNode {
        return InspectorNode(component).withChildren {
            component.children.forEach {
                add(componentToNode(it))
            }
        } as InspectorNode
    }

    override fun draw() {
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

            return object : UIText(componentName) {
                override fun animationFrame() {
                    super.animationFrame()

                    val isCurrentlyHidden = targetComponent.parent != targetComponent && !targetComponent.parent.children.contains(
                        targetComponent
                    )
                    if (isCurrentlyHidden && !wasHidden) {
                        wasHidden = true
                        setText("§r$componentName §7§o(Hidden)")
                    } else if (!isCurrentlyHidden && wasHidden) {
                        wasHidden = false
                        setText(componentName)
                    }
                }

            }.constrain {
                x = SiblingConstraint() + 5.pixels()
                width = TextAspectConstraint()
            }.onMouseClick { event ->
                event.stopImmediatePropagation()

                selectedComponent = if (selectedComponent == targetComponent) {
                    null
                } else targetComponent
            }
        }
    }
}