package gg.essential.elementa.components.inspector.tabs

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.debug.CycleSafeConstraintDebugger
import gg.essential.elementa.constraints.debug.withDebugger
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import gg.essential.elementa.utils.bindParent
import gg.essential.elementa.utils.not
import gg.essential.elementa.utils.onLeftClick
import org.jetbrains.annotations.ApiStatus
import java.awt.Color

@ApiStatus.Internal
class ValuesTab : InspectorTab("Values") {

    private val grid by UIContainer().constrain {
        x = 5.pixels
        width = ChildBasedMaxSizeConstraint()
        height = ChildBasedSizeConstraint()
    } childOf this

    private val firstRow by UIContainer().constrain {
        width = ChildBasedSizeConstraint()
        height = ChildBasedMaxSizeConstraint()
    } childOf grid

    private val secondRow by UIContainer().constrain {
        y = SiblingConstraint()
        width = ChildBasedSizeConstraint()
        height = ChildBasedMaxSizeConstraint()
    } childOf grid

    private val thirdRow = UIContainer().constrain {
        x = 5.pixels
        y = SiblingConstraint()
        width = ChildBasedSizeConstraint()
        height = ChildBasedMaxSizeConstraint()
    } childOf this

    private val valueList = mutableListOf<Value>()

    private val leftValue by createValue("Left", firstRow) { getLeft() }
    private val topValue by createValue("Top", firstRow) { getTop() }
    private val widthValue by createValue("Width", firstRow) { getWidth() }
    private val rightValue by createValue("Right", secondRow) { getRight() }
    private val bottomValue by createValue("Bottom", secondRow) { getBottom() }
    private val heightValue by createValue("Height", secondRow) { getHeight() }

    private val radiusValueText by createValue("Radius", thirdRow, { getRadius() }, { it == 0f })
    private val textScaleValue by createValue("TextScale", thirdRow, { getTextScale() }, { it == 1f })


    private var useColorHex = true
    private val componentColor = BasicState(Color.WHITE)
    private val colorValueText = Value("Color") {
        val color = getColor()
        componentColor.set(color)
        if (color.rgb == Color.WHITE.rgb) {
            return@Value null
        } else if (useColorHex) {
            if (color.alpha == 255) {
                "0x${Integer.toHexString(color.rgb and (0xFFFFFF)).uppercase()}"
            } else {
                "0x${Integer.toHexString(color.rgb).uppercase()}"
            }

        } else {
            if (color.alpha == 255) {
                "Color(%d, %d, %d)".format(color.red, color.green, color.blue)
            } else {
                "Color(%d, %d, %d, %d)".format(color.red, color.green, color.blue, color.alpha)
            }
        }
    }.apply {
        onLeftClick {
            useColorHex = !useColorHex
        }
        bindParent(thirdRow, !hidden)
        registerCustomComponent(UIBlock().setColor(componentColor.toConstraint()).constrain {
            width = 7.pixels
            height = AspectConstraint()
        })
    }

    private fun createValue(name: String, parent: UIComponent, getter: UIComponent.() -> Float): Value {
        return Value(name, getter).apply {
            bindParent(parent, !hidden)
        }
    }

    private fun createValue(
        name: String,
        parent: UIComponent,
        getter: UIComponent.() -> Float,
        filter: (Float) -> Boolean = { true }
    ): Value {
        return Value(name, getter, filter).apply {
            bindParent(parent, hidden)
        }
    }

    private inner class Value(name: String, private val extractor: UIComponent.() -> String?) : UIContainer() {

        constructor(
            name: String,
            floatExtractor: (UIComponent) -> Float?,
            filter: (Float) -> Boolean = { true },
        ) : this(
            name,
            extractor = {
                val float = floatExtractor(this)
                if (float != null && filter(float)) {
                    float.toString()
                } else {
                    null
                }
            })

        val hidden = BasicState(false)

        private val nameText by UIText("$name: ").constrain {
            width = TextAspectConstraint()
        } childOf this

        private val valueText by UIText("").constrain {
            x = SiblingConstraint()
            width = TextAspectConstraint()
        } childOf this


        init {
            constrain {
                x = ColumnPositionConstraint(10f)
                width = ChildBasedSizeConstraint()
                height = ChildBasedMaxSizeConstraint() + 3.pixels()
            }
            valueList.add(this)
        }

        fun update(component: UIComponent) {
            val value = extractor(component)
            hidden.set(value == null)
            if (value != null) {
                valueText.setText(value)
            }
        }

        fun registerCustomComponent(uiComponent: UIComponent) {
            uiComponent.constrain {
                x = SiblingConstraint()
            } childOf this
        }
    }

    override fun updateWithComponent(component: UIComponent) {
    }

    override fun updateValues() {
        val cachedComponent = targetComponent ?: return
        withDebugger(CycleSafeConstraintDebugger()) {
            valueList.forEach {
                it.update(cachedComponent)
            }
        }
    }
}