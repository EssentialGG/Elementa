package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.components.inspector.InspectorNode
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalMinecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

class ConstraintResolutionGui(
    private val guiName: String,
    private val gui: UIComponent,
    private val nodes: List<ResolverNode>?
) : WindowScreen() {
    init {
        UIBlock(Color(22, 22, 24)).constrain {
            width = FillConstraint()
            height = FillConstraint()
        } childOf window

        UIBlock(Color(80, 80, 80)).constrain {
            x = RelativeConstraint(0.1f)
            y = RelativeConstraint(0.1f)
            width = 1.pixels()
            height = RelativeConstraint(0.8f)
        } childOf window

        val container = UIContainer().constrain {
            x = SiblingConstraint()
            y = RelativeConstraint(0.1f)
            width = RelativeConstraint(0.8f) - 2.pixels()
            height = RelativeConstraint(0.8f)
        } effect ScissorEffect() childOf window

        UIBlock(Color(80, 80, 80)).constrain {
            x = SiblingConstraint()
            y = RelativeConstraint(0.1f)
            width = 1.pixels()
            height = RelativeConstraint()
            height = RelativeConstraint(0.8f)
        } childOf window

        val titleContent = UIContainer().constrain {
            x = 1.pixels()
            width = RelativeConstraint() - 2.pixels()
            height = ChildBasedSizeConstraint()
        } childOf container

        val titleText = UIText("Cyclic Constraint Tree Detected").constrain {
            x = CenterConstraint()
            textScale = 2.pixels()
            color = Color(239, 83, 80).asConstraint()
        } childOf titleContent

        val tabContainer = UIContainer().constrain {
            y = SiblingConstraint(10f).to(titleContent) as YConstraint
            width = RelativeConstraint()
            height = ChildBasedSizeConstraint()
        } childOf container

        val tabContent = UIContainer().constrain {
            x = CenterConstraint()
            width = RelativeConstraint(0.5f)
            height = ChildBasedMaxSizeConstraint() + 2.pixels()
        } childOf tabContainer

        val tabHighlight = UIBlock(Color(200, 200, 200)).constrain {
            y = (-2).pixels(alignOpposite = true)
            width = RelativeConstraint(0.5f).to(tabContent) as WidthConstraint
            height = 2.pixels()
        } childOf tabContent

        val inactiveText = Color(187, 187, 187).asConstraint()
        val activeText = Color.WHITE.asConstraint()

        val listTab = UIContainer().constrain {
            width = RelativeConstraint(0.5f)
            height = ChildBasedSizeConstraint() + 10.pixels()
        } childOf tabContent

        val listTabText = UIText("Bad Constraint Path").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = activeText
        } childOf listTab

        val treeTab = UIContainer().constrain {
            x = SiblingConstraint()
            width = RelativeConstraint(0.5f)
            height = ChildBasedSizeConstraint() + 10.pixels()
        } childOf tabContent

        val treeTabText = UIText("Component Hierarchy").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = inactiveText
        } childOf treeTab

        val displayContent = UIContainer().constrain {
            x = 1.pixels()
            y = SiblingConstraint(30f)
            width = RelativeConstraint() - 2.pixels()
            height = FillConstraint()
        } childOf container

        val listView = ListView().constrain {
            x = CenterConstraint()
        } childOf displayContent

        val treeView = TreeView(gui).constrain {
            width = RelativeConstraint()
            height = RelativeConstraint()
        }

        var listSelected = true

        listTab.onMouseEnter {
            if (listSelected)
                return@onMouseEnter

            listTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, activeText)
            }
        }.onMouseLeave {
            if (listView.hasParent)
                return@onMouseLeave

            listTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
        }.onMouseClick {
            if (listSelected)
                return@onMouseClick

            listSelected = true

            displayContent.replaceChild(listView, treeView)
            treeTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
            tabHighlight.animate {
                setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
            }
        }

        treeTab.onMouseEnter {
            if (!listSelected)
                return@onMouseEnter

            treeTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, activeText)
            }
        }.onMouseLeave {
            if (!listSelected)
                return@onMouseLeave

            treeTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
        }.onMouseClick {
            if (!listSelected)
                return@onMouseClick

            listSelected = false

            displayContent.replaceChild(treeView, listView)
            listTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
            tabHighlight.animate {
                setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(alignOpposite = true))
            }
        }

        UIBlock(Color(80, 80, 80)).constrain {
            x = (-1).pixels(alignOpposite = true)
            width = 1.pixels()
            height = RelativeConstraint()
        } childOf container
    }

    private inner class ListView : UIContainer() {
        init {
            constrain {
                width = ChildBasedMaxSizeConstraint()
                height = FillConstraint()
            }

            UIText("Open Screen name: $guiName").constrain {
                y = SiblingConstraint(15f)
                textScale = 1.25f.pixels()
            } childOf this

            if (nodes != null) {
                UIText("Cyclic constraints:").constrain {
                    y = SiblingConstraint(15f)
                    textScale = 1.25f.pixels()
                } childOf this

                ConstraintPathComponent().constrain {
                    y = SiblingConstraint(10f)
                    height = FillConstraint()
                } childOf this
            } else {
                UIWrappedText("Unfortunately Elementa is unable to determine the constraints responsible. This is most likely due to the use of basicConstraints.").constrain {
                    width = RelativeConstraint()
                    color = Color(239, 83, 80).asConstraint()
                } childOf this
            }
        }
    }

    private inner class ConstraintPathComponent : UIContainer() {
        init {
            constrain {
                width = ChildBasedSizeConstraint()
            }

            val scrollComponent = ScrollComponent() childOf this

            val pathItems = nodes!!.indices.map { index ->
                ConstraintPathItem(index).constrain {
                    y = SiblingConstraint(15f)
                } childOf scrollComponent
            }

            scrollComponent.constrain {
                width = basicWidthConstraint {
                    pathItems.map { it.getWidth() }.max()!!
                }
                height = RelativeConstraint()
            }
        }
    }

    private inner class ConstraintPathItem(index: Int) : UIContainer() {
        private val node = nodes!![index]

        init {
            constrain {
                height = ChildBasedMaxSizeConstraint()
                width = ChildBasedSizeConstraint()
            }

            val right = UIContainer().constrain {
                x = SiblingConstraint(10f)
                width = ChildBasedMaxSizeConstraint()
                height = ChildBasedSizeConstraint() + 4.pixels()
            }

            val left = UIContainer().constrain {
                width = ChildBasedSizeConstraint()
                height = basicHeightConstraint { right.getHeight() }
            } childOf this

            UIText("${index + 1}. ").constrain {
                textScale = 1.25f.pixels()
            } childOf left

            right childOf this

            if (index != nodes!!.lastIndex) {
                UIText("§7Component: §r${node.component.componentName}@${Integer.toHexString(node.component.hashCode())}").constrain {
                    y = SiblingConstraint()
                } childOf right

                UIText("§7Constraint: §r${node.constraint.javaClass.simpleName}").constrain {
                    y = SiblingConstraint(2f)
                } childOf right

                UIText("§7Constraint Type: §r${node.constraintType.prettyName}").constrain {
                    y = SiblingConstraint(2f)
                } childOf right
            } else {
                UIText("§7The first entry in this list") childOf right
            }
        }
    }

    private inner class TreeView(rootComponent: UIComponent) : UIContainer() {
        init {
            val rootNode = componentToNode(rootComponent)

            TreeGraphComponent(rootNode, TreeGraphStyle().copy(heightBetweenRows = 20f)).constrain {
                x = RelativeConstraint(0.05f)
                width = RelativeConstraint(0.9f)
                height = RelativeConstraint()
            } childOf this
        }

        private fun componentToNode(component: UIComponent): TreeGraphNode {
            return ConstraintTreeNode(component).withChildren {
                component.children.forEach {
                    add(componentToNode(it))
                }
            }
        }
    }

    private inner class ConstraintTreeNode(private val target: UIComponent) : TreeGraphNode() {
        override fun makeComponent(): UIComponent {
            val block = UIBlock(Color(50, 50, 50)).constrain {
                width = ChildBasedSizeConstraint() + 3.pixels()
                height = ChildBasedSizeConstraint() + 3.pixels()
            } effect OutlineEffect(Color(100, 100, 100), 1f)

            val hasError = nodes?.any { it.component == target } ?: false
            val colorCode = if (hasError) "§c" else ""

            UIText("$colorCode${target.componentName}@${Integer.toHexString(target.hashCode())}").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                textScale = 0.5f.pixels()
            } childOf block

            return block
        }
    }
}