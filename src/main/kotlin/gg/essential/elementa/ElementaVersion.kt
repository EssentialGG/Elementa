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
    V1,

    /**
     * This Elementa version improves the behavior of mouse input in two ways
     *
     * 1. In Minecraft versions <=1.12.2, the game calculates the position of the mouse input in an integer context
     * relative to scaled pixels. However, Elementa uses real pixels to determine the position of components leading
     * to situations where certain components or parts of components are not clickable due the game truncating the decimal.
     * This Elementa version improves this behavior by restoring the decimal component of mouse clicks to the mouse positions
     * on affected versions.
     *
     * 2. Minecraft mouse click input is positioned in the top left corner of a pixel. As a result, the left and top pixel of
     * a component do not register clicks and components with a width or height of 1 are also not clickable. This Elementa version
     * improves this behavior by moving the click to the center of the pixel.
     */
    V2,

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


        @PublishedApi
        internal var active: ElementaVersion = v0
    }
}