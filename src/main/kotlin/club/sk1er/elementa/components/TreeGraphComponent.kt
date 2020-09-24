package club.sk1er.elementa.components

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.RelativeConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.effects.ScissorEffect
import club.sk1er.elementa.utils.LineUtils
import java.awt.Color
import kotlin.math.max
import kotlin.properties.Delegates

data class TreeGraphStyle(
    val widthBetweenNodes: Float = 10f,
    val heightBetweenRows: Float = 10f,
    val lineColor: Color = Color.WHITE,
    val lineWidth: Float = 2f,
    val lineDrawer: (UIPoint, UIPoint) -> Unit = { p0, p1 ->
        LineUtils.drawLine(p0, p1, lineColor, lineWidth)
    }
)

abstract class TreeGraphNode() {
    var depth by Delegates.notNull<Int>()
    val children = mutableListOf<TreeGraphNode>()
    val component by lazy { makeComponent() }

    private var widthBacker: Double? = null

    fun width(style: TreeGraphStyle): Double {
        if (widthBacker == null) {
            widthBacker = if (children.isEmpty()) {
                component.getWidth().toDouble()
            } else {
                max(
                    children.sumByDouble { it.width(style) } + (children.size - 1) * style.widthBetweenNodes,
                    component.getWidth().toDouble()
                )
            }
        }

        return widthBacker!!
    }

    // Must have absolutely-resolvable width and height. The position
    // will be handled by the caller
    protected abstract fun makeComponent(): UIComponent

    fun withChildren(childBuilder: TreeGraphBuilder.() -> Unit): TreeGraphNode {
        val builder = TreeGraphBuilder(this)
        builder.apply(childBuilder)
        return builder.root
    }

    fun forAllChildren(action: (TreeGraphNode, depth: Int) -> Unit) {
        children.forEach {
            action(it, it.depth)
            it.forAllChildren(action)
        }
    }

    // Should only be called on the root node component
    internal fun layoutChildren(style: TreeGraphStyle) {
        component.setX(0.pixels())
        component.setY(0.pixels())

        layoutChildrenHelper(style, 0.0, component.getHeight() + style.heightBetweenRows.toDouble())

        normalizePositions()
    }

    private fun layoutChildrenHelper(style: TreeGraphStyle, x_: Double, y: Double) {
        if (children.isEmpty())
            return

        val totalWidth = children.sumByDouble { it.width(style) } + (children.size - 1) * style.widthBetweenNodes
        val maxHeight = children.map { it.component.getHeight().toDouble() }.max()!!

        var x = x_ - totalWidth / 2.0

        children.forEach { node ->
            x += node.width(style) / 2f

            node.component.setX(x.pixels())
            node.component.setY((y + (maxHeight - node.component.getHeight()) / 2.0).pixels())


            node.layoutChildrenHelper(style, x, y + maxHeight + style.heightBetweenRows)

            x += node.width(style) / 2f + style.widthBetweenNodes
        }
    }

    private fun normalizePositions() {
        var minX = component.getLeft()
        var minY = component.getTop()

        forAllChildren { node, _ ->
            node.component.getLeft().also {
                if (it < minX)
                    minX = it
            }
            node.component.getTop().also {
                if (it < minY)
                    minY = it
            }
        }

        component.setX((component.getLeft() - minX).pixels())
        component.setY((component.getTop() - minY).pixels())

        forAllChildren { node, _ ->
            node.component.apply {
                setX((getLeft() - minX).pixels())
                setY((getTop() - minY).pixels())
            }
        }
    }

    internal fun setDepths(depth: Int = 0) {
        this.depth = depth

        children.forEach {
            it.depth = depth + 1
            it.setDepths(depth + 1)
        }
    }

    internal fun collectLines(): List<Pair<UIPoint, UIPoint>> {
        val widthModifiers = if (children.isEmpty()) emptyList() else {
            val delta = 1f / (children.size + 1)
            (1..children.size).map { it * delta }
        }.map {
            it * component.getWidth() - component.getWidth() / 2f
        }

        return children.mapIndexed { index, node ->
            val p1 = point(alignBottom = true).let {
                it.withX(it.x + widthModifiers[index].pixels())
            }

            p1 to node.point(alignBottom = false)
        } + children.map {
            it.collectLines()
        }.flatten()
    }

    private fun point(alignBottom: Boolean): UIPoint {
        val extraHeight = if (alignBottom) component.getHeight() else 0f

        return UIPoint(
            (component.getLeft() + component.getWidth() / 2f - component.parent.getLeft()).pixels(),
            (component.getTop() + extraHeight - component.parent.getTop()).pixels()
        )
    }
}

class TreeGraphComponent(
    private val rootNode: TreeGraphNode,
    private val style: TreeGraphStyle = TreeGraphStyle()
) : UIComponent() {
    private var layedOut = false

    private val scroll = ScrollComponent(horizontalScrollEnabled = true).constrain {
        width = RelativeConstraint()
        height = RelativeConstraint()
    } effect ScissorEffect() childOf this

    private lateinit var lines: List<Pair<UIPoint, UIPoint>>

    init {
        enableEffect(ScissorEffect())

        val horizontalScroll = ScrollComponent.DefaultScrollBar(isHorizontal = true) childOf this
        val verticalScroll = ScrollComponent.DefaultScrollBar(isHorizontal = false) childOf this

        scroll.setHorizontalScrollBarComponent(horizontalScroll.grip)
        scroll.setVerticalScrollBarComponent(verticalScroll.grip)

        rootNode.setDepths(0)

        val componentMatrix = mutableListOf(mutableListOf(rootNode))

        rootNode.component childOf scroll

        rootNode.forAllChildren { node, depth ->
            while (depth >= componentMatrix.size)
                componentMatrix.add(mutableListOf())
            componentMatrix[depth].add(node)

            node.component childOf scroll
        }
    }

    override fun draw() {
        if (!layedOut) {
            rootNode.layoutChildren(style)
            lines = rootNode.collectLines()
            lines.forEach { (p0, p1) ->
                scroll.insertChildAt(p0, 0)
                scroll.insertChildAt(p1, 1)
            }
            layedOut = true
        }

        lines.forEach { style.lineDrawer(it.first, it.second) }

        super.draw()
    }
}

class TreeGraphBuilder(val root: TreeGraphNode) {
    fun add(node: TreeGraphNode) {
        root.children.add(node)
    }
}
