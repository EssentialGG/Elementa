package club.sk1er.elementa.features

import club.sk1er.elementa.UIComponent

interface Feature {
    fun beforeDraw(component: UIComponent)

    fun afterDraw(component: UIComponent)
}