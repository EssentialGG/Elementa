package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ChildSizeRangeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.XConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels

abstract class TreeArrowComponent : UIComponent() {
    abstract fun open()
    abstract fun close()
}

class TreeView(var root: TreeNode? = null) : UIContainer() {
    init {
        constrain {
            width = ChildSizeRangeConstraint()
            height = ChildSizeRangeConstraint()
        }

        if (root != null)
            root!!.toDisplayComponent() childOf this
    }

    fun setRoot(root: TreeNode?) = apply {
        clearChildren()
        this.root = root
        if (root != null)
            root.toDisplayComponent() childOf this
    }
}

abstract class TreeNode {
    var parent: TreeNode? = null
        private set
    var indentationOffset: XConstraint = 10.pixels()
    abstract var arrowComponent: () -> TreeArrowComponent
    val children = mutableListOf<TreeNode>()

    private var cachedDisplayComponent: UIComponent? = null
    private var refreshCache = true

    abstract fun toComponent(): UIComponent

    /**
     * Wraps the component returned from toComponent in a component that handles
     * having children. Only override this function if you really need to.
     */
    open fun toDisplayComponent(): UIComponent {
        if (!refreshCache)
            return cachedDisplayComponent!!

        cachedDisplayComponent = object : UIComponent() {
            private val arrowComponent = this@TreeNode.arrowComponent()
            private var opened = false
            private val childContainer: UIContainer
            private val mappedChildren = this@TreeNode.children.map { it.toDisplayComponent() }

            init {
                constrain {
                    y = SiblingConstraint()
                    width = ChildSizeRangeConstraint()
                    height = ChildSizeRangeConstraint()
                }

                arrowComponent childOf this
                toComponent() childOf this

                childContainer = UIContainer().constrain {
                    x = indentationOffset
                    y = SiblingConstraint()
                    width = ChildSizeRangeConstraint()
                    height = ChildSizeRangeConstraint()
                } childOf this

                mappedChildren.forEach {
                    it childOf childContainer
                }

                mappedChildren.reversed().forEach {
                    it.hide(instantly = true)
                }

                onMouseClick { event ->
                    if (opened) {
                        arrowComponent.close()
                        mappedChildren.reversed().forEach {
                            it.hide()
                        }
                    } else {
                        arrowComponent.open()
                        mappedChildren.forEach {
                            it.unhide()
                        }
                    }
                    opened = !opened
                    event.stopImmediatePropagation()
                }
            }

            override fun draw() {
                super.draw()
            }
        }

        return cachedDisplayComponent!!
    }

    fun addChild(child: TreeNode) {
        child.parent = this
        children.add(child)
        refreshCache = true
    }

    fun removeChild(child: TreeNode) {
        children.remove(child)
        refreshCache = true
    }

    fun withChildren(childBuilder: TreeNodeBuilder.() -> Unit): TreeNode {
        val builder = TreeNodeBuilder(this)
        builder.apply(childBuilder)
        return builder.root
    }
}

class TreeNodeBuilder(val root: TreeNode) {
    fun add(node: TreeNode) {
        root.addChild(node)
    }
}
