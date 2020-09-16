package club.sk1er.elementa.constraints.resolution

import club.sk1er.elementa.constraints.SuperConstraint

class ConstraintValidationException(val history: List<SuperConstraint<*>>) : Exception()