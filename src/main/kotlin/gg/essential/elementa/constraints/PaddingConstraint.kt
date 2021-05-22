package gg.essential.elementa.constraints

import gg.essential.elementa.UIComponent

interface PaddingConstraint {

    fun getVerticalPadding(component: UIComponent): Float

    fun getHorizontalPadding(component: UIComponent) : Float
}