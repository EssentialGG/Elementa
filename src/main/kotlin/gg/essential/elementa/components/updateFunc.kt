package gg.essential.elementa.components

/**
 * Called once at the start of every frame to update any animations and miscellaneous state.
 *
 * @param dt Time (in seconds) since last frame
 * @param dtMs Time (in milliseconds) since last frame
 *
 * This differs from `(dt / 1000).toInt()` in that it will account for the fractional milliseconds which would
 * otherwise be lost to rounding. E.g. if there are three frames each lasting 16.4ms,
 * `(dt / 1000).toInt()` would be 16 each time, but `dtMs` will be 16 on the first two frames and 17 on the third.
 */
typealias UpdateFunc = (dt: Float, dtMs: Int) -> Unit

internal val NOP_UPDATE_FUNC: UpdateFunc = { _, _ -> }

internal class NopUpdateFuncList(override val size: Int) : AbstractList<UpdateFunc>() {
    override fun get(index: Int): UpdateFunc = NOP_UPDATE_FUNC
}
