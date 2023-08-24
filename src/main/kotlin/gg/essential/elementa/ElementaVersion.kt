package gg.essential.elementa

/**
 * Sometimes it is necessary or desirable to introduce breaking behavioral changes to Elementa. In order to maintain
 * full backwards compatibility in these cases, library consumers must explicitly opt-in to such changes for their
 * [gg.essential.elementa.components.Window]s. This allows Elementa to evolve without breaking mods which rely on old,
 * suboptimal behavior.
 *
 * This opt-in, if supplied to [gg.essential.elementa.components.Window]'s constructor, will only be active during the
 * Window's draw call (or any other affected methods, if extended by a future version).
 * To opt-in to the new behavior outside of these methods, you may use [enableFor].
 */
enum class ElementaVersion {

    /**
     * The initial version of Elementa. This is the default behavior if no opt-in is active.
     */
    @Deprecated(DEPRECATION_MESSAGE)
    V0,

    /**
     * [gg.essential.elementa.components.UIBlock.drawBlock] and the similar methods all starting with `drawBlock` will
     * now always render the block with depth testing enabled and set to always pass. This will result in them always
     * writing their depth to the depth buffer and allows components rendered in front to not be unexpectedly influenced
     * by elements rendered behind them.
     * Additionally they will always reset the depth test state to disabled and the depth test function to LEQUAL before
     * returning (this matches the default state during GUI rendering but may be important in some special use cases).
     */
    @Deprecated(DEPRECATION_MESSAGE)
    V1,

    /**
     * This Elementa version improves the behavior of mouse input in three ways
     *
     * 1. In Minecraft versions <=1.12.2, the game calculates the position of the mouse input in an integer context
     * relative to scaled pixels. However, Elementa uses real pixels to determine the position of components leading
     * to situations where certain components or parts of components are not clickable due the game truncating the fractional part.
     * This Elementa version improves this behavior by restoring the fractional component of mouse clicks to the mouse positions
     * in [gg.essential.elementa.WindowScreen.onMouseClicked] if it is not already present
     *
     * 2. Minecraft mouse click input is positioned in the top left corner of a pixel. As a result, the left and top pixel of
     * a component do not register clicks and components with a width or height of 1 are also not clickable. This Elementa version
     * improves this behavior by offsetting mouse coordinates to the center of the real pixel.
     * In particular, [gg.essential.elementa.components.Window.mouseClick] will add half a real pixel to the passed
     * coordinates and [gg.essential.elementa.UIComponent.getMousePosition] will add half a real pixel to the returned
     * coordinates.
     * As a result, if you have previously relied on the exact distance between a click and a component, beware that
     * this value will now be off by up to half a real pixel compared to what it used to be. If required, you can get
     * back the original real-pixel-aligned value by rounding down to the nearest real pixel via
     * [gg.essential.elementa.utils.guiHint].
     * E.g. if the user clicks on the MC pixel at 3/4 with their GUI scale set to 2, the click used to be processed at
     * 3.0/4.0, but with this change it will appear at 3.25/4.25 (because 0.25 is half a real pixel at scale 2).
     *
     * 3. [gg.essential.elementa.components.Window] will now call the new [gg.essential.elementa.UIComponent.dragMouse]
     * override (the one using Float) instead of the old one (using Int). This allows the drag listeners to receive
     * high quality mouse coordinates (including the two changes above) but it may be breaking if you rely on an
     * override of that method. If you do, then you should switch to using the new override at the same time as you
     * upgrade to the new version (or override both if you need to maintain support for old versions).
     */
    @Deprecated(DEPRECATION_MESSAGE)
    V2,

    /**
     * When there are multiple [gg.essential.elementa.effects.Effect] applied to a single component, their [gg.essential.elementa.effects.Effect.afterDraw]
     * are now called in reverse order, such that they form a stack around the draw itself:
     * `effectA.beforeDraw effectB.beforeDraw component.draw effectB.afterDraw effectA.afterDraw`
     *
     * Prior versions called `effectA.afterDraw` before `effectB.afterDraw` which could result in inproper cleanup when
     * both effects modify the same thing.
     */
    @Deprecated(DEPRECATION_MESSAGE)
    V3,

    /**
     * Mark components as initialized and call [gg.essential.elementa.UIComponent.afterInitialization] during `beforeDraw` instead of `draw`.
     *
     * This ensures that [gg.essential.elementa.effects.Effect.setup] is always called before [gg.essential.elementa.effects.Effect.beforeDraw].
     * On prior versions, calling [gg.essential.elementa.UIComponent.enableEffect] on a component that wasn't yet initialized would result
     * in the Effect's `beforeDraw` being called once before `setup`.
     */
    V4,

    ;

    /**
     * Run the given block of code with the provided opt-in.
     * This method may be used to downgrade the version if required.
     *
     * The current opt-in is restored after this method returns.
     * This method is not thread-safe and may only be used from the main thread. This may change in the future.
     */
    inline fun <T> enableFor(block: () -> T): T {
        val prevVersion = active
        active = this
        try {
            return block()
        } finally {
            active = prevVersion
        }
    }

    companion object {

        private const val DEPRECATION_MESSAGE = """This version of Elementa has been deprecated.
It may still be used but its behavior has been determined to be unexpected, suboptimal or broken in same way.
We therefore recommend you opt-in to a newer version.
Be sure to read through all the changes between your current version and your new version to be able to act in case you are affected by them.
"""

        // These are all for comparing with the active version so we do not have magic constants anywhere and can easily
        // see where a certain version has any effects.
        // Deprecation is suppressed because we do want to keep supporting old versions.
        @Suppress("DEPRECATION")
        internal val v0 = V0
        @Suppress("DEPRECATION")
        internal val v1 = V1
        @Suppress("DEPRECATION")
        internal val v2 = V2
        @Suppress("DEPRECATION")
        internal val v3 = V3
        internal val v4 = V4


        @PublishedApi
        internal var active: ElementaVersion = v0
    }
}