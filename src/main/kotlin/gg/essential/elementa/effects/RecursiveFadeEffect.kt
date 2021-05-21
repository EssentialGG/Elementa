package gg.essential.elementa.effects

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.effects.Effect
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.withAlpha
import java.awt.Color
import kotlin.math.roundToInt

class RecursiveFadeEffect(
    private var isOverridden: State<Boolean> = BasicState(false),
    private var overriddenAlphaPercentage: State<Float> = BasicState(1f)
) : Effect() {
    fun rebindIsOverridden(state: State<Boolean>) = apply {
        isOverridden = state
    }

    fun rebindOverriddenAlphaPercentage(state: State<Float>) = apply {
        overriddenAlphaPercentage = state
    }

    override fun setup() {
        recurseChildren(boundComponent) {
            it.constrain {
                color = OverridableAlphaColorConstraint(color, isOverridden, overriddenAlphaPercentage)
            }
        }
    }

    fun remove() {
        recurseChildren(boundComponent) {
            if (it.constraints.color is OverridableAlphaColorConstraint) {
                it.constrain {
                    color = (color as OverridableAlphaColorConstraint).originalConstraint
                }
            }
        }
    }

    private fun recurseChildren(component: UIComponent, action: (UIComponent) -> Unit) {
        action(component)
        component.children.forEach { recurseChildren(it, action) }
    }

    private class OverridableAlphaColorConstraint(
        val originalConstraint: ColorConstraint,
        private val isOverridden: State<Boolean>,
        private val overriddenAlphaPercentage: State<Float>
    ) : ColorConstraint {
        override var cachedValue: Color = Color.WHITE
        override var constrainTo: UIComponent? = null
        override var recalculate = true

        private var originalAlpha: Int? = null

        init {
            isOverridden.onSetValue {
                recalculate = true
            }

            overriddenAlphaPercentage.onSetValue {
                recalculate = true
            }
        }

        override fun getColorImpl(component: UIComponent): Color {
            val originalColor = originalConstraint.getColorImpl(component)

            if (originalAlpha == null)
                originalAlpha = originalColor.alpha

            if (isOverridden.get())
                return originalColor.withAlpha((originalAlpha!! * overriddenAlphaPercentage.get()).roundToInt().coerceIn(0, 255))
            return originalColor
        }

        override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
            // no-op
        }
    }
}
