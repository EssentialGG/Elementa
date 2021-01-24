package club.sk1er.elementa.effects

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.constraints.ColorConstraint
import club.sk1er.elementa.constraints.ConstraintType
import club.sk1er.elementa.constraints.resolution.ConstraintVisitor
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.effects.Effect
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import club.sk1er.elementa.utils.withAlpha
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
