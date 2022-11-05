package gg.essential.elementa.components.inspector

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.tabs.ConstraintsTab
import gg.essential.elementa.components.inspector.tabs.InspectorTab
import gg.essential.elementa.components.inspector.tabs.StatesTab
import gg.essential.elementa.components.inspector.tabs.ValuesTab
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.toConstraint
import gg.essential.elementa.utils.hoveredState
import gg.essential.elementa.utils.onLeftClick
import gg.essential.elementa.utils.onSetValueAndNow
import gg.essential.elementa.utils.or
import gg.essential.universal.UMatrixStack
import gg.essential.universal.USound
import org.jetbrains.annotations.ApiStatus
import java.awt.Color

class InfoBlock(private val inspector: Inspector) : UIContainer() {
    private var cachedComponent: UIComponent? = null

    private val tabContainer by UIContainer().constrain {
        width = ChildBasedSizeConstraint() + 15.pixels
        height = ChildBasedMaxSizeConstraint() + 10.pixels
    } childOf this

    private val contentContainer by UIContainer().constrain {
        y = SiblingConstraint()
        width = ChildBasedSizeConstraint() + 10.pixels
        height = ChildBasedSizeConstraint()
    } childOf this

    private val constraintsTab = ConstraintsTab()
    private val selectedTab: State<InspectorTab> = BasicState(constraintsTab)

    private val tabs = listOf(constraintsTab, ValuesTab(), StatesTab())

    init {
        tabs.forEach { tab ->
            UIText(tab.name).constrain {
                y = CenterConstraint()
                x = SiblingConstraint(5f)
            }.apply {
                setColor((hoveredState() or selectedTab.map { it == tab }).map {
                    if (it) {
                        Color.WHITE
                    } else {
                        Color(255, 255, 255, 102)
                    }
                }.toConstraint())
                onLeftClick {
                    USound.playButtonPress()
                    selectedTab.set(tab)
                }
            } childOf tabContainer
        }

        selectedTab.onSetValueAndNow {
            contentContainer.clearChildren()
            it childOf contentContainer
        }

    }


    override fun draw(matrixStack: UMatrixStack) {
        super.draw(matrixStack)

        var cachedComponent = cachedComponent
        if (cachedComponent != inspector.selectedNode?.targetComponent) {
            cachedComponent = inspector.selectedNode?.targetComponent.also {
                this.cachedComponent = it
            }
            if (cachedComponent != null) {
                tabs.forEach {
                    it.newComponent(cachedComponent)
                }
            }
        }
        if (cachedComponent != null) {
            tabs.forEach {
                it.updateValues()
            }
        }

    }

    @ApiStatus.Internal
    fun openConstraintsTab() {
        selectedTab.set(tabs[0])
    }

    @ApiStatus.Internal
    fun openValuesTab() {
        selectedTab.set(tabs[1])
    }

    @ApiStatus.Internal
    fun openStatesTab() {
        selectedTab.set(tabs[2])
    }
}