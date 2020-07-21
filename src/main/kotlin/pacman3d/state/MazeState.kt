package pacman3d.state

import pacman3d.logic.ActorPosition
import pacman3d.logic.Direction
import pacman3d.maze.Maze
import pacman3d.maze.MazeCoordinates

class MazeState {

    private val state = DEFAULT_LAYOUT.copyOf()

    var dotsLeft = state.count { it.isDotOrPill }
        private set

    operator fun get(x: Int, y: Int): Byte = state[Maze.indexOf(x, y)]
    operator fun get(pos: ActorPosition): Byte = state[pos.mazeIndex]
    operator fun get(pos: MazeCoordinates): Byte = this[pos.x, pos.y]
    operator fun set(pos: ActorPosition, value: Byte) { state[pos.mazeIndex] = value }

    fun forEachTile(func: (state: MazeState, x: Int, y: Int) -> Unit) {
        for (y in Maze.FIRST_EFFECTIVE_LINE until Maze.LAST_EFFECTIVE_LINE)
            for (x in 0 until Maze.WIDTH_UNITS)
                func(this, x, y)
    }

    fun isTileValidInDirection(position: ActorPosition, direction: Direction)
        = this[position.mazeX + direction.x, position.mazeY + direction.y].isValid

    // TODO: Simplify this or clean this up.
    fun isAllowedToTurn(position: ActorPosition, direction: Direction, threshold: Double): Boolean {
        var result = true
        with(position) {
            if (direction.isVertical) {
                val f = x - mazeX
                result = result && get(mazeX, mazeY + direction.y).isValid
                if (f < 0.5 - threshold) result = result && get(mazeX - 1, mazeY + direction.y).isValid
                if (f > 0.5 + threshold) result = result && get(mazeX + 1, mazeY + direction.y).isValid
            }
            else {
                val f = y - mazeY
                result = result && get(mazeX + direction.x, mazeY).isValid
                if (f < 0.5 - threshold) result = result && get(mazeX + direction.x, mazeY - 1).isValid
                if (f > 0.5 + threshold) result = result && get(mazeX + direction.x, mazeY + 1).isValid
            }
        }

        return result
    }

    companion object {
        const val INVALID   : Byte = 0
        const val EMPTY     : Byte = 1
        const val DOT       : Byte = 2
        const val PILL      : Byte = 3
        const val GATE      : Byte = 4

        private const val E = EMPTY
        private const val D = DOT
        private const val P = PILL
        private const val G = GATE

        private val DEFAULT_LAYOUT = byteArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0, D, D, D, D, D, D, D, D, D, D, D, D, 0,
                0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0,
                0, P, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, P, 0,
                0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0,
                0, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, 0,
                0, D, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, D, 0,
                0, D, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, D, 0,
                0, D, D, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, D, D, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, E, 0, 0, E, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, E, 0, 0, E, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, E, E, E, E, E, E, E, E, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, G, G, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, E, E, E, E, E, E, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                E, E, E, E, E, E, D, E, E, E, 0, E, E, E, E, E, E, 0, E, E, E, D, E, E, E, E, E, E,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, E, E, E, E, E, E, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, 0, 0, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, E, E, E, E, E, E, E, E, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, 0, 0, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, 0, 0, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0,
                0, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0, D, D, D, D, D, D, D, D, D, D, D, D, 0,
                0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0,
                0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0,
                0, P, D, D, 0, 0, D, D, D, D, D, D, D, E, E, D, D, D, D, D, D, D, 0, 0, D, D, P, 0,
                0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0,
                0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0,
                0, D, D, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, D, D, 0,
                0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0,
                0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0,
                0, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        )

        inline val Byte.isDot get() = this == DOT
        inline val Byte.isPill get() = this == PILL
        inline val Byte.isDotOrPill get() = isDot || isPill
        inline val Byte.isValid get() = this != INVALID
    }
}