package club.sk1er.elementa.constraints

class AspectConstraint(private val value: Float): Constraint() {
    override fun getValue() = value
}