package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.basicWidthConstraint
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.pixels
import kotlin.math.max

abstract class TreeArrowComponent : UIComponent() {
    abstract fun open()
    abstract fun close()
}

open class TreeView(roots: List<TreeNode>) : UIContainer() {
    constructor(root: TreeNode) : this(listOf(root))

    constructor() : this(emptyList())

    init {
        constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        setRoots(roots)
    }

    fun setRoots(roots: List<TreeNode>) = apply {
        clearChildren()
        roots.forEach { it.toDisplayComponent() childOf this }
    }
}

abstract class TreeNode {
    var parent: TreeNode? = null
        private set
    var indentationOffset: Float = 10f
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
            private val mappedChildren = this@TreeNode.children.map { it.toDisplayComponent() }

            init {
                constrain {
                    y = SiblingConstraint()
                    width = ChildBasedMaxSizeConstraint()
                    height = ChildBasedSizeConstraint()
                }

                val ownContent = UIContainer().constrain {
                    width = ChildBasedSizeConstraint()
                    height = ChildBasedMaxSizeConstraint()
                } childOf this

                arrowComponent childOf ownContent
                UIContainer().constrain {
                    x = SiblingConstraint()
                    width = 5.pixels()
                } childOf ownContent
                toComponent() childOf ownContent

                if (mappedChildren.isNotEmpty()) {
                    val childContainer = UIContainer().constrain {
                        x = indentationOffset.pixels()
                        y = SiblingConstraint()
                        width = ChildBasedMaxSizeConstraint()
                        height = ChildBasedSizeConstraint()
                    } childOf this

                    mappedChildren.forEach {
                        it childOf childContainer
                    }

                    mappedChildren.reversed().forEach {
                        it.hide(instantly = true)
                    }

                    constrain {
                        width = basicWidthConstraint {
                            max(ownContent.getWidth(), childContainer.getWidth() + indentationOffset)
                        }
                    }
                }

                onMouseClick { event ->
                    event.stopImmediatePropagation()

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
                }
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
