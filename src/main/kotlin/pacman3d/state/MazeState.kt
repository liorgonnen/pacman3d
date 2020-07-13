package pacman3d.state

import pacman3d.maze.Maze
import pacman3d.maze.Maze.DOT
import pacman3d.maze.Maze.INVALID
import pacman3d.maze.Maze.PILL

class MazeState {

    private inline val Byte.isDot get() = this == DOT
    private inline val Byte.isPill get() = this == PILL
    private inline val Byte.isDotOrPill get() = isDot || isPill

    private val state = Maze.createDefaultState()

    var dotsLeft = state.count { it.isDotOrPill }
        private set

    private fun stateAt(x: Int, y: Int) = state[y * Maze.WIDTH_UNITS + x]

    fun isValid(x: Int, y: Int) = stateAt(x, y) != INVALID

    fun isDotOrPill(x: Int, y: Int) = stateAt(x, y).isDotOrPill

    fun isDot(x: Int, y: Int) = stateAt(x, y).isDot

    fun isPill(x: Int, y: Int) = stateAt(x, y).isPill

    fun forEachTile(func: (state: MazeState, x: Int, y: Int) -> Unit) {
        for (y in Maze.FIRST_EFFECTIVE_LINE until Maze.LAST_EFFECTIVE_LINE)
            for (x in 0 until Maze.WIDTH_UNITS)
                func(this, x, y)
    }
}