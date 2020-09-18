package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels

class ConstraintResolutionGui(list: List<ResolverNode>) : WindowScreen() {
    init {
        UIText("bad constraints :(").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 3.pixels()
        } childOf window
    }
}