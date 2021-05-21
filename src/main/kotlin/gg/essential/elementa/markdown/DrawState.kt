package gg.essential.elementa.markdown

/**
 * Stores universal state necessary for rendering all drawables.
 *
 * The first time we layout the markdown tree (in MarkdownComponent),
 * we lay everything out based upon the current x and y values of
 * the MarkdownComponent. At this time, however, we do not yet know
 * the height of the component (we need to layout all of the drawables
 * to determine their height first). After this first layout, we set
 * the height of the MarkdownComponent based on the heights and
 * positions of the drawables.
 *
 * When we set this height, the y location of the MarkdownComponent
 * may change (for example, if its y constraint is a CenterConstraint).
 * So when we change the height, we would have to re-rendering the
 * entire tree again to update the positions. It is possible that this
 * may actually be a never-ending process (i.e. changing the height
 * changes the position, and changing the position changes the height).
 *
 * To avoid all of the re-laying-out, we layout the first time and use
 * those initial position values as a "base position" for all of the
 * drawables. When MarkdownComponent#draw is called, it subtracts its
 * current position from those base positions, stores those in this
 * class as shift components, and passes that to all of the drawables.
 * Those drawables then offset any drawing they do by these shift
 * values.
 */
data class DrawState(
    val xShift: Float,
    val yShift: Float
)
