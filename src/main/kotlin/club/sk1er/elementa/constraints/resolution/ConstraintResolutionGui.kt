package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.OutlineEffect
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.mods.core.universal.UniversalGraphicsHandler
import club.sk1er.mods.core.universal.UniversalMinecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

class ConstraintResolutionGui(private val nodes: List<ResolverNode>) : WindowScreen() {
    init {
        UIBlock(Color(22, 22, 24)).constrain {
            width = FillConstraint()
            height = FillConstraint()
        } childOf window

        val container = UIContainer().constrain {
            x = RelativeConstraint(0.1f)
            y = RelativeConstraint(0.1f)
            width = RelativeConstraint(0.8f)
            height = RelativeConstraint(0.8f)
        } effect ScissorEffect() childOf window

        UIBlock(Color(80, 80, 80)).constrain {
            width = 1.pixels()
            height = RelativeConstraint()
        } childOf container

        val contentParent = UIContainer().constrain {
            x = SiblingConstraint()
            width = RelativeConstraint() - 4.pixels()
            height = RelativeConstraint()
        } childOf container

        UIBlock(Color(80, 80, 80)).constrain {
            x = SiblingConstraint()
            width = 1.pixels()
            height = RelativeConstraint()
        } childOf container

        val content = UIContainer().constrain {
            x = RelativeConstraint(0.1f)
            width = RelativeConstraint(0.8f)
            height = RelativeConstraint()
        } childOf contentParent

        UIText("Cyclic Constraint Tree Detected").constrain {
            textScale = 2.pixels()
            color = Color(239, 83, 80).asConstraint()
        } childOf content

        val guiName = UniversalMinecraft.getMinecraft().currentScreen?.javaClass?.simpleName ?: "<unknown>"

        UIText("Open Screen name: $guiName").constrain {
            y = SiblingConstraint(15f)
            textScale = 1.25f.pixels()
        } childOf content

        UIText("Cyclic constraints:").constrain {
            y = SiblingConstraint(15f)
            textScale = 1.25f.pixels()
        } childOf content

        ConstraintPathComponent().constrain {
            y = SiblingConstraint(10f)
            width = RelativeConstraint()
            height = FillConstraint()
        } childOf content
    }

    private inner class ConstraintPathComponent : UIContainer() {
        init {
            val scrollComponent = ScrollComponent().constrain {
                width = RelativeConstraint()
                height = RelativeConstraint()
            } childOf this

            nodes.indices.forEach { index ->
                ConstraintPathItem(index).constrain {
                    y = SiblingConstraint(15f)
                } childOf scrollComponent
            }
        }
    }

    private inner class ConstraintPathItem(index: Int) : UIContainer() {
        private val node = nodes[index]

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

            if (index != nodes.lastIndex) {
                UIText("§7Component: §r${node.component.componentName}").constrain {
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