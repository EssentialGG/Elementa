package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent

class UIBlock : UIComponent() {
    override fun draw() {
        val x = this.getLeft()
        val y = this.getTop()

        // etc etc

        super.draw()
    }
}