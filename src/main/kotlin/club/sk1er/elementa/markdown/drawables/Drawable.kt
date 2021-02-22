package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.MarkdownConfig
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

abstract class Drawable(val config: MarkdownConfig) {
    lateinit var layout: Layout

    var x by lazy { layout::x }
    var y by lazy { layout::y }
    var width by lazy { layout::width }
    var height by lazy { layout::height }
    var margin by lazy { layout::margin }

    // Used to disable top and bottom padding in some elements
    var insertSpaceBefore = true
    var insertSpaceAfter = true

    var previous: Drawable? = null
    var next: Drawable? = null

    fun layout(x: Float, y: Float, width: Float): Layout {
        return layoutImpl(x, y, width).also {
            layout = it
        }
    }

    abstract fun layoutImpl(x: Float, y: Float, width: Float): Layout

    abstract fun draw()

    fun isHovered(mouseX: Float, mouseY: Float): Boolean {
        return mouseX in layout.left..layout.right && mouseY in layout.top..layout.bottom
    }

    fun isElementHovered(mouseX: Float, mouseY: Float): Boolean {
        return mouseX in layout.elementLeft..layout.elementRight && mouseY in layout.elementTop..layout.elementBottom
    }

    data class Layout(
        var x: Float,
        var y: Float,
        var width: Float,
        var height: Float,
        var margin: Margin = Margin()
    ) {
        val elementWidth get() = width - margin.left - margin.right
        val elementHeight get() = height - margin.top - margin.bottom

        val top get() = y
        val bottom get() = y + height
        val elementTop get() = y + margin.top
        val elementBottom get() = y + margin.top + height

        val left get() = x
        val right get() = x + width
        val elementLeft get() = x + margin.left
        val elementRight get() = x + margin.left + width
    }

    data class Margin(
        var left: Float = 0f,
        var top: Float = 0f,
        var right: Float = 0f,
        var bottom: Float = 0f
    )

    companion object {
        // To resolve the ambiguity
        fun trim(drawableList: DrawableList) {
            trim(drawableList as List<Drawable>)
        }

        fun trim(drawables: List<Drawable>) {
            drawables.firstOrNull()?.also {
                it.insertSpaceBefore = false
            }
            drawables.lastOrNull()?.also {
                it.insertSpaceAfter = false
            }
        }

        fun trim(drawable: Drawable) {
            if (drawable is DrawableList) {
                trim(drawable.drawables)
            } else {
                drawable.insertSpaceBefore = false
                drawable.insertSpaceAfter = false
            }
        }
    }
}

fun <R> lazy(property: () -> KMutableProperty0<R>): LazyPropertyDelegate<R> {
    return LazyPropertyDelegate(property)
}

class LazyPropertyDelegate<R>(private val provider: () -> KMutableProperty0<R>) {
    private var propertyBacker: KMutableProperty0<R>? = null

    private fun property(): KMutableProperty0<R> {
        if (propertyBacker == null)
            propertyBacker = provider()
        return propertyBacker!!
    }

    operator fun getValue(d: Drawable, p: KProperty<*>): R {
        return property().get()
    }

    operator fun setValue(d: Drawable, p: KProperty<*>, value: R) {
        property().set(value)
    }
}

