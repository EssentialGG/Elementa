package gg.essential.elementa.components.inspector.state

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.toConstraint
import gg.essential.elementa.utils.bindParent
import gg.essential.elementa.utils.hoveredState
import gg.essential.elementa.utils.onLeftClick
import org.jetbrains.annotations.ApiStatus
import java.awt.Color

@ApiStatus.Internal
class CompactSelector<T>(
    private val options: List<T>,
    private val state: State<T>,
    private val mapper: (T) -> String,
) : UIContainer() {

    private val selectorOpen = BasicState(false)

    private val selectedOption by UIText().bindText(state.map(mapper)).onLeftClick {
        selectorOpen.set { !it }
    } childOf this

    private val optionsContainer by UIBlock(Color(40, 40, 40)).constrain {
        y = SiblingConstraint()
        width = ChildBasedMaxSizeConstraint()
        height = ChildBasedSizeConstraint()
    }.bindParent(this, selectorOpen)

    init {
        constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        }
        options.forEach { option ->
            val optionText by UIText(mapper(option)).constrain {
                y = SiblingConstraint(2f)
            } childOf optionsContainer

            optionText.setColor(
                optionText.hoveredState().map {
                    if (it) {
                        Color.WHITE
                    } else {
                        Color(0xBBBBBB)
                    }
                }.toConstraint()
            )

            optionText.onLeftClick {
                state.set(option)
                selectorOpen.set(false)
            }
        }
    }
}