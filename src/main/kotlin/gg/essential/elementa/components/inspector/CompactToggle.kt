package gg.essential.elementa.components.inspector

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.AspectConstraint
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.onLeftClick
import gg.essential.elementa.utils.onSetValueAndNow
import gg.essential.universal.USound
import java.awt.Color

internal class CompactToggle(
    private val enabled: State<Boolean>,
) : UIBlock() {

    private val switchBox by UIBlock().constrain {
        width = AspectConstraint()
        height = 100.percent
        color = notchColor.toConstraint()
        y = CenterConstraint()
    } childOf this

    init {
        onLeftClick {
            USound.playButtonPress()
            enabled.set { !it }
        }

        constrain {
            width = AspectConstraint(2f)
            height = 7.pixels
        }

        setColor(backgroundColor.toConstraint())

        enabled.onSetValueAndNow {
            val xConstraint = 0.pixels(alignOpposite = it)
            // Null during init
            if (Window.ofOrNull(this@CompactToggle) != null) {
                switchBox.animate {
                    setXAnimation(Animations.OUT_EXP, 0.25f, xConstraint)
                }
            } else {
                switchBox.setX(xConstraint)
            }
        }
    }
    companion object {

        internal val backgroundColor = Color(0xBBBBBB)
        internal val notchColor = Color(0x2BC553)
    }
}