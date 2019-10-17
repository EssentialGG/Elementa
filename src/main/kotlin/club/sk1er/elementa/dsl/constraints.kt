package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.*

fun WidthConstraint.max(maxConstraint: WidthConstraint) = MaxWidthConstraint(this, maxConstraint)
fun WidthConstraint.min(minConstraint: WidthConstraint) = MinWidthConstraint(this, minConstraint)

fun HeightConstraint.max(maxConstraint: HeightConstraint) = MaxHeightConstraint(this, maxConstraint)
fun HeightConstraint.min(minConstraint: HeightConstraint) = MinHeightConstraint(this, minConstraint)

fun WidthConstraint.padWidth(padding: Float) = WidthPaddingConstraint(this, padding)
fun HeightConstraint.padHeight(padding: Float) = HeightPaddingConstraint(this, padding)