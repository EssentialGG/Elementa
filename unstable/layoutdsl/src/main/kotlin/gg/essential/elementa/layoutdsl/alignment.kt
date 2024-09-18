package gg.essential.elementa.layoutdsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.PositionConstraint
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.common.constraints.CenterPixelConstraint

interface Alignment {
    fun applyHorizontal(component: UIComponent): () -> Unit
    fun applyVertical(component: UIComponent): () -> Unit

    companion object {
        @Suppress("FunctionName")
        fun Start(padding: Float): Alignment = BasicAlignment { padding.pixels() }
        @Suppress("FunctionName")
        fun Center(roundUp: Boolean): Alignment = BasicAlignment { CenterPixelConstraint(roundUp) }
        @Suppress("FunctionName")
        fun End(padding: Float): Alignment = BasicAlignment { padding.pixels(alignOpposite = true) }

        val Start: Alignment = Start(0f)
        val Center: Alignment = BasicAlignment { CenterPixelConstraint() }
        val End: Alignment = End(0f)

        val TrueCenter: Alignment = BasicAlignment { CenterConstraint() }
    }
}

private class BasicAlignment(private val constraintFactory: () -> PositionConstraint) : Alignment {
    override fun applyHorizontal(component: UIComponent): () -> Unit {
        return BasicXModifier(constraintFactory).applyToComponent(component)
    }

    override fun applyVertical(component: UIComponent): () -> Unit {
        return BasicYModifier(constraintFactory).applyToComponent(component)
    }
}

fun Modifier.alignBoth(alignment: Alignment) = alignHorizontal(alignment).alignVertical(alignment)

fun Modifier.alignHorizontal(alignment: Alignment) = this then HorizontalAlignmentModifier(alignment)

fun Modifier.alignVertical(alignment: Alignment) = this then VerticalAlignmentModifier(alignment)

private class HorizontalAlignmentModifier(private val alignment: Alignment) : Modifier {
    override fun applyToComponent(component: UIComponent): () -> Unit {
        return alignment.applyHorizontal(component)
    }
}

private class VerticalAlignmentModifier(private val alignment: Alignment) : Modifier {
    override fun applyToComponent(component: UIComponent): () -> Unit {
        return alignment.applyVertical(component)
    }
}
