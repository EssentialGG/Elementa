package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.components.inspector.InspectorNode
import club.sk1er.elementa.constraints.*
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

        val container = UIContainer().constrain {
            x = RelativeConstraint(0.1f)
            y = RelativeConstraint(0.1f)
            width = RelativeConstraint(0.8f) - 2.pixels()
            height = RelativeConstraint(0.8f)
        } effect ScissorEffect() childOf window

        UIBlock(Color(80, 80, 80)).constrain {
            width = 1.pixels()
            height = RelativeConstraint()
        } childOf container

        val titleContent = UIContainer().constrain {
            x = 1.pixels()
            width = RelativeConstraint() - 2.pixels()
            height = RelativeConstraint(0.1f)
        } childOf container

        UIText("Cyclic Constraint Tree Detected").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 2.pixels()
            color = Color(239, 83, 80).asConstraint()
        } childOf titleContent

        val displayContent = UIContainer().constrain {
            x = 1.pixels()
            y = SiblingConstraint().to(titleContent) + RelativeConstraint(0.05f) as YConstraint
            width = RelativeConstraint() - 2.pixels()
            height = FillConstraint()
        } childOf container

        ListView().constrain {
            x = RelativeConstraint(0.05f)
            width = RelativeConstraint(0.4f)
            height = RelativeConstraint()
        } childOf displayContent

        UIBlock(Color(80, 80, 80)).constrain {
            x = RelativeConstraint(0.5f)
            width = 1.pixels()
            height = RelativeConstraint()
        } childOf displayContent

        TreeView(gui).constrain {
            x = RelativeConstraint(0.55f)
            width = RelativeConstraint(0.4f)
            height = RelativeConstraint()
        } childOf displayContent

        UIBlock(Color(80, 80, 80)).constrain {
            x = (-1).pixels(alignOpposite = true)
            width = 1.pixels()
            height = RelativeConstraint()
        } childOf container

        UIBlock(Color(80, 80, 80)).constrain {
            x = (-1).pixels(alignOpposite = true)
            width = 1.pixels()
            height = RelativeConstraint()
        } childOf container

        Inspector(window) childOf window
    }

    private inner class ListView : UIContainer() {
        init {
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
                    width = RelativeConstraint()
                    height = FillConstraint()
                } childOf this
            } else {
                UIWrappedText("Unfortunately Elementa is unable to determine the constraints responsible. This is most likely due to the use of basicConstraints. ").constrain {
                    width = RelativeConstraint()
                    color = Color(239, 83, 80).asConstraint()
                } childOf this
            }
        }
    }

    private inner class TreeView(rootComponent: UIComponent) : UIContainer() {
        init {
            val rootNode = componentToNode(rootComponent)

            TreeGraphComponent(rootNode, TreeGraphStyle().copy(heightBetweenRows = 20f)).constrain {
                width = RelativeConstraint()
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

    private class ConstraintTreeNode(private val target: UIComponent) : TreeGraphNode() {
        override fun makeComponent(): UIComponent {
            val block = UIBlock(Color(50, 50, 50)).constrain {
                width = ChildBasedSizeConstraint() + 3.pixels()
                height = ChildBasedSizeConstraint() + 3.pixels()
            } effect OutlineEffect(Color(100, 100, 100), 1f)

            UIText("${target.componentName}@${Integer.toHexString(target.hashCode())}").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                textScale = 0.5f.pixels()
            } childOf block

            return block
        }
    }

    private inner class ConstraintPathComponent : UIContainer() {
        init {
            val scrollComponent = ScrollComponent().constrain {
                width = RelativeConstraint()
                height = RelativeConstraint()
            } childOf this

            nodes!!.indices.forEach { index ->
                ConstraintPathItem(index).constrain {
                    y = SiblingConstraint(15f)
                } childOf scrollComponent
            }
        }
    }

    private inner class ConstraintPathItem(index: Int) : UIContainer() {
        private val node = nodes!![index]

        init {
            constrain {
                height = ChildBasedMaxSizeConstraint()
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
}