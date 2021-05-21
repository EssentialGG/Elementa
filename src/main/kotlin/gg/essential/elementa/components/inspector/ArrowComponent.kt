package gg.essential.elementa.components.inspector

import gg.essential.elementa.components.SVGComponent
import gg.essential.elementa.components.TreeArrowComponent
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels

class ArrowComponent(private val empty: Boolean) : TreeArrowComponent() {
    private val closedIcon = SVGComponent.ofResource("/svg/square-plus.svg").constrain {
        width = 10.pixels()
        height = 10.pixels()
    }
    private val openIcon = SVGComponent.ofResource("/svg/square-minus.svg").constrain {
        width = 10.pixels()
        height = 10.pixels()
    }

    init {
        constrain {
            width = 10.pixels()
            height = 10.pixels()
        }

        if (!empty)
            closedIcon childOf this
    }

    override fun open() {
        if (!empty)
            replaceChild(openIcon, closedIcon)
    }

    override fun close() {
        if (!empty)
            replaceChild(closedIcon, openIcon)
    }
}