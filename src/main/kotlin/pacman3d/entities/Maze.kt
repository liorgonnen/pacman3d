package pacman3d.entities

import org.w3c.dom.ValidityState
import pacman3d.logic.Direction
import pacman3d.renderables.MazeRenderable
import pacman3d.logic.Position
import pacman3d.maze.MazeConst
import kotlin.experimental.and
import kotlin.experimental.or

class Maze : AbsGameEntity<MazeRenderable>() {

    private val state = DEFAULT_LAYOUT.copyOf()

    var dotsLeft = state.count { it.isDotOrEnergizer }

    operator fun get(x: Int, y: Int): Byte = state[MazeConst.indexOf(x, y)]
    operator fun get(x: Double, y: Double): Byte = state[MazeConst.indexOf(x.toInt(), y.toInt())]
    operator fun get(pos: Position): Byte = state[pos.mazeIndex]
    operator fun set(pos: Position, value: Byte) { state[pos.mazeIndex] = value }

    fun forEachTile(func: (state: Maze, x: Int, y: Int) -> Unit) {
        for (y in MazeConst.FIRST_EFFECTIVE_LINE until MazeConst.LAST_EFFECTIVE_LINE)
            for (x in 0 until MazeConst.WIDTH_UNITS)
                func(this, x, y)
    }

    fun isIntersection(position: Position) = isIntersection(position.mazeX, position.mazeY)

    fun isIntersection(x: Int, y: Int)
        = this[x, y].isValid && (this[x - 1, y].isValid || this[x + 1, y].isValid || this[x, y - 1].isValid || this[x, y + 1].isValid)

    fun isIntersection(position: Position, direction: Direction)
        = isIntersection(position.mazeX + direction.x, position.mazeY + direction.y)

    fun onDotEaten(position: Position) {
        this[position] = EMPTY
        dotsLeft--
    }

    companion object {
        const val VALID         : Byte = 0x01
        const val EMPTY         : Byte = 0x01
        const val DOT           : Byte = 0x02
        const val ENERGIZER     : Byte = 0x04
        const val GHOST_HOUSE   : Byte = 0x08

        private val E = EMPTY
        private val D = DOT or VALID
        private val R = ENERGIZER or VALID
        private val G = GHOST_HOUSE or VALID

        private val DEFAULT_LAYOUT = byteArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0,
                0, 0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0, 0,
                0, 0, R, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, R, 0, 0,
                0, 0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0, 0,
                0, 0, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0,
                0, 0, D, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, D, 0, 0,
                0, 0, D, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, D, 0, 0,
                0, 0, D, D, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, D, D, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, E, 0, 0, E, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, E, 0, 0, E, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, E, E, E, E, E, E, E, E, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, G, G, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, G, G, G, G, G, G, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                E, E, E, E, E, E, E, D, E, E, E, 0, G, G, G, G, G, G, 0, E, E, E, D, E, E, E, E, E, E, E,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, G, G, G, G, G, G, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, 0, 0, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, E, E, E, E, E, E, E, E, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, 0, 0, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, D, 0, 0, E, 0, 0, 0, 0, 0, 0, 0, 0, E, 0, 0, D, 0, 0, 0, 0, 0, 0, 0,
                0, 0, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0,
                0, 0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0, 0,
                0, 0, D, 0, 0, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, D, 0, 0, 0, 0, D, 0, 0,
                0, 0, R, D, D, 0, 0, D, D, D, D, D, D, D, E, E, D, D, D, D, D, D, D, 0, 0, D, D, R, 0, 0,
                0, 0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0, 0,
                0, 0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, D, 0, 0, 0, 0,
                0, 0, D, D, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, 0, 0, D, D, D, D, D, D, 0, 0,
                0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0,
                0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0, D, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, D, 0, 0,
                0, 0, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, D, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        )

        inline val Byte.isDot get() = (this and DOT) == DOT
        inline val Byte.isEnergizer get() = (this and ENERGIZER) == ENERGIZER
        inline val Byte.isDotOrEnergizer get() = isDot || isEnergizer
        inline val Byte.isValid get() = (this and VALID) == VALID
        inline val Byte.isGhostHouse get() = (this and GHOST_HOUSE) == GHOST_HOUSE
    }

    override fun createRenderable() = MazeRenderable()
}