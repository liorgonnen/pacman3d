package pacman3d.logic

private typealias DirectionFunc = () -> Direction

sealed class Direction(
        private val str: String,
        val ordinal: Int,
        val multiplier: Int,
        val x: Int,
        val y: Int,
        val isHorizontal: Boolean,
        private val oppositeDirectionFunc: DirectionFunc,
        private val leftFunc: DirectionFunc,
        private val rightFunc: DirectionFunc) {

    object UP : Direction("Up", 0, -1, 0, -1, false, { DOWN }, { LEFT }, { RIGHT })
    object DOWN : Direction("Down", 1, 1, 0, 1, false, { UP }, { RIGHT }, { LEFT })
    object LEFT : Direction("Left", 2, -1, -1, 0, true, { RIGHT }, { DOWN }, { UP })
    object RIGHT : Direction("Right", 3, 1, 1, 0, true, { LEFT }, { UP }, { DOWN })

    val oppositeDirection get() = oppositeDirectionFunc()

    val left get() = leftFunc()

    val right get() = rightFunc()

    override fun toString(): String = str
}