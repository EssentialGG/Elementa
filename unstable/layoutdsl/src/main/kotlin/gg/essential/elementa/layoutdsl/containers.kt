@file:OptIn(ExperimentalContracts::class)

package gg.essential.elementa.layoutdsl

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.WidthConstraint
import gg.essential.elementa.dsl.boundTo
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.coerceAtLeast
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.common.HollowUIContainer
import gg.essential.elementa.common.constraints.AlternateConstraint
import gg.essential.elementa.common.constraints.SpacedCramSiblingConstraint
import gg.essential.elementa.state.v2.*
import gg.essential.universal.UMatrixStack
import java.awt.Color
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun LayoutScope.box(modifier: Modifier = Modifier, block: LayoutScope.() -> Unit = {}): UIComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val container = TransparentBlock().apply {
        componentName = "BoxContainer"
        setWidth(ChildBasedSizeConstraint())
        setHeight(ChildBasedSizeConstraint())
    }
    container.addChildModifier(Modifier.alignHorizontal(Alignment.Center).alignVertical(Alignment.Center))
    return container(modifier = modifier, block = block)
}

fun LayoutScope.row(horizontalArrangement: Arrangement = Arrangement.spacedBy(), verticalAlignment: Alignment = Alignment.Center, block: LayoutScope.() -> Unit): UIComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return row(Modifier, horizontalArrangement, verticalAlignment, block)
}
fun LayoutScope.row(modifier: Modifier, horizontalArrangement: Arrangement = Arrangement.spacedBy(), verticalAlignment: Alignment = Alignment.Center, block: LayoutScope.() -> Unit): UIComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val rowContainer = TransparentBlock().apply {
        componentName = "RowContainer"
        setWidth(ChildBasedSizeConstraint())
        setHeight(ChildBasedMaxSizeConstraint())
    }

    rowContainer.addChildModifier(Modifier.alignVertical(verticalAlignment))

    rowContainer(modifier = modifier, block = block)
    horizontalArrangement.initialize(rowContainer, Axis.HORIZONTAL)

    return rowContainer
}

fun LayoutScope.column(verticalArrangement: Arrangement = Arrangement.spacedBy(), horizontalAlignment: Alignment = Alignment.Center, block: LayoutScope.() -> Unit): UIComponent {
    return column(Modifier, verticalArrangement, horizontalAlignment, block)
}
fun LayoutScope.column(modifier: Modifier, verticalArrangement: Arrangement = Arrangement.spacedBy(), horizontalAlignment: Alignment = Alignment.Center, block: LayoutScope.() -> Unit): UIComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val columnContainer = TransparentBlock().apply {
        componentName = "ColumnContainer"
        setWidth(ChildBasedMaxSizeConstraint())
        setHeight(ChildBasedSizeConstraint())
    }

    columnContainer.addChildModifier(Modifier.alignHorizontal(horizontalAlignment))

    columnContainer(modifier = modifier, block = block)
    verticalArrangement.initialize(columnContainer, Axis.VERTICAL)

    return columnContainer
}

fun LayoutScope.flowContainer(
    modifier: Modifier = Modifier,
    // TODO ideally we can make this use Arrangement on a per-row basis, currently it's just always SpaceBetween
    minSeparation: () -> WidthConstraint = { 0.pixels },
    verticalSeparation: () -> WidthConstraint = { 0.pixels },
    block: LayoutScope.() -> Unit = {},
): UIComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val flowContainer = TransparentBlock().apply {
        componentName = "FlowContainer"
        setHeight(ChildBasedSizeConstraint())
    }

    val childModifier = Modifier
        .then(BasicXModifier { SpacedCramSiblingConstraint(minSeparation(), 0.pixels) })
        .then(BasicYModifier { SpacedCramSiblingConstraint(minSeparation(), 0.pixels, verticalSeparation()) })
    flowContainer.addChildModifier(childModifier)

    flowContainer(modifier = modifier, block = block)

    return flowContainer
}

fun LayoutScope.scrollable(
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    vertical: Boolean = false,
    pixelsPerScroll: Float = 15f,
    block: LayoutScope.() -> Unit = {},
): ScrollComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    if (!horizontal && !vertical) {
        throw IllegalArgumentException("Either `horizontal` or `vertical` or both must be `true`.")
    }

    val outer = ScrollComponent(
        horizontalScrollEnabled = horizontal,
        verticalScrollEnabled = vertical,
        pixelsPerScroll = pixelsPerScroll,
    )
    val inner = outer.children.first()
    // Need an extra wrapper because ScrollComponent does stupid things which breaks padding in the inner component
    val content = HollowUIContainer() childOf outer // actually adds to `inner` because ScrollComponent redirects it

    outer.apply {
        componentName = "scrollable"
        setWidth(ChildBasedSizeConstraint() boundTo content)
        setHeight(ChildBasedSizeConstraint() boundTo content)
    }
    inner.apply {
        componentName = "scrollableInternal"
        setWidth(100.percent boundTo content)
        setHeight(100.percent boundTo content)
    }
    content.apply {
        componentName = "scrollableContent"
        setWidth(AlternateConstraint(ChildBasedSizeConstraint(), 100.percent boundTo outer).coerceAtLeast(AlternateConstraint(100.percent boundTo outer, 0.pixels)))
        setHeight(AlternateConstraint(ChildBasedSizeConstraint(), 100.percent boundTo outer).coerceAtLeast(AlternateConstraint(100.percent boundTo outer, 0.pixels)))
        addChildModifier(Modifier.alignBoth(Alignment.Center))
    }

    outer(modifier = modifier)

    block(LayoutScope(content, this, content))

    return outer
}

fun LayoutScope.floatingBox(
    modifier: Modifier = Modifier,
    floating: State<Boolean> = stateOf(true),
    block: LayoutScope.() -> Unit = {},
): UIComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    fun UIComponent.isMounted(): Boolean =
        parent == this || (this in parent.children && parent.isMounted())

    // Elementa's floating system is quite tricky to work with because components that are floating are added into a
    // persistent list but will not automatically be removed from that list when they're removed from the component
    // tree, and as such will continue to render.
    // This class tries to work around that by canceling `draw` and automatically un-floating itself in such cases,
    // as well as automatically adding itself back to the floating list when it is put back into the component tree.
    class FloatableContainer : UIBlock(Color(0, 0, 0, 0)) {
        val shouldBeFloating: Boolean
            get() = floating.get()

        // Keeps track of the current floating state because the parent field of the same name is private
        @set:JvmName("setFloating_")
        var isFloating: Boolean = false
            set(value) {
                if (field == value) return
                field = value
                setFloating(value)
            }

        override fun animationFrame() {
            // animationFrame is called from the regular tree traversal, so it's safe to directly update the floating
            // list from here
            isFloating = shouldBeFloating

            super.animationFrame()
        }

        override fun draw(matrixStack: UMatrixStack) {
            // If we're no longer mounted in the component tree, we should no longer draw
            if (!isMounted()) {
                // and if we're still floating (likely the case because that'll be why we're still drawing), then
                // we also need to un-float ourselves
                if (isFloating) {
                    // since this is likely called from the code that iterates over the floating list to draw each
                    // component, modifying the floating list here would result in a CME, so we need to delay this.
                    Window.enqueueRenderOperation {
                        // Note: we must not assume that our shouldBe state hasn't changed since we scheduled this
                        isFloating = shouldBeFloating && isMounted()
                    }
                }
                return
            }

            // If we should be floating but aren't right now, then this isn't being called from the floating draw loop
            // and it should be safe for us to immediately set us as floating.
            // Doing so will add us to the floating draw loop and thereby allow us to draw later.
            if (shouldBeFloating && !isFloating) {
                isFloating = true
                return
            }

            // If we should not be floating but are right now, then this is similar to the no-longer-mounted case above
            // i.e. we want to un-float ourselves.
            // Except we're still mounted so we do still want to draw the content (this means it'll be floating for one
            // more frame than it's supposed to but there isn't anything we can really do about that because the regular
            // draw loop has already concluded by this point).
            if (!shouldBeFloating && isFloating) {
                Window.enqueueRenderOperation { isFloating = shouldBeFloating }
                super.draw(matrixStack)
                return
            }

            // All as it should be, can just draw it
            super.draw(matrixStack)
        }
    }

    val container = FloatableContainer().apply {
        componentName = "floatingBox"
        setWidth(ChildBasedSizeConstraint())
        setHeight(ChildBasedSizeConstraint())
    }
    container.addChildModifier(Modifier.alignBoth(Alignment.Center))
    return container(modifier = modifier, block = block)
}
