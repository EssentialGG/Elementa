package club.sk1er.elementa

import scala.tools.nsc.doc.model.Constraint
import java.util.*

class UIElement {

    private var parent: UIElement? = null
    private var children: ArrayList<UIElement> = ArrayList()



    constructor(vararg components: Any) {
        components.forEach {
            when(it) {
                is Constraint -> constraints.add(it)
                is UIElement -> this.addChild(it)
            }
        }
    }

    fun setParent(parent: UIElement) {
        this.parent = parent
        this.parent!!.addChild(this)
    }

    fun addChild(child: UIElement) {
        this.children.add(child)
    }

    @JvmOverloads
    fun draw() {
        if (parent == null) {
            GuiLib.drawRect(this.x, this.y, this.width, this.height, this.color)
        } else {
            GuiLib.drawRect(this.parent!!.x + this.x, this.parent!!.y + this.y, this.width, this.height, this.color)
        }
        this.children.forEach { it.draw() }
    }
}