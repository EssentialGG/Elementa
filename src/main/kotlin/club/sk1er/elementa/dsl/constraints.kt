package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.*

fun SizeConstraint.min(minConstraint: SizeConstraint) = MinSizeConstraint(this, minConstraint)
fun SizeConstraint.max(maxConstraint: SizeConstraint) = MaxSizeConstraint(this, maxConstraint)
fun SizeConstraint.minMax(minConstraint: SizeConstraint, maxConstraint: SizeConstraint) = MaxSizeConstraint(MinSizeConstraint(this, minConstraint), maxConstraint)
fun SizeConstraint.sizeMinMax(minConstraint: SizeConstraint, maxConstraint: SizeConstraint) = this.minMax(minConstraint, maxConstraint)

fun PositionConstraint.min(minConstraint: PositionConstraint) = MinPositionConstraint(this, minConstraint)
fun PositionConstraint.max(maxConstraint: PositionConstraint) = MaxPositionConstraint(this, maxConstraint)
fun PositionConstraint.minMax(minConstraint: PositionConstraint, maxConstraint: PositionConstraint) = MaxPositionConstraint(MinPositionConstraint(this, minConstraint), maxConstraint)
fun PositionConstraint.positionMinMax(minConstraint: PositionConstraint, maxConstraint: PositionConstraint) = this.minMax(minConstraint, maxConstraint)

operator fun PositionConstraint.plus(other: PositionConstraint) = AdditivePositionConstraint(this, other)
operator fun SizeConstraint.plus(other: SizeConstraint) = AdditiveSizeConstraint(this, other)

operator fun PositionConstraint.minus(other: PositionConstraint) = SubtractivePositionConstraint(this, other)
operator fun SizeConstraint.minus(other: SizeConstraint) = SubtractiveSizeConstraint(this, other)