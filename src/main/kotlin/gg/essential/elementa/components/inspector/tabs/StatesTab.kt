package gg.essential.elementa.components.inspector.tabs

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.state.MappedTextInput
import gg.essential.elementa.constraints.*
import gg.essential.elementa.debug.ManagedState
import gg.essential.elementa.debug.StateRegistry
import gg.essential.elementa.debug.StateRegistryComponentFactory
import gg.essential.elementa.dsl.*
import org.jetbrains.annotations.ApiStatus
import java.awt.Color

@ApiStatus.Internal
class StatesTab : InspectorTab("States") {

    override fun updateWithComponent(component: UIComponent) {
        clearChildren()
        if (component is StateRegistry) {
            val managedStates = component.managedStates
            managedStates.forEach {
                createStateViewer(it, this)
            }
        }
    }

    override fun updateValues() {
    }

    @ApiStatus.Internal
    companion object {

        fun createStateViewer(managedState: ManagedState, parent: UIComponent) {
            val container = createContainer(managedState.name, parent)
            val component = StateRegistryComponentFactory.createInspectorComponent(managedState) childOf container

            component.constrain {
                if (component is MappedTextInput<*>) {
                    y = (-1).pixels
                }
                x = SiblingConstraint(3f)
            }

            if (managedState is ManagedState.OfColor) {
                UIBlock(managedState.state).constrain {
                    width = 7.pixels
                    height = AspectConstraint()
                    x = SiblingConstraint(3f)
                } childOf container
            }
        }

        private fun createContainer(name: String, parent: UIComponent): UIContainer {
            val container by UIContainer().constrain {
                y = SiblingConstraint()
                width = ChildBasedSizeConstraint()
                height = ChildBasedMaxSizeConstraint()
            }.addChild(
                UIText(name).constrain {
                    y = SiblingConstraint()
                    color = Color(0xAAAAAA).toConstraint()
                }
            ) childOf parent

            return container as UIContainer
        }
    }

}