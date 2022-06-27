package gg.essential.elementa.components.inspector

import gg.essential.elementa.components.SVGComponent
import gg.essential.elementa.components.TreeArrowComponent
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.svg.SVGParser
import gg.essential.universal.UMatrixStack

class ArrowComponent(private val empty: Boolean) : TreeArrowComponent() {
    private val closedIcon = SVGComponent(closedSvg).constrain {
        width = 10.pixels
        height = 10.pixels
    }
    private val openIcon = SVGComponent(openSvg).constrain {
        width = 10.pixels
        height = 10.pixels
    }

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        if (!empty) {
            closedIcon childOf this
        }
    }

    override fun open() {
        if (!empty) {
            replaceChild(openIcon, closedIcon)
        }
    }

    override fun close() {
        if (!empty) {
            replaceChild(closedIcon, openIcon)
        }
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDraw(matrixStack)
        super.draw(matrixStack)
    }

    companion object {

        private val closedSvg = SVGParser.parseFromResource("/svg/square-plus.svg")
        private val openSvg = SVGParser.parseFromResource("/svg/square-minus.svg")

    }
}