package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent

object Window : UIComponent() {
    init {
        super.parent = this
    }

    override fun getLeft(): Int {
        return 0
    }

    override fun getTop(): Int {
        return 0
    }
}