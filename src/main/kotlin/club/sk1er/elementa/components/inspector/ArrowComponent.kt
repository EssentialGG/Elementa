package club.sk1er.elementa.components.inspector

import club.sk1er.elementa.components.SVGComponent
import club.sk1er.elementa.components.TreeArrowComponent
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels

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