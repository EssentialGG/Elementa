package gg.essential.elementa.components.inspector.tabs

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.constrain
import org.jetbrains.annotations.ApiStatus


@ApiStatus.Internal
abstract class InspectorTab(val name: String) : UIContainer() {

    protected var targetComponent: UIComponent? = null

    init {
        constrain {
            x = CenterConstraint()
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        }
    }

    fun newComponent(component: UIComponent?) {
        targetComponent = component
        if (component != null) {
            updateWithComponent(component)
        }
    }

    abstract fun updateWithComponent(component: UIComponent)

    abstract fun updateValues()
}