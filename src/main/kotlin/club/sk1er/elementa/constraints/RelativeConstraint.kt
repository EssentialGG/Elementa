package club.sk1er.elementa.constraints

class RelativeConstraint(private val value: Float): Constraint() {
    override fun getValue() = value
}