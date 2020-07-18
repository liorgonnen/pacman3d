package pacman3d.logic

private typealias DirectionFunc = () -> Direction

sealed class Direction(
        private val str: String,
        val ordinal: Int,
        val multiplier: Int,
        val x: Int,
        val y: Int,
        val isHorizontal: Boolean,
        private val oppositeDirectionFunc: DirectionFunc) {

    object UP : Direction("Up", 0, -1, 0, -1, false, { DOWN })
    object DOWN : Direction("Down", 1, 1, 0, 1, false, { UP })
    object LEFT : Direction("Left", 2, -1, -1, 0, true, { RIGHT })
    object RIGHT : Direction("Right", 3, 1, 1, 0, true, { LEFT })

    val oppositeDirection get() = oppositeDirectionFunc()

    override fun toString() = str
}