package club.sk1er.elementa.events

import club.sk1er.elementa.UIComponent

data class UIClickEvent(
    val absoluteX: Float,
    val absoluteY: Float,
    val mouseButton: Int,
    val target: UIComponent,
    val currentTarget: UIComponent,
    val clickCount: Int
) : UIEvent() {
    val relativeX = absoluteX - currentTarget.getLeft()
    val relativeY = absoluteY - currentTarget.getTop()
}

data class UIScrollEvent(
    val delta: Double,
    val target: UIComponent,
    val currentTarget: UIComponent
) : UIEvent()