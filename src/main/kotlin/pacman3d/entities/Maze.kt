package pacman3d.entities

import pacman3d.renderables.MazeRenderable
import pacman3d.logic.Position
import pacman3d.maze.MazeConst

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

    fun eatDot(position: Position) {
        this[position] = EMPTY
        dotsLeft--
    }

    companion object {
        const val INVALID       : Byte = 0
        const val EMPTY         : Byte = 1
        const val DOT           : Byte = 2
        const val ENERGIZER     : Byte = 3
        const val GHOST_HOUSE   : Byte = 4

        private const val E = EMPTY
        private const val D = DOT
        private const val R = ENERGIZER
        private const val G = GHOST_HOUSE

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

        inline val Byte.isDot get() = this == DOT
        inline val Byte.isEnergizer get() = this == ENERGIZER
        inline val Byte.isDotOrEnergizer get() = isDot || isEnergizer
    }

    override fun createRenderable() = MazeRenderable()

    override fun update(world: World, time: Double) = Unit
}