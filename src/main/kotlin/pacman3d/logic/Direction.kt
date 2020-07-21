package pacman3d.logic

private typealias DirectionFunc = () -> Direction

sealed class Direction(
        private val str: String,
        val x: Int,
        val y: Int,
        val isHorizontal: Boolean,
        private val oppositeDirectionFunc: DirectionFunc) {

    object UP : Direction("Up", 0, -1, false, { DOWN })
    object DOWN : Direction("Down", 0, 1, false, { UP })
    object LEFT : Direction("Left", -1, 0, true, { RIGHT })
    object RIGHT : Direction("Right", 1, 0, true, { LEFT })

    val isVertical = !isHorizontal

    val oppositeDirection get() = oppositeDirectionFunc()

    infix fun differentDirectionalityThan(other: Direction) = isHorizontal != other.isHorizontal

    override fun toString() = str
}