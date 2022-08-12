package gg.essential.elementa.components.inspector

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.TreeNode
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.tabs.StatesTab
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.AnimationComponent
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.dsl.*
import java.awt.Color

class InfoBlockNode<T>(private val constraint: SuperConstraint<T>, private val name: String? = null) : TreeNode() {
    override fun getArrowComponent() = ArrowComponent(!constraintHasChildren(constraint))

    override fun getPrimaryComponent() = object : UIContainer() {
        val stringHolder: UIComponent

        init {
            val name = constraint.javaClass.simpleName.let {
                if (name == null) it else "$name: $it"
            }
            UIText(name).constrain {
                x = SiblingConstraint()
            } childOf this

            val states = if (constraint is StateRegistry) {
                constraint.getManagedStates()
            } else {
                emptyList()
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
                val percentComplete =
                    constraint.elapsedFrames.toFloat() / (constraint.totalFrames + constraint.delayFrames)
                createStringComponent("§7Completion Percentage: ${Inspector.percentFormat.format(percentComplete)}§r")
                createStringComponent("§7Paused: ${constraint.animationPaused}§r")
            }

            states.forEach { managedState ->
                StatesTab.createStateViewer(managedState, stringHolder)
            }
        }

        fun createStringComponent(text: String) {
            UIText(text).constrain {
                y = SiblingConstraint()
                color = Color(0xAAAAAA).toConstraint()
            } childOf stringHolder
        }

        override fun animationFrame() {
            super.animationFrame()

            if (constraint is AnimationComponent<*>) {
                val strings = stringHolder.childrenOfType<UIText>()
                val percentComplete = constraint.elapsedFrames.toFloat() / (constraint.totalFrames + constraint.delayFrames)
                strings[1].setText("§7Completion Percentage: ${Inspector.percentFormat.format(percentComplete)}§r")
                strings[2].setText("§7Paused: ${constraint.animationPaused}§r")
            }
        }
    }.constrain {
        x = SiblingConstraint()
        y = 2.pixels()
        width = ChildBasedMaxSizeConstraint() + 10.pixels()
        height = ChildBasedSizeConstraint() + 5.pixels()
    }

    companion object {
        private fun constraintHasChildren(constraint: SuperConstraint<*>) = when (constraint) {
            is AdditiveConstraint,
            is CoerceAtMostConstraint,
            is CoerceAtLeastConstraint,
            is SubtractiveConstraint -> true
            is AnimationComponent<*> -> true
            else -> false
        }
    }
}