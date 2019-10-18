package club.sk1er.elementa.dsl

import club.sk1er.elementa.constraints.*

fun SizeConstraint.max(maxConstraint: SizeConstraint) = MaxSizeConstraint(this, maxConstraint)
fun SizeConstraint.min(minConstraint: SizeConstraint) = MinSizeConstraint(this, minConstraint)

operator fun PositionConstraint.plus(other: PositionConstraint) = AdditivePositionConstraint(this, other)
operator fun SizeConstraint.plus(other: SizeConstraint) = AdditiveSizeConstraint(this, other)

operator fun PositionConstraint.minus(other: PositionConstraint) = SubtractivePositionConstraint(this, other)
operator fun SizeConstraint.minus(other: SizeConstraint) = SubtractiveSizeConstraint(this, other)