package club.sk1er.elementa.components.inspector

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.TreeNode
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.ChildBasedSizeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.TextAspectConstraint
import club.sk1er.elementa.dsl.toConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels
import java.awt.Color

class InspectorNode(private val inspector: Inspector, val targetComponent: UIComponent) : TreeNode() {
    private val componentClassName = targetComponent.javaClass.simpleName.ifEmpty { "UnknownType" }
    private val componentDisplayName = targetComponent.componentName.let { if (it == componentClassName) it else "$componentClassName: $it" }
    private var wasHidden = false

    private val component: UIComponent = object : UIBlock(Color(0, 0, 0, 0)) {
        private val text = UIText(componentDisplayName).constrain {
            width = TextAspectConstraint()
        } childOf this

        override fun animationFrame() {
            super.animationFrame()

            val isCurrentlyHidden = targetComponent.parent != targetComponent && !targetComponent.parent.children.contains(
                targetComponent
            )
            if (isCurrentlyHidden && !wasHidden) {
                wasHidden = true
                text.setText("§r$componentDisplayName §7§o(Hidden)")
            } else if (!isCurrentlyHidden && wasHidden) {
                wasHidden = false
                text.setText(componentDisplayName)
            }
        }
    }.constrain {
        x = SiblingConstraint()
        y = 2.pixels()
        width = ChildBasedSizeConstraint()
        height = ChildBasedSizeConstraint()
    }.onMouseClick { event ->
        event.stopImmediatePropagation()

        inspector.selectedNode?.component?.setColor(Color(0, 0, 0, 0).toConstraint())

        if (inspector.selectedNode == this@InspectorNode) {
            inspector.setSelectedNode(null)
        } else {
            inspector.setSelectedNode(this@InspectorNode)
            setColor(Color(32, 78, 138).toConstraint())
        }
    }

    override fun getArrowComponent() = ArrowComponent(targetComponent.children.isEmpty())

    override fun getPrimaryComponent() = component
}
